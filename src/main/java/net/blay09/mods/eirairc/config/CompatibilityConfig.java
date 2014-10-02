// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.util.List;

import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

public class CompatibilityConfig {

	private static final String CATEGORY = ConfigurationHandler.CATEGORY_COMPAT;
	private static final String DEFAULT_CLIENT_BRIDGE_TOKEN = "[IG]";

	public static boolean disableChatToggle = false; // shared or client?
	public static boolean vanillaChat = true; // shared or client?
	public static boolean clientBridge = false; // shared or client?
	public static String clientBridgeMessageToken = DEFAULT_CLIENT_BRIDGE_TOKEN; // shared or client?
	public static String clientBridgeNickToken = ""; // shared or client?

	public static void load(Configuration config) {
		disableChatToggle = config.get(CATEGORY, "disableChatToggle", disableChatToggle).getBoolean(disableChatToggle);
		vanillaChat = config.get(CATEGORY, "vanillaChat", vanillaChat).getBoolean(vanillaChat);
		// TODO forcefully enable vanillaChat
		vanillaChat = true;
		clientBridge = config.get(CATEGORY, "clientBridge", clientBridge).getBoolean(clientBridge);
		clientBridgeMessageToken = Utils.unquote(config.get(CATEGORY, "clientBridgeMessageToken", clientBridgeMessageToken).getString());
		clientBridgeNickToken = Utils.unquote(config.get(CATEGORY, "clientBridgeNickToken", clientBridgeNickToken).getString());

	}
	
	public static void save(Configuration config) {
		config.get(CATEGORY, "disableChatToggle", disableChatToggle).set(disableChatToggle);
		config.get(CATEGORY, "vanillaChat", vanillaChat).set(vanillaChat);
		config.get(CATEGORY, "clientBridge", clientBridge).set(clientBridge);
		config.get(CATEGORY, "clientBridgeMessageToken", "").set(Utils.quote(clientBridgeMessageToken));
		config.get(CATEGORY, "clientBridgeNickToken", "").set(Utils.quote(clientBridgeNickToken));

	}

	public static String handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("disableChatToggle")) value = String.valueOf(disableChatToggle);
		else if(key.equals("vanillaChat")) value = String.valueOf(vanillaChat);
		else if(key.equals("clientBridge")) value = String.valueOf(clientBridge);
		else if(key.equals("clientBridgeMessageToken")) value = clientBridgeMessageToken;
		else if(key.equals("clientBridgeNickToken")) value = clientBridgeNickToken;
		return value;
	}

	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("disableChatToggle")) {
			disableChatToggle = Boolean.parseBoolean(value);
		} else if(key.equals("vanillaChat")) {
			vanillaChat = Boolean.parseBoolean(value);
		} else if(key.equals("clientBridge")) {
			clientBridge = Boolean.parseBoolean(value);
		} else if(key.equals("clientBridgeMessageToken")) {
			clientBridgeMessageToken = value;
		} else if(key.equals("clientBridgeNickToken")) {
			clientBridgeNickToken = value;
		} else {
			return false;
		}
		return true;
	}
	
	public static void addOptionsToList(List<String> list) {
		list.add("disableChatToggle");
//		list.add("vanillaChat");
		list.add("clientBridge");
		list.add("clientBridgeMessageToken");
		list.add("clientBridgeNickToken");
	}

	public static void addValuesToList(List<String> list, String option) {
		if(option.equals("disableChatToggle") || option.equals("vanillaChat") || option.equals("clientBridge")) {
			Utils.addBooleansToList(list);
		} else if(option.equals("clientBridgeMessageToken")) {
			list.add(DEFAULT_CLIENT_BRIDGE_TOKEN);
		}
	}
}
