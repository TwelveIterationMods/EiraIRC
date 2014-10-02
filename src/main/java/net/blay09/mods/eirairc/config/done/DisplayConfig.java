// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config.done;

import java.util.List;

import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

public class DisplayConfig {

	private static final String CATEGORY = ConfigurationHandler.CATEGORY_DISPLAY;
	
	public static boolean enableNameColors = true; // shared
	public static boolean enableIRCColors = true; // server

	public static String mcColor = "white"; // theme (default mc name)
	public static String mcOpColor = "red"; // theme (default op name)
	public static String ircOpColor = "gold"; // theme (default irc op name)
	public static String ircVoiceColor = "gray"; // theme (default irc voice name)
	public static String ircColor = "gray";  // theme (default irc name)
	public static String ircPrivateColor = "gray";  // theme (irc priv msg color)
	public static String ircNoticeColor = "red"; // theme (irc notice msg color)
	public static String emoteColor = "gold"; // theme (mc&irc emote msg color)

	public static String quitMessage = "Leaving."; // server
	public static boolean hudRecState = true; // client
	public static boolean hidePlayerTags = false; // shared


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
		enableIRCColors = config.get(CATEGORY, "enableIRCColors", enableIRCColors).getBoolean(enableIRCColors);
		hudRecState = config.get(CATEGORY, "hudRecState", hudRecState).getBoolean(hudRecState);
		hidePlayerTags = config.get(CATEGORY, "hidePlayerTags", hidePlayerTags).getBoolean(hidePlayerTags);
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
		config.get(CATEGORY, "enableIRCColors", enableIRCColors).set(enableIRCColors);
		config.get(CATEGORY, "hudRecState", hudRecState).set(hudRecState);
		config.get(CATEGORY, "hidePlayerTags", hidePlayerTags).set(hidePlayerTags);
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
		list.add("enableIRCColors");
		list.add("hidePlayerTags");
	}
	
	public static void addValuesToList(List<String> list, String option) {
		if(option.endsWith("Color")) {
			Utils.addValidColorsToList(list);
		} else if(option.startsWith("enable") || option.equals("hidePlayerTags")) {
			Utils.addBooleansToList(list);
		}
	}
	
	public static String handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("enableNameColors")) value = String.valueOf(enableNameColors);
		else if(key.equals("enableIRCColors")) value = String.valueOf(enableIRCColors);
		else if(key.equals("defaultColor")) value = mcColor;
		else if(key.equals("opColor")) value = mcOpColor;
		else if(key.equals("ircColor")) value = ircColor;
		else if(key.equals("ircPrivateColor")) value = ircPrivateColor;
		else if(key.equals("ircVoiceColor")) value = ircVoiceColor;
		else if(key.equals("ircOpColor")) value = ircOpColor;
		else if(key.equals("ircNoticeColor")) value = ircNoticeColor;
		else if(key.equals("emoteColor")) value = emoteColor;
		else if(key.equals("quitMessage")) value = quitMessage;
		else if(key.equals("hidePlayerTags")) value = String.valueOf(hidePlayerTags);
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
		} else if(key.equals("enableIRCColors")){
			enableIRCColors = Boolean.parseBoolean(value);
		} else if(key.equals("hidePlayerTags")){
			hidePlayerTags = Boolean.parseBoolean(value);
		} else {
			return false;
		}
		return true;
	}
	
}
