package net.xlxyvergil.waterupgrade.compat.thirst;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sfiomn.legendarysurvivaloverhaul.api.data.json.JsonThirstConsumable;
import sfiomn.legendarysurvivaloverhaul.api.data.manager.ThirstDataManager;
import sfiomn.legendarysurvivaloverhaul.api.thirst.ThirstUtil;
import sfiomn.legendarysurvivaloverhaul.util.CapabilityUtil;

public class LsoThirstHandler implements IThirstHandler {
	@Override
	public int getThirst(Player player) {
		return CapabilityUtil.getThirstCapability(player).getHydrationLevel();
	}

	@Override
	public int getMaxThirst(Player player) {
		return 20;
	}

	@Override
	public boolean canDrink(Player player) {
		return !CapabilityUtil.getThirstCapability(player).isHydrationLevelAtMax();
	}

	@Override
	public boolean isDrinkableItem(ItemStack stack) {
		return ThirstDataManager.getConsumable(stack) != null;
	}

	@Override
	public boolean isThirstEnabled() {
		return true;
	}

	@Override
	public boolean consumeDrinkItem(Player player, ItemStack stack) {
		JsonThirstConsumable consumable = ThirstDataManager.getConsumable(stack);
		if (consumable == null) return false;
		ThirstUtil.takeDrink(player, stack);
		return true;
	}

	@Override
	public int getConsumableHydration(ItemStack stack) {
		JsonThirstConsumable consumable = ThirstDataManager.getConsumable(stack);
		return consumable != null ? consumable.hydration : 0;
	}
}
