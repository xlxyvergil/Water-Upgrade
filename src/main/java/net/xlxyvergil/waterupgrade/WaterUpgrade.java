package net.xlxyvergil.waterupgrade;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.api.distmarker.Dist;
import net.xlxyvergil.waterupgrade.init.ModItems;
import org.slf4j.Logger;

@Mod(WaterUpgrade.MODID)
public class WaterUpgrade {
	public static final String MODID = "waterupgrade";
	private static final Logger LOGGER = LogUtils.getLogger();

	public WaterUpgrade(FMLJavaModLoadingContext context) {
		IEventBus modEventBus = context.getModEventBus();

		modEventBus.addListener(this::commonSetup);

		MinecraftForge.EVENT_BUS.register(this);

		context.registerConfig(ModConfig.Type.COMMON, Config.SERVER_SPEC);

		ModItems.init(modEventBus);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		LOGGER.info("WaterUpgrade common setup");
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		LOGGER.info("WaterUpgrade server starting");
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			LogUtils.getLogger().info("WaterUpgrade client setup");
		}
	}
}
