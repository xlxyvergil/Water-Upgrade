package net.xlxyvergil.waterupgrade.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeGuiManager;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.xlxyvergil.waterupgrade.Config;
import net.xlxyvergil.waterupgrade.WaterUpgrade;
import net.xlxyvergil.waterupgrade.upgrades.drinking.*;

public class ModItems {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WaterUpgrade.MODID);

	// SophisticatedBackpacks creative tab key
	private static final ResourceKey<CreativeModeTab> SBP_CREATIVE_TAB =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation("sophisticatedbackpacks", "main"));

	public static final RegistryObject<DrinkingUpgradeItem> DRINKING_UPGRADE = ITEMS.register("drinking_upgrade",
			() -> new DrinkingUpgradeItem(Config.SERVER.drinkingUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final RegistryObject<DrinkingUpgradeItem> ADVANCED_DRINKING_UPGRADE = ITEMS.register("advanced_drinking_upgrade",
			() -> new DrinkingUpgradeItem(Config.SERVER.advancedDrinkingUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));

	private static final UpgradeContainerType<DrinkingUpgradeWrapper, DrinkingUpgradeContainer> DRINKING_TYPE =
			new UpgradeContainerType<>(DrinkingUpgradeContainer::new);
	private static final UpgradeContainerType<DrinkingUpgradeWrapper, DrinkingUpgradeContainer> ADVANCED_DRINKING_TYPE =
			new UpgradeContainerType<>(DrinkingUpgradeContainer::new);

	public static void init(IEventBus modBus) {
		ITEMS.register(modBus);
		modBus.addListener(ModItems::registerContainers);
		modBus.addListener(ModItems::addToCreativeTab);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modBus.addListener(ModItems::registerTabsClient));
	}

	private static void registerContainers(net.minecraftforge.registries.RegisterEvent event) {
		if (!event.getRegistryKey().equals(ForgeRegistries.Keys.MENU_TYPES)) {
			return;
		}
		UpgradeContainerRegistry.register(DRINKING_UPGRADE.getId(), DRINKING_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_DRINKING_UPGRADE.getId(), ADVANCED_DRINKING_TYPE);
	}

	private static void registerTabsClient(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
		UpgradeGuiManager.registerTab(DRINKING_TYPE, (DrinkingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new DrinkingUpgradeTab.Basic(uc, p, s, Config.SERVER.drinkingUpgrade.slotsInRow.get()));
		UpgradeGuiManager.registerTab(ADVANCED_DRINKING_TYPE, (DrinkingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
				new DrinkingUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedDrinkingUpgrade.slotsInRow.get()));
	}

	private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == SBP_CREATIVE_TAB) {
			event.accept(DRINKING_UPGRADE.get());
			event.accept(ADVANCED_DRINKING_UPGRADE.get());
		}
	}
}
