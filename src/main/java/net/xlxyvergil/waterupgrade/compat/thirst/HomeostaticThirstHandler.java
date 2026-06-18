package net.xlxyvergil.waterupgrade.compat.thirst;

import homeostatic.common.Hydration;
import homeostatic.common.water.WaterInfo;
import homeostatic.platform.Services;
import homeostatic.util.WaterHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

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
		return WaterHelper.getItemHydration(stack) != null;
	}

	@Override
	public boolean isThirstEnabled() {
		return true;
	}

	@Override
	public boolean consumeDrinkItem(Player player, ItemStack stack) {
		Hydration hydration = WaterHelper.getItemHydration(stack);
		if (hydration == null) return false;
		// 手动触发 Finish 事件，让 Homeostatic 自己的 onFinishUsingItem 处理恢复
		MinecraftForge.EVENT_BUS.post(
				new LivingEntityUseItemEvent.Finish(player, stack, 32, stack.copy()));
		return true;
	}

	@Override
	public int getConsumableHydration(ItemStack stack) {
		Hydration hydration = WaterHelper.getItemHydration(stack);
		return hydration != null ? hydration.amount() : 0;
	}

	@Override
	public boolean handlesFinishEvent() {
		return true;
	}
}
