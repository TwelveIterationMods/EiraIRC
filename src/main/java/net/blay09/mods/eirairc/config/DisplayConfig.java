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
	public static String opColor = "red";
	public static String ircColor = "gray";
	public static String emoteColor = "gold";
	public static String quitMessage = "Leaving.";
	public static boolean hudRecState = true;
	
	public static void load(Configuration config) {
		ircColor = config.get(CATEGORY, "ircColor", ircColor).getString();
		emoteColor = config.get(CATEGORY, "emoteColor", emoteColor).getString();
		opColor = config.get(CATEGORY, "opColor", opColor).getString();
		enableNameColors = config.get(CATEGORY, "enableNameColors", enableNameColors).getBoolean(enableNameColors);
		hudRecState = config.get(CATEGORY, "hudRecState", hudRecState).getBoolean(hudRecState);
	}
	
	public static void save(Configuration config) {
		config.get(CATEGORY, "ircColor", ircColor).set(ircColor);
		config.get(CATEGORY, "emoteColor", emoteColor).set(emoteColor);
		config.get(CATEGORY, "opColor", opColor).set(opColor);
		config.get(CATEGORY, "enableNameColors", enableNameColors).set(enableNameColors);
		config.get(CATEGORY, "hudRecState", hudRecState).set(hudRecState);
	}
	
	public static void addOptionsToList(List<String> list) {
		list.add("ircColor");
		list.add("emoteColor");
		list.add("opColor");
		list.add("enableNameColors");
	}
	
	public static void addValuesToList(List<String> list, String option) {
		if(option.endsWith("Color")) {
			Utils.addValidColorsToList(list);
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
