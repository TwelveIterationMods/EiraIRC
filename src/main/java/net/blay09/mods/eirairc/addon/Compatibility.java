// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.addon;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public class Compatibility {

	public static void postInit(FMLPostInitializationEvent event) {
		event.buildSoftDependProxy("Dynmap", "net.blay09.mods.eirairc.addon.DynmapWebChatAddon");

		if(event.getSide() == Side.CLIENT) {
			event.buildSoftDependProxy("eiramoticons", "net.blay09.mods.eirairc.addon.EiraMoticonsAddon");
			new FancyOverlay();
		}
	}

}
