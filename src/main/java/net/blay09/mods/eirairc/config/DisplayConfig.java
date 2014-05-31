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
	
	public static boolean enableNameColors = true;
	public static String mcColor = "white";
	public static String mcOpColor = "red";
	public static String ircOpColor = "gold";
	public static String ircVoiceColor = "gray";
	public static String ircColor = "gray";
	public static String ircPrivateColor = "gray";
	public static String ircNoticeColor = "red";
	public static String emoteColor = "gold";
	public static String quitMessage = "Leaving.";
	public static boolean hudRecState = true;

	
	public static void load(Configuration config) {
		emoteColor = config.get(CATEGORY, "emoteColor", emoteColor).getString();
		mcColor = config.get(CATEGORY, "defaultColor", mcColor).getString();
		mcOpColor = config.get(CATEGORY, "opColor", mcOpColor).getString();
		ircColor = config.get(CATEGORY, "ircColor", ircColor).getString();
		ircPrivateColor = config.get(CATEGORY, "ircPrivateColor", ircPrivateColor).getString();
		ircVoiceColor = config.get(CATEGORY, "ircVoiceColor", ircVoiceColor).getString();
		ircOpColor = config.get(CATEGORY, "ircOpColor", ircOpColor).getString();
		ircNoticeColor = config.get(CATEGORY, "ircNoticeColor", ircNoticeColor).getString();
		enableNameColors = config.get(CATEGORY, "enableNameColors", enableNameColors).getBoolean(enableNameColors);
		hudRecState = config.get(CATEGORY, "hudRecState", hudRecState).getBoolean(hudRecState);
	}
	
	public static void save(Configuration config) {
		config.get(CATEGORY, "emoteColor", emoteColor).set(emoteColor);
		config.get(CATEGORY, "defaultColor", mcColor).set(mcColor);
		config.get(CATEGORY, "opColor", mcOpColor).set(mcOpColor);
		config.get(CATEGORY, "ircColor", ircColor).set(ircColor);
		config.get(CATEGORY, "ircPrivateColor", ircPrivateColor).set(ircPrivateColor);
		config.get(CATEGORY, "ircVoiceColor", ircVoiceColor).set(ircVoiceColor);
		config.get(CATEGORY, "ircOpColor", ircOpColor).set(ircOpColor);
		config.get(CATEGORY, "ircNoticeColor", ircNoticeColor).set(ircNoticeColor);
		config.get(CATEGORY, "enableNameColors", enableNameColors).set(enableNameColors);
		config.get(CATEGORY, "hudRecState", hudRecState).set(hudRecState);
	}
	
	public static void addOptionsToList(List<String> list) {
		list.add("emoteColor");
		list.add("defaultColor");
		list.add("opColor");
		list.add("ircColor");
		list.add("ircPrivateColor");
		list.add("ircVoiceColor");
		list.add("ircOpColor");
		list.add("ircNoticeColor");
		list.add("enableNameColors");
	}
	
	public static void addValuesToList(List<String> list, String option) {
		if(option.endsWith("Color")) {
			Utils.addValidColorsToList(list);
		} else if(option.startsWith("enable")) {
			Utils.addBooleansToList(list);
		}
	}
	
	public static String handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("enableNameColors")) value = String.valueOf(enableNameColors);
		else if(key.equals("defaultColor")) value = mcColor;
		else if(key.equals("opColor")) value = mcOpColor;
		else if(key.equals("ircColor")) value = ircColor;
		else if(key.equals("ircPrivateColor")) value = ircPrivateColor;
		else if(key.equals("ircVoiceColor")) value = ircVoiceColor;
		else if(key.equals("ircOpColor")) value = ircOpColor;
		else if(key.equals("ircNoticeColor")) value = ircNoticeColor;
		else if(key.equals("emoteColor")) value = emoteColor;
		else if(key.equals("quitMessage")) value = quitMessage;
		return value;
	}
	
	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.endsWith("Color")) {
			if(!Utils.isValidColor(value)) {
				Utils.sendLocalizedMessage(sender, "irc.color.invalid", value);
				return false;
			}
		}
		if(key.equals("defaultColor")) {
			mcColor = value;
		} else if(key.equals("opColor")) {
			mcOpColor = value;
		} else if(key.equals("ircColor")) {
			ircColor = value;
		} else if(key.equals("ircPrivateColor")) {
			ircPrivateColor = value;
		} else if(key.equals("ircVoiceColor")) {
			ircVoiceColor = value;
		} else if(key.equals("ircOpColor")) {
			ircOpColor = value;
		} else if(key.equals("ircNoticeColor")) {
			ircNoticeColor = value;
		} else if(key.equals("emoteColor")) {
			emoteColor = value;
		} else if(key.equals("quitMessage")) {
			quitMessage = value;
		} else if(key.equals("enableNameColors")){
			enableNameColors = Boolean.parseBoolean(value);
		} else {
			return false;
		}
		return true;
	}
	
}
