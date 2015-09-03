// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.addon;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.event.InitConfigEvent;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.minecraftforge.common.MinecraftForge;

public class Compatibility {

	private static boolean isEiraMoticonsInstalled;
	public static boolean isEiraMoticonsInstalled() {
		return isEiraMoticonsInstalled;
	}

	public static void postInit(FMLPostInitializationEvent event) {
		event.buildSoftDependProxy("Dynmap", "net.blay09.mods.eirairc.addon.DynmapWebChatAddon");

		if(event.getSide() == Side.CLIENT) {
			event.buildSoftDependProxy("eiramoticons", "net.blay09.mods.eirairc.addon.EiraMoticonsAddon");
			new FancyOverlay();
		}

		MinecraftForge.EVENT_BUS.post(new InitConfigEvent.SharedGlobalSettings(SharedGlobalConfig.manager));
		if(EiraIRC.proxy.getClientGlobalConfig() != null) {
			MinecraftForge.EVENT_BUS.post(new InitConfigEvent.ClientGlobalSettings(EiraIRC.proxy.getClientGlobalConfig()));
		}

		isEiraMoticonsInstalled = Loader.isModLoaded("eiramoticons");
	}
}
