package net.xlxyvergil.waterupgrade.compat.thirst;

import net.minecraftforge.fml.ModList;

public class ThirstHandlerRegistry {
	private static IThirstHandler handler = null;

	public static IThirstHandler getHandler() {
		if (handler == null) {
			if (ModList.get().isLoaded("toughasnails")) {
				handler = new TanThirstHandler();
			} else if (ModList.get().isLoaded("homeostatic")) {
				handler = new HomeostaticThirstHandler();
			} else if (ModList.get().isLoaded("thirst")) {
				handler = new ThirstModHandler();
			} else if (ModList.get().isLoaded("legendarysurvivaloverhaul")) {
				handler = new LsoThirstHandler();
			}
		}
		return handler;
	}
}
