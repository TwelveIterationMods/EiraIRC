// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import blay09.mods.eirairc.Utils;
import blay09.mods.eirairc.command.IRCCommandHandler;

public class GlobalConfig {

	public static final String MC_CMESSAGE_FORMAT_NORMAL = "[{CHANNEL}] <{NICK}> {MESSAGE}";
	public static final String MC_CMESSAGE_FORMAT_LIGHT = "[#] <{NICK}> {MESSAGE}";
	public static final String MC_CMESSAGE_FORMAT_SLIGHT = "[{NICK}] {MESSAGE}";
	public static final String MC_CMESSAGE_FORMAT_DETAIL = "[{SERVER}:{CHANNEL}] <{NICK}> {MESSAGE}";
	public static final String MC_CEMOTE_FORMAT_NORMAL = "[{CHANNEL}] * {NICK} {MESSAGE}";
	public static final String MC_PMESSAGE_FORMAT_NORMAL = "[Private] <{NICK}> {MESSAGE}";
	public static final String MC_PMESSAGE_FORMAT_LIGHT = "[P] <{NICK}> {MESSAGE}";
	public static final String MC_PMESSAGE_FORMAT_SLIGHT = "[[{NICK}]] {MESSAGE}";
	public static final String MC_PMESSAGE_FORMAT_DETAIL = "[{SERVER}] <{NICK}> {MESSAGE}";
	public static final String MC_PEMOTE_FORMAT_NORMAL = "* {NICK} {MESSAGE}";
	public static final String IRC_CMESSAGE_FORMAT_NORMAL = "<{NICK}> {MESSAGE}";
	public static final String IRC_CMESSAGE_FORMAT_DETAIL = "[{SERVER}] <{NICK}> {MESSAGE}";
	public static final String IRC_CEMOTE_FORMAT_NORMAL = "{NICK} {MESSAGE}";
	public static final String IRC_PMESSAGE_FORMAT_NORMAL = "<{NICK}> {MESSAGE}";
	public static final String IRC_PMESSAGE_FORMAT_DETAIL = "[{SERVER}] <{NICK}> {MESSAGE}";
	public static final String IRC_PEMOTE_FORMAT_NORMAL = "{NICK} {MESSAGE}";
	
	public static String nick = ConfigurationHandler.DEFAULT_NICK;
	public static final List<String> colorBlackList = new ArrayList<String>();
	public static String opColor = "red";
	public static String ircColor = "gray";
	public static String quitMessage = "Leaving.";
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
	public static String mcChannelMsgFormat = MC_CMESSAGE_FORMAT_SLIGHT;
	public static String mcPrivateMsgFormat = MC_PMESSAGE_FORMAT_SLIGHT;
	public static String mcChannelEmtFormat = MC_CEMOTE_FORMAT_NORMAL;
	public static String mcPrivateEmtFormat = MC_PEMOTE_FORMAT_NORMAL;
	public static String ircChannelMsgFormat = IRC_CMESSAGE_FORMAT_NORMAL;
	public static String ircPrivateMsgFormat = IRC_PMESSAGE_FORMAT_NORMAL;
	public static String ircChannelEmtFormat = IRC_CEMOTE_FORMAT_NORMAL;
	public static String ircPrivateEmtFormat = IRC_PEMOTE_FORMAT_NORMAL;
	
	public static void handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("opColor")) {
			if(Utils.isValidColor(value)) {
				opColor = value;
			} else {
				IRCCommandHandler.sendLocalizedMessage(sender, "irc.colorInvalid", value);
				return;
			}
		} else if(key.equals("ircColor")) {
			if(Utils.isValidColor(value)) {
				ircColor = value;
			} else {
				IRCCommandHandler.sendLocalizedMessage(sender, "irc.colorInvalid", value);
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
			IRCCommandHandler.sendLocalizedMessage(sender, "irc.configRequiresRestart");
		} else if(key.equals("interOp") || key.equals("enableAliases")) {
			IRCCommandHandler.sendLocalizedMessage(sender, "irc.configNoAbuse");
			return;
		} else {
			IRCCommandHandler.sendLocalizedMessage(sender, "irc.invalidConfigChange", "Global", key);
			return;
		}
		IRCCommandHandler.sendLocalizedMessage(sender, "irc.configChange", "Global", key, value);
		ConfigurationHandler.save();
	}
	
}
