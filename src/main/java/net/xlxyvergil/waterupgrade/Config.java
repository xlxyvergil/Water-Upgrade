package net.xlxyvergil.waterupgrade;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IUpgradeCountLimitConfig;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeGroup;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
	public static final Server SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		SERVER = new Server(builder);
		SERVER_SPEC = builder.build();
	}

	public static class Server {
		public final DrinkingUpgradeConfig drinkingUpgrade;
		public final DrinkingUpgradeConfig advancedDrinkingUpgrade;
		public final IUpgradeCountLimitConfig maxUpgradesPerStorage;

		Server(ForgeConfigSpec.Builder builder) {
			drinkingUpgrade = new DrinkingUpgradeConfig(builder, "Drinking Upgrade", "drinkingUpgrade", 9, 3);
			advancedDrinkingUpgrade = new DrinkingUpgradeConfig(builder, "Advanced Drinking Upgrade", "advancedDrinkingUpgrade", 16, 4);
			maxUpgradesPerStorage = new SimpleUpgradeCountLimitConfig();
		}

		public static class DrinkingUpgradeConfig {
			public final ForgeConfigSpec.IntValue filterSlots;
			public final ForgeConfigSpec.IntValue slotsInRow;

			DrinkingUpgradeConfig(ForgeConfigSpec.Builder builder, String name, String path, int defaultFilterSlots, int defaultSlotsInRow) {
				builder.push(path);
				filterSlots = builder.comment("Number of filter slots in the " + name).defineInRange("filterSlots", defaultFilterSlots, 1, 36);
				slotsInRow = builder.comment("Number of filter slots in a row in the " + name).defineInRange("slotsInRow", defaultSlotsInRow, 1, 12);
				builder.pop();
			}
		}

		private static class SimpleUpgradeCountLimitConfig implements IUpgradeCountLimitConfig {
			@Override
			public int getMaxUpgradesPerStorage(String storageType, @Nullable ResourceLocation upgradeRegistryName) {
				return 1;
			}

			@Override
			public int getMaxUpgradesInGroupPerStorage(String storageType, UpgradeGroup upgradeGroup) {
				return 1;
			}
		}
	}
}
