package net.blay09.mods.eirairc.addon;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

public class Compatibility {

	private static boolean isEiraMoticonsInstalled;
	public static boolean isEiraMoticonsInstalled() {
		return isEiraMoticonsInstalled;
	}

	public static void postInit(FMLPostInitializationEvent event) {
		event.buildSoftDependProxy("Dynmap", "net.blay09.mods.eirairc.addon.DynmapWebChatAddon");
		event.buildSoftDependProxy("eiramoticons", "net.blay09.mods.eirairc.addon.EiraMoticonsAddon");

		isEiraMoticonsInstalled = Loader.isModLoaded("eiramoticons");
	}
}
