// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.ConfigCategory;
import blay09.mods.eirairc.Utils;

public class GlobalConfig {

	public static String nick = ConfigurationHandler.DEFAULT_NICK;
	public static final List<String> colorBlackList = new ArrayList<String>();
	public static final Map<String, DisplayFormatConfig> displayFormates = new HashMap<String, DisplayFormatConfig>();
	public static String opColor = "red";
	public static String ircColor = "gray";
	public static String emoteColor = "purple";
	public static String quitMessage = "Leaving.";
	public static String displayMode = "S-Light";
	public static boolean enableNameColors = true;
	public static boolean enableAliases = true;
	public static boolean relayDeathMessages = true;
	public static boolean relayMinecraftJoinLeave = true;
	public static boolean relayIRCJoinLeave = true;
	public static boolean relayNickChanges = true;
	public static boolean allowPrivateMessages = true;
	public static boolean persistentConnection = true;
	public static boolean saveCredentials = false;
	public static boolean enableLinkFilter = true;
	public static boolean registerShortCommands = true;
	public static boolean interOp = false;
	
	public static DisplayFormatConfig getDisplayFormatConfig() {
		DisplayFormatConfig dfc = displayFormates.get(displayMode);
		if(dfc == null) {
			displayMode = "S-Light";
			dfc = displayFormates.get(displayMode);
			if(dfc == null) {
				return new DisplayFormatConfig(new ConfigCategory("unknown"));
			}
		}
		return dfc;
	}
	
	public static void handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("opColor")) {
			if(Utils.isValidColor(value)) {
				opColor = value;
			} else {
				Utils.sendLocalizedMessage(sender, "irc.color.invalid", value);
				return;
			}
		} else if(key.equals("ircColor")) {
			if(Utils.isValidColor(value)) {
				ircColor = value;
			} else {
				Utils.sendLocalizedMessage(sender, "irc.color.invalid", value);
				return;
			}
		} else if(key.equals("emoteColor")) {
			if(Utils.isValidColor(value)) {
				emoteColor = value;
			} else {
				Utils.sendLocalizedMessage(sender, "irc.color.invalid", value);
				return;
			}
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
		} else if(key.equals("allowPrivateMessages")){
			allowPrivateMessages = Boolean.parseBoolean(value);
		} else if(key.equals("persistentConnection")){
			persistentConnection = Boolean.parseBoolean(value);
		} else if(key.equals("saveCredentials")){
			saveCredentials = Boolean.parseBoolean(value);
		} else if(key.equals("enableLinkFilter")){
			enableLinkFilter = Boolean.parseBoolean(value);
		} else if(key.equals("registerShortCommands")){
			registerShortCommands = Boolean.parseBoolean(value);
			Utils.sendLocalizedMessage(sender, "irc.config.requiresRestart");
		} else if(key.equals("interOp") || key.equals("enableAliases")) {
			Utils.sendLocalizedMessage(sender, "irc.config.noAbuse");
			return;
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", "Global", key);
			return;
		}
		Utils.sendLocalizedMessage(sender, "irc.config.change", "Global", key, value);
		ConfigurationHandler.save();
	}
	
}
