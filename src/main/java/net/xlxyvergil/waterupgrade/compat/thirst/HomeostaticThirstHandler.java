package net.xlxyvergil.waterupgrade.compat.thirst;

import homeostatic.common.item.DrinkableItemManager;
import homeostatic.common.water.WaterInfo;
import homeostatic.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HomeostaticThirstHandler implements IThirstHandler {
	@Override
	public int getThirst(Player player) {
		return Services.PLATFORM.getWaterCapabilty(player)
				.map(cap -> cap.getWaterLevel())
				.orElse(WaterInfo.MAX_WATER_LEVEL);
	}

	@Override
	public int getMaxThirst(Player player) {
		return WaterInfo.MAX_WATER_LEVEL;
	}

	@Override
	public boolean canDrink(Player player) {
		return Services.PLATFORM.getWaterCapabilty(player)
				.map(cap -> cap.getWaterLevel() < WaterInfo.MAX_WATER_LEVEL)
				.orElse(false);
	}

	@Override
	public boolean isDrinkableItem(ItemStack stack) {
		return DrinkableItemManager.get(stack) != null;
	}

	@Override
	public boolean isThirstEnabled() {
		return true;
	}

	@Override
	public boolean consumeDrinkItem(Player player, ItemStack stack) {
		homeostatic.common.item.DrinkableItem drinkable = DrinkableItemManager.get(stack);
		if (drinkable == null) return false;
		Services.PLATFORM.getWaterCapabilty(player).ifPresent(cap -> {
			cap.increaseWaterLevel(drinkable.amount());
			cap.increaseSaturationLevel(drinkable.saturation());
		});
		return true;
	}

	@Override
	public int getConsumableHydration(ItemStack stack) {
		homeostatic.common.item.DrinkableItem drinkable = DrinkableItemManager.get(stack);
		return drinkable != null ? drinkable.amount() : 0;
	}
}
