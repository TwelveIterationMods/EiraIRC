// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.config;

import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Localization {

	public static void init() {
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage", "/%s <connect|disconnect|join|leave|nick|who|color|alias|twitch|nickserv|mode|msg|list|config|help>");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.connect", "/%s connect <server> [password]");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.twitch", "/%s twitch <username> <password>");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.nick", "/%s nick [server] <nick>");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.join", "/%s join [server] <channel>");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.leave", "/%s leave [server] <channel>");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.who", "/%s who [server] [channel]");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.mode", "/%s mode [server] [channel] [flags]");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.msg", "/%s msg [server] <nick> <message>");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.config", "/%s config <global|<server>> <option> <value>");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.help", "/%s help <topic>");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.color", "/irc color <color>");
		LanguageRegistry.instance().addStringLocalization("commands.irc.usage.alias", "/irc alias <username> <alias> OR /irc alias <alias>");

		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.nopermission", EnumChatFormatting.RED + "You do not have permission to use this command.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.nosuchplayer", EnumChatFormatting.RED + "That player cannot be found");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.server.alreadyConnected", "The server is already connected to %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.client.alreadyConnected", "You are already connected to %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.server.notConnected", "The server is not connected to %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.client.notConnected", "You are not connected to %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.server.activeConnections", "The server is connected to:");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.client.activeConnections", "The client is connected to:");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.connecting", "Connecting to %s...");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.connected", "Connected to %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.disconnecting", "Disconnecting from %s...");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.disconnected", "Disconnected from %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.joinChannel", "Joining channel %s on %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.leaveChannel", "Leaving channel %s on %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.notMultiplayer", "This is not a multiplayer server. Use /irc instead.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.aliasSet", "Alias for '%s' set to '%s'");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.aliasDisabled", "Aliases are disabled on this server.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.aliasLookup", "The username for '%s' is '%s'");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.colorDisabled", "Name colors are disabled on this server.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.colorSet", "Name color set to '%s'");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.colorBlackList", "The color '%s' is not allowed on this server.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.usersOnline", "%d users online in %s on %s:");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.joinMsgIRC", EnumChatFormatting.YELLOW + "[%s] * %s joined the channel");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.partMsgIRC", EnumChatFormatting.YELLOW + "[%s] * %s left the channel");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.joinMsgMC", "* %s joined the game");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.deathMsgMC", "* %s has died: %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.partMsgMC", "* %s left the game");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.nickChangeIRC", EnumChatFormatting.YELLOW + "[%s] * %s is now known as %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.nickChangeMC", "* %s is now known as %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.channelMode", "Mode for channel %s on %s is %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.channelModeChange", "Mode %s for channel %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.specifyChannel", "You have to specify a channel.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.specifyServer", "You have to specify a server.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.msgDisabled", "Private messages are not enabled.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.msgInvalidTarget", "%s is not in a valid target channel.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.configNoAbuse", "To prevent abuse, this setting can only be changed in the config file.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.helpValidTopics", EnumChatFormatting.YELLOW + "Valid topics are:");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.helpTopics", EnumChatFormatting.YELLOW + "* color, alias, twitch, msg, mode, commands, config");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.helpInvalidTopic", EnumChatFormatting.YELLOW + "Invalid help topic '%s'. Valid topics are:");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.configChange", "[%s] Setting config option '%s' to '%s'");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.invalidConfigChange", "[%s] Invalid config option %s. Use TAB to loop through valid options.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.nickChange", "[%s] Changing nick to %s...");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.colorInvalid", "'%s' is not a valid color.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.aliasNotFound", "The alias '%s' cannot be found.");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.connectionError", "Could not connect to %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.nickServUpdated", "Updated nickserv information for %s");
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":irc.serverOnlyCommand", "This command only works on the server side. Use the GUI (default: I) or the config file for the client settings.");
	}
	
}
