package net.xlxyvergil.waterupgrade.compat.thirst;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import toughasnails.api.thirst.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.init.ModTags;

public class TanThirstHandler implements IThirstHandler {

	private static MobEffect THIRST_EFFECT;

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
		int thirstRestored = ModTags.Items.getThirstRestored(stack);
		if (thirstRestored <= 0) return false;

		IThirst thirst = ThirstHelper.getThirst(player);
		// 直接调用 TAN 公开 API，不使用事件总线
		// 事件链在背包 tick 上下文中不可靠
		thirst.addThirst(thirstRestored);
		thirst.addHydration(getHydrationValue(stack));

		float poisonChance = getPoisonChance(stack);
		if (player.level().random.nextFloat() < poisonChance) {
			player.addEffect(new MobEffectInstance(getThirstEffect(), 600));
		}

		return true;
	}

	@Override
	public boolean handlesFinishEvent() {
		return true;
	}

	@Override
	public int getConsumableHydration(ItemStack stack) {
		return ModTags.Items.getThirstRestored(stack);
	}

	// 以下方法与 TAN ThirstHandler.onItemUseFinish 第 164-188 行完全一致
	// 复制是因为 TAN 没有公开 hydration/poison_chance 查询 API

	private static float getHydrationValue(ItemStack stack) {
		if (stack.is(ModTags.Items.ONE_HUNDRED_HYDRATION_DRINKS)) return 1.0F;
		if (stack.is(ModTags.Items.NINETY_HYDRATION_DRINKS)) return 0.9F;
		if (stack.is(ModTags.Items.EIGHTY_HYDRATION_DRINKS)) return 0.8F;
		if (stack.is(ModTags.Items.SEVENTY_HYDRATION_DRINKS)) return 0.7F;
		if (stack.is(ModTags.Items.SIXTY_HYDRATION_DRINKS)) return 0.6F;
		if (stack.is(ModTags.Items.FIFTY_HYDRATION_DRINKS)) return 0.5F;
		if (stack.is(ModTags.Items.FOURTY_HYDRATION_DRINKS)) return 0.4F;
		if (stack.is(ModTags.Items.THIRTY_HYDRATION_DRINKS)) return 0.3F;
		if (stack.is(ModTags.Items.TWENTY_HYDRATION_DRINKS)) return 0.2F;
		if (stack.is(ModTags.Items.TEN_HYDRATION_DRINKS)) return 0.1F;
		return 0.0F;
	}

	private static float getPoisonChance(ItemStack stack) {
		if (stack.is(ModTags.Items.ONE_HUNDRED_POISON_CHANCE_DRINKS)) return 1.0F;
		if (stack.is(ModTags.Items.SEVENTY_FIVE_POISON_CHANCE_DRINKS)) return 0.75F;
		if (stack.is(ModTags.Items.FIFTY_POISON_CHANCE_DRINKS)) return 0.5F;
		if (stack.is(ModTags.Items.TWENTY_FIVE_POISON_CHANCE_DRINKS)) return 0.25F;
		return 0.0F;
	}

	private static MobEffect getThirstEffect() {
		if (THIRST_EFFECT == null) {
			THIRST_EFFECT = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation("toughasnails", "thirst"));
		}
		return THIRST_EFFECT;
	}
}
