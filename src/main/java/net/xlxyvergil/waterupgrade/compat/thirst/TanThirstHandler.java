package net.xlxyvergil.waterupgrade.compat.thirst;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.init.ModTags;

public class TanThirstHandler implements IThirstHandler {
	@Override
	public int getThirst(Player player) {
		return ThirstHelper.getThirst(player).getThirst();
	}

	@Override
	public int getMaxThirst(Player player) {
		return 20;
	}

	@Override
	public boolean canDrink(Player player) {
		return ThirstHelper.canDrink(player, true);
	}

	@Override
	public boolean isDrinkableItem(ItemStack stack) {
		return stack.is(ModTags.Items.DRINKS);
	}

	@Override
	public boolean isThirstEnabled() {
		return ThirstHelper.isThirstEnabled();
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
		return ModTags.Items.getThirstRestored(stack);
	}
}
