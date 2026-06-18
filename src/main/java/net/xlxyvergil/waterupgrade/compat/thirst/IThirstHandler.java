package net.xlxyvergil.waterupgrade.compat.thirst;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IThirstHandler {
	int getThirst(Player player);

	int getMaxThirst(Player player);

	boolean canDrink(Player player);

	boolean isDrinkableItem(ItemStack stack);

	boolean isThirstEnabled();

	/**
	 * Consume a drink item and apply its thirst/hydration effect to the player.
	 * @return true if the item was successfully consumed
	 */
	boolean consumeDrinkItem(Player player, ItemStack stack);

	/**
	 * Get the hydration amount restored by the given stack.
	 * @return hydration value, or 0 if not a drinkable item
	 */
	int getConsumableHydration(ItemStack stack);

	/**
	 * Whether this handler already fires LivingEntityUseItemEvent.Finish in consumeDrinkItem.
	 * If true, DrinkingUpgradeWrapper will not fire it again.
	 */
	default boolean handlesFinishEvent() {
		return false;
	}
}
