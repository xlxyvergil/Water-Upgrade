package net.xlxyvergil.waterupgrade.compat.thirst;

import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ThirstModHandler implements IThirstHandler {
	@Override
	public int getThirst(Player player) {
		return player.getCapability(ModCapabilities.PLAYER_THIRST)
				.map(cap -> cap.getThirst())
				.orElse(20);
	}

	@Override
	public int getMaxThirst(Player player) {
		return 20;
	}

	@Override
	public boolean canDrink(Player player) {
		return player.getCapability(ModCapabilities.PLAYER_THIRST)
				.map(cap -> cap.getThirst() < 20)
				.orElse(false);
	}

	@Override
	public boolean isDrinkableItem(ItemStack stack) {
		return ThirstHelper.itemRestoresThirst(stack);
	}

	@Override
	public boolean isThirstEnabled() {
		return true;
	}

	@Override
	public boolean consumeDrinkItem(Player player, ItemStack stack) {
		ItemStack mainHandItem = player.getMainHandItem();
		player.getInventory().items.set(player.getInventory().selected, stack);
		boolean consumed = stack.use(player.level(), player, InteractionHand.MAIN_HAND).getResult() == InteractionResult.CONSUME;
		player.getInventory().items.set(player.getInventory().selected, mainHandItem);
		return consumed;
	}

	@Override
	public int getConsumableHydration(ItemStack stack) {
		return ThirstHelper.getThirst(stack);
	}
}
