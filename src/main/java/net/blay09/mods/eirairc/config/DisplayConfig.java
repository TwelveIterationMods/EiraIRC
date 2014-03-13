// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class DisplayConfig {

	private static final String CATEGORY = ConfigurationHandler.CATEGORY_DISPLAY;
	
	public static final Map<String, DisplayFormatConfig> displayFormates = new HashMap<String, DisplayFormatConfig>();
	
	public static boolean enableNameColors = true;
	public static String opColor = "red";
	public static String ircColor = "gray";
	public static String emoteColor = "gold";
	public static String quitMessage = "Leaving.";
	public static String displayMode = "S-Light";
	public static String originalDisplayMode;
	public static boolean relayDeathMessages = true;
	public static boolean relayMinecraftJoinLeave = true;
	public static boolean relayIRCJoinLeave = true;
	public static boolean relayNickChanges = true;
	public static boolean hudRecState = true;
	public static String botProfile = "Custom";
	public static boolean vanillaChat = false;
	
	public static void load(Configuration config) {
		displayMode = Utils.unquote(config.get(CATEGORY, "displayMode", displayMode).getString());
		ircColor = config.get(CATEGORY, "ircColor", ircColor).getString();
		emoteColor = config.get(CATEGORY, "emoteColor", emoteColor).getString();
		relayDeathMessages = config.get(CATEGORY, "relayDeathMessages", relayDeathMessages).getBoolean(relayDeathMessages);
		relayMinecraftJoinLeave = config.get(CATEGORY, "relayMinecraftJoinLeave", relayMinecraftJoinLeave).getBoolean(relayMinecraftJoinLeave);
		relayIRCJoinLeave = config.get(CATEGORY, "relayIRCJoinLeave", relayIRCJoinLeave).getBoolean(relayIRCJoinLeave);
		relayNickChanges = config.get(CATEGORY, "relayNickChanges", relayNickChanges).getBoolean(relayNickChanges);
		opColor = config.get(CATEGORY, "opColor", opColor).getString();
		enableNameColors = config.get(CATEGORY, "enableNameColors", enableNameColors).getBoolean(enableNameColors);
		hudRecState = config.get(CATEGORY, "hudRecState", hudRecState).getBoolean(hudRecState);
		vanillaChat = config.get(CATEGORY, "vanillaChat", vanillaChat).getBoolean(vanillaChat);
		
		ConfigCategory displayFormatCategory = config.getCategory(CATEGORY + Configuration.CATEGORY_SPLITTER + ConfigurationHandler.CATEGORY_FORMATS);
		DisplayFormatConfig.defaultConfig(config, displayFormatCategory);
		for(ConfigCategory category : displayFormatCategory.getChildren()) {
			DisplayFormatConfig dfc = new DisplayFormatConfig(category);
			dfc.load(config);
			displayFormates.put(dfc.getName(), dfc);
		}
		config.getCategory(ConfigurationHandler.CATEGORY_DISPLAY).setComment("These options determine how the chat is displayed and what is sent / received to and from IRC.");
	}
	
	public static void save(Configuration config) {
		config.get(CATEGORY, "displayMode", "").set(Utils.quote(displayMode));
		config.get(CATEGORY, "ircColor", ircColor).set(ircColor);
		config.get(CATEGORY, "emoteColor", emoteColor).set(emoteColor);
		config.get(CATEGORY, "relayDeathMessages", relayDeathMessages).set(relayDeathMessages);
		config.get(CATEGORY, "relayMinecraftJoinLeave", relayMinecraftJoinLeave).set(relayMinecraftJoinLeave);
		config.get(CATEGORY, "relayIRCJoinLeave", relayIRCJoinLeave).set(relayIRCJoinLeave);
		config.get(CATEGORY, "relayNickChanges", relayNickChanges).set(relayNickChanges);
		config.get(CATEGORY, "opColor", opColor).set(opColor);
		config.get(CATEGORY, "enableNameColors", enableNameColors).set(enableNameColors);
		config.get(CATEGORY, "hudRecState", hudRecState).set(hudRecState);
		config.get(CATEGORY, "vanillaChat", vanillaChat).set(vanillaChat);
	}
	
	public static void addOptionsToList(List<String> list) {
		list.add("displayMode");
		list.add("ircColor");
		list.add("emoteColor");
		list.add("relayDeathMessages");
		list.add("relayMinecraftJoinLeave");
		list.add("relayIRCJoinLeave");
		list.add("relayNickChanges");
		list.add("opColor");
		list.add("enableNameColors");
		list.add("vanillaChat");
	}
	
	public static void addValuesToList(List<String> list, String option) {
		if(option.endsWith("Color")) {
			Utils.addValidColorsToList(list);
		} else if(option.equals("displayMode")) {
			for(String dm : displayFormates.keySet()) {
				list.add(dm);
			}
		} else if(option.equals("vanillaChat")) {
			Utils.addBooleansToList(list);
		} else if(option.startsWith("relay") || option.startsWith("enable")) {
			Utils.addBooleansToList(list);
		}
	}
	
	public static String handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("enableNameColors")) value = String.valueOf(enableNameColors);
		else if(key.equals("opColor")) value = opColor;
		else if(key.equals("ircColor")) value = ircColor;
		else if(key.equals("emoteColor")) value = emoteColor;
		else if(key.equals("quitMessage")) value = quitMessage;
		else if(key.equals("displayMode")) value = displayMode;
		else if(key.equals("relayMinecraftJoinLeave")) value = String.valueOf(relayMinecraftJoinLeave);
		else if(key.equals("relayIRCJoinLeave")) value = String.valueOf(relayIRCJoinLeave);
		else if(key.equals("relayDeathMessages")) value = String.valueOf(relayDeathMessages);
		else if(key.equals("relayNickChanges")) value = String.valueOf(relayNickChanges);
		else if(key.equals("vanillaChat")) value = String.valueOf(vanillaChat);
		return value;
	}
	
	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.endsWith("Color")) {
			if(!Utils.isValidColor(value)) {
				Utils.sendLocalizedMessage(sender, "irc.color.invalid", value);
				return false;
			}
		}
		if(key.equals("opColor")) {
			opColor = value;
		} else if(key.equals("ircColor")) {
			ircColor = value;
		} else if(key.equals("emoteColor")) {
			emoteColor = value;
		} else if(key.equals("displayMode")) {
			if(displayFormates.containsKey(value)) {
				Utils.sendLocalizedMessage(sender, "irc.config.invalidDisplayMode", value);
				return false;
			}
			displayMode = value;
		} else if(key.equals("quitMessage")) {
			quitMessage = value;
		} else if(key.equals("enableNameColors")){
			enableNameColors = Boolean.parseBoolean(value);
		} else if(key.equals("relayDeathMessages")){
			relayDeathMessages = Boolean.parseBoolean(value);
		} else if(key.equals("relayMinecraftJoinLeave")){
			relayMinecraftJoinLeave = Boolean.parseBoolean(value);
		} else if(key.equals("relayIRCJoinLeave")){
			relayIRCJoinLeave = Boolean.parseBoolean(value);
		} else if(key.equals("relayNickChanges")){
			relayNickChanges = Boolean.parseBoolean(value);
		} else if(key.equals("vanillaChat")){
			vanillaChat = Boolean.parseBoolean(value);
		} else {
			return false;
		}
		return true;
	}
	
}
