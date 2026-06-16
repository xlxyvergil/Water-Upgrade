package net.xlxyvergil.waterupgrade.upgrades.drinking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.ForgeEventFactory;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.ITrackedContentsItemHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IFilteredUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import net.p3pp3rf1y.sophisticatedcore.util.InventoryHelper;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import net.xlxyvergil.waterupgrade.compat.thirst.IThirstHandler;
import net.xlxyvergil.waterupgrade.compat.thirst.ThirstHandlerRegistry;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DrinkingUpgradeWrapper extends UpgradeWrapperBase<DrinkingUpgradeWrapper, DrinkingUpgradeItem> implements ITickableUpgrade, IFilteredUpgrade {
	private static final int COOLDOWN = 100;
	private static final int STILL_THIRSTY_COOLDOWN = 10;
	private static final int DRINKING_RANGE = 3;
	private final FilterLogic filterLogic;

	public DrinkingUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
		super(storageWrapper, upgrade, upgradeSaveHandler);
		filterLogic = new FilterLogic(upgrade, upgradeSaveHandler, upgradeItem.getFilterSlotCount(),
				stack -> ThirstHandlerRegistry.getHandler().isDrinkableItem(stack));
	}

	@Override
	public void tick(@Nullable Entity entity, Level level, BlockPos pos) {
		if (isInCooldown(level) || (entity != null && !(entity instanceof Player))) {
			return;
		}

		IThirstHandler handler = ThirstHandlerRegistry.getHandler();
		if (handler == null || !handler.isThirstEnabled()) {
			return;
		}

		boolean thirstyPlayer = false;
		if (entity == null) {
			AtomicBoolean stillThirstyPlayer = new AtomicBoolean(false);
			level.getEntities(EntityType.PLAYER, new AABB(pos).inflate(DRINKING_RANGE), p -> true)
					.forEach(p -> stillThirstyPlayer.set(stillThirstyPlayer.get() || drinkPlayerAndGetThirsty(p, level, handler)));
			thirstyPlayer = stillThirstyPlayer.get();
		} else {
			if (drinkPlayerAndGetThirsty((Player) entity, level, handler)) {
				thirstyPlayer = true;
			}
		}
		if (thirstyPlayer) {
			setCooldown(level, STILL_THIRSTY_COOLDOWN);
			return;
		}

		setCooldown(level, COOLDOWN);
	}

	private boolean drinkPlayerAndGetThirsty(Player player, Level level, IThirstHandler handler) {
		int thirstLevel = handler.getMaxThirst(player) - handler.getThirst(player);
		if (thirstLevel == 0) {
			return false;
		}
		return tryDrinkingFromStorage(level, thirstLevel, player, handler) && handler.canDrink(player);
	}

	private boolean tryDrinkingFromStorage(Level level, int thirstLevel, Player player, IThirstHandler handler) {
		ITrackedContentsItemHandler inventory = storageWrapper.getInventoryForUpgradeProcessing();
		return InventoryHelper.iterate(inventory, (slot, stack) -> tryDrinkingStack(level, thirstLevel, player, slot, stack, inventory), () -> false, ret -> ret);
	}

	private boolean tryDrinkingStack(Level level, int thirstLevel, Player player, Integer slot, ItemStack stack, ITrackedContentsItemHandler inventory) {
		IThirstHandler handler = ThirstHandlerRegistry.getHandler();
		if (handler == null || !handler.isDrinkableItem(stack) || !filterLogic.matchesFilter(stack)) {
			return false;
		}

		boolean isHurt = player.getHealth() < player.getMaxHealth() - 0.1F;
		if (!isThirstyEnoughForDrink(thirstLevel, stack, handler) && !(shouldDrinkImmediatelyWhenHurt() && thirstLevel > 0 && isHurt)) {
			return false;
		}

		ItemStack singleItemCopy = stack.copy();
		singleItemCopy.setCount(1);

		if (!handler.consumeDrinkItem(player, singleItemCopy)) {
			return false;
		}

		stack.shrink(1);
		inventory.setStackInSlot(slot, stack);

		ItemStack resultItem = ForgeEventFactory.onItemUseFinish(player, singleItemCopy.copy(), 0, singleItemCopy.getItem().finishUsingItem(singleItemCopy, level, player));
		if (!resultItem.isEmpty()) {
			ItemStack insertResult = inventory.insertItem(resultItem, false);
			if (!insertResult.isEmpty()) {
				player.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(playerInventory ->
						InventoryHelper.insertOrDropItem(player, insertResult, playerInventory));
			}
		}

		return true;
	}

	private boolean isThirstyEnoughForDrink(int thirstLevel, ItemStack stack, IThirstHandler handler) {
		ThirstLevel drinkAtThirstLevel = getDrinkAtThirstLevel();
		if (drinkAtThirstLevel == ThirstLevel.ANY) {
			return true;
		}

		int hydration = handler.getConsumableHydration(stack);
		if (hydration <= 0) {
			return false;
		}
		return (drinkAtThirstLevel == ThirstLevel.HALF ? (hydration / 2) : hydration) <= thirstLevel;
	}

	@Override
	public FilterLogic getFilterLogic() {
		return filterLogic;
	}

	public ThirstLevel getDrinkAtThirstLevel() {
		return NBTHelper.getEnumConstant(upgrade, "drinkAtThirstLevel", ThirstLevel::fromName).orElse(ThirstLevel.HALF);
	}

	public void setDrinkAtThirstLevel(ThirstLevel thirstLevel) {
		NBTHelper.setEnumConstant(upgrade, "drinkAtThirstLevel", thirstLevel);
		save();
	}

	public boolean shouldDrinkImmediatelyWhenHurt() {
		return NBTHelper.getBoolean(upgrade, "drinkImmediatelyWhenHurt").orElse(true);
	}

	public void setDrinkImmediatelyWhenHurt(boolean drinkImmediatelyWhenHurt) {
		NBTHelper.setBoolean(upgrade, "drinkImmediatelyWhenHurt", drinkImmediatelyWhenHurt);
		save();
	}
}
