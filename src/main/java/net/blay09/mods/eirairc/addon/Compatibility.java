package net.blay09.mods.eirairc.addon;

import cpw.mods.fml.common.Loader;

public class Compatibility {

	public static boolean isEiraMoticonsInstalled() {
		return Loader.isModLoaded("eiramoticons");
	}

}
