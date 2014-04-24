package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

public class CompatibilityConfig {

	private static final String CATEGORY = ConfigurationHandler.CATEGORY_COMPAT;

	public static boolean disableChatToggle = false;
	public static boolean vanillaChat = true;
	
	public static void load(Configuration config) {
		disableChatToggle = config.get(CATEGORY, "disableChatToggle", disableChatToggle).getBoolean(disableChatToggle);
		vanillaChat = config.get(CATEGORY, "vanillaChat", vanillaChat).getBoolean(vanillaChat);
	}
	
	public static void save(Configuration config) {
		config.get(CATEGORY, "disableChatToggle", disableChatToggle).set(disableChatToggle);
		config.get(CATEGORY, "vanillaChat", vanillaChat).set(vanillaChat);
	}

	public static String handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("disableChatToggle")) value = String.valueOf(disableChatToggle);
		else if(key.equals("vanillaChat")) value = String.valueOf(vanillaChat);
		return value;
	}

	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("disableChatToggle")) {
			disableChatToggle = Boolean.parseBoolean(value);
		} else if(key.equals("vanillaChat")){
			vanillaChat = Boolean.parseBoolean(value);
		} else {
			return false;
		}
		return true;
	}
	
}
