// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Localization {

	public static void addString(String key, String text) {
		LanguageRegistry.instance().addStringLocalization(Globals.MOD_ID + ":" + key, text);
	}
	
	public static void init() {
		
		/* COMMANDS */
		addString("irc.commands.irc", "/[serv]irc <command>");
		addString("irc.commands.connect", "/[serv]irc connect <server> [password]");
		addString("irc.commands.disconnect", "/[serv]irc disconnect [server]");
		addString("irc.commands.join", "/[serv]irc join [server/]<channel> (ex. irc.esper.net/#EiraIRC)");
		addString("irc.commands.leave", "/[serv]irc leave [server/]<channel> (ex. irc.esper.net/#EiraIRC)");
		addString("irc.commands.who", "/[serv]irc who [(<server>|<channel>)]");
		addString("irc.commands.msg", "/[serv]irc msg <nick> <message ...>");
		addString("irc.commands.help", "/[serv]irc help <topic>");
		addString("irc.commands.nick", "/[serv]irc nick [server] <nick>");
		addString("irc.commands.config", "/[serv]irc config (global|<server>|<channel>) <key> [value]");
		addString("irc.commands.twitch", "/servirc twitch <username> <oauth>");
		addString("irc.commands.nickserv", "/servirc nickserv <username> <password>");
		addString("irc.commands.color", "/irc color <color>");
		addString("irc.commands.alias", "/irc alias <username> <alias> OR /irc alias <alias>");
		
		/* COMMAND LIST */
		addString("irc.cmdlist.general", EnumChatFormatting.YELLOW + "General Commands: " + EnumChatFormatting.WHITE + "config, help, list");
		addString("irc.cmdlist.irc", EnumChatFormatting.YELLOW + "IRC Commands: " + EnumChatFormatting.WHITE + "connect, disconnect, join, leave, nick, msg, who");
		addString("irc.cmdlist.special", EnumChatFormatting.YELLOW + "Special Commands: " + EnumChatFormatting.WHITE + "twitch, color, alias");
		
		/* BOT */
		addString("irc.bot.cmdlist", "Channel Bot Commands: !who");
		addString("irc.bot.unknownCommand", "Unknown command. Type HELP to see a list of all available commands.");
		addString("irc.bot.msgSent", "Message sent to %s: %s");
		addString("irc.bot.nickInUse", "Nickname %s is already in use. Trying %s...");
		addString("irc.bot.nickInvalid", "%s is not a valid nickname.");
		addString("irc.bot.playersOnline", "%d players online:");
		addString("irc.bot.noPlayersOnline", "No players online.");
		
		/* GENERAL */
		addString("irc.general.notConnected", EnumChatFormatting.RED + "You are not connected to %s.");
		addString("irc.general.alreadyConnected", EnumChatFormatting.RED + "You are already connected to %s.");
		addString("irc.general.notOnChannel", EnumChatFormatting.RED + "You are not on channel %s.");
		addString("irc.general.noPermission", EnumChatFormatting.RED + "You do not have permission to use this command.");
		addString("irc.general.noSuchPlayer", "That player cannot be found.");
		addString("irc.general.notMultiplayer", EnumChatFormatting.RED + "This is not a multiplayer server. Use /irc instead.");
		addString("irc.general.serverOnlyCommand", "This command only works on the server side. Use the GUI (Default: I) or the config file for the client settings.");
		addString("irc.general.usage", EnumChatFormatting.RED + "Usage: %s");
		
		/* BASIC IRC */
		addString("irc.basic.connecting", "Connecting to %s...");
		addString("irc.basic.connected", "Connected to %s.");
		addString("irc.basic.disconnecting", "Disconnecting from %s...");
		addString("irc.basic.disconnected", "Disconnected from %s.");
		addString("irc.basic.joiningChannel", "Joining channel %s on %s...");
		addString("irc.basic.leavingChannel", "Leaving channel %s on %s...");
		addString("irc.basic.changingNick", "[%s] Changing nick to %s...");
		addString("irc.basic.nickServUpdated", "Updated nickserv information for %s.");
		
		/* COMMAND SPECIFIC */
		addString("irc.list.activeConnections", EnumChatFormatting.YELLOW + "EiraIRC is connected to: ");
		addString("irc.alias.set", "Alias for %s set to '%s'.");
		addString("irc.alias.reset", "Alias removed for %s.");
		addString("irc.alias.disabled", "Aliases are disabled on this server.");
		addString("irc.alias.lookup", "The username for %s is '%s'.");
		addString("irc.alias.notFound", "The alias '%s' cannot be found.");
		addString("irc.color.disabled", "Name colors are disabled on this server.");
		addString("irc.color.set", "Name color set to '%s'.");
		addString("irc.color.reset", "Name color reset.");
		addString("irc.color.blacklist", "The color '%s' is not allowed on this server.");
		addString("irc.color.invalid", "'%s' is not a valid color. Use TAB to loop through valid options.");
		addString("irc.who.usersOnline", "[%s] " + EnumChatFormatting.AQUA + "%d users " + EnumChatFormatting.WHITE + "online in " + EnumChatFormatting.YELLOW + "%s" + EnumChatFormatting.WHITE + ":");
		addString("irc.who.noUsersOnline", "[%s] No users online in %s.");
		addString("irc.msg.disabled", "Private messages are not enabled.");
		addString("irc.config.noAbuse", "To prevent abuse, this setting can only be changed in the config file.");
		addString("irc.config.change", "[%s] Setting config option '%s' to '%s'");
		addString("irc.config.lookup", "[%s] Config option '%s' is set to '%s'");
		addString("irc.config.invalidOption", "[%s] Invalid config option '%s'. Use TAB to loop through valid options.");
		addString("irc.config.requiresRestart", "This config option requires a restart to take effect.");
		addString("irc.help.validTopics", EnumChatFormatting.YELLOW + "Valid topics are: ");
		addString("irc.help.topicList", "* color, alias, twitch, msg, commands, config");
		addString("irc.help.invalidTopic", EnumChatFormatting.RED + "Invalid help topic '%s'. Valid topics are: ");
		addString("irc.connect.error", "Could not connect to %s.");
		
		/* DISPLAY FORMAT */
		addString("irc.display.irc.joinMsg", EnumChatFormatting.YELLOW + "[%s] * %s joined the channel");
		addString("irc.display.irc.partMsg", EnumChatFormatting.YELLOW + "[%s] * %s left the channel");
		addString("irc.display.irc.quitMsg", EnumChatFormatting.YELLOW + "[%s] * %s disconnected from IRC (%s)");
		addString("irc.display.irc.nickChange", EnumChatFormatting.YELLOW + "[%s] * %s is now known as %s");
		addString("irc.display.irc.topic", EnumChatFormatting.YELLOW + "Topic for %s: " + EnumChatFormatting.WHITE + "%s");
		addString("irc.display.mc.joinMsg", "* %s joined the game");
		addString("irc.display.mc.partMsg", "* %s left the game");
		addString("irc.display.mc.nickChange", "* %s is now known as %s");
		
		/* TARGET ERRORS */
		addString("irc.target.specifyChannel", "You have to specify a channel.");
		addString("irc.target.specifyServer", "You have to specify a server.");
		addString("irc.target.serverNotFound", "The server at %s cannot be found.");
		addString("irc.target.invalidTarget", "Invalid target. Expected %s.");
		addString("irc.target.notConnected", "You are not connected to the server at %s.");
	}
	
}
