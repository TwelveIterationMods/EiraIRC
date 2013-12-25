// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IRCChannel;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.irc.IRCUser;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.IRCTargetError;
import blay09.mods.eirairc.util.Utils;

public class IRCCommandHandler {

	public static boolean onChatCommand(EntityPlayer sender, String text, boolean serverSide) {
		String[] params = text.split(" ");
		if(!params[0].startsWith("!")) {
			return false;
		}
		params[0] = params[0].substring(1);
		return processCommand(sender, params, serverSide);
	}
	
	public static boolean processCommand(ICommandSender sender, String[] args, boolean serverSide) {
		String commandName = serverSide ? "servirc" : "irc";
		if(args.length < 1) {
			sendIRCUsage(sender);
			return true;
		}
		String cmd = args[0];
		if(cmd.equals("connect")) { // [serv]irc connect <target> [password]
			if(serverSide && !Utils.isOP(sender)) {
				Utils.sendLocalizedMessage(sender, "irc.general.noPermission");
				return true;
			}
			if(args.length <= 1) {
				throw new WrongUsageException("EiraIRC:irc.commands.connect", commandName);
			}
			String host = args[1];
			if(EiraIRC.instance.isConnectedTo(host)) {
				Utils.sendLocalizedMessage(sender, "irc.general.alreadyConnected", host);
				return true;
			}
			Utils.sendLocalizedMessage(sender, "irc.basic.connecting", host);
			ServerConfig serverConfig = ConfigurationHandler.getServerConfig(host);
			String password = null;
			if(args.length > 2) {
				password = args[2];
				serverConfig.setServerPassword(args[2]);
			}
			if(Utils.connectTo(serverConfig) != null) {
				ConfigurationHandler.addServerConfig(serverConfig);
				ConfigurationHandler.save();
			} else {
				Utils.sendLocalizedMessage(sender, "irc.connect.error", host);
			}
			return true;
		} else if(cmd.equals("twitch")) { // servirc twitch <username> <oauth> OR irc twitch
			if(serverSide && !Utils.isOP(sender)) {
				Utils.sendLocalizedMessage(sender, "irc.general.noPermission");
				return true;
			}
			if(!serverSide) {
				if(!ConfigurationHandler.hasServerConfig(Globals.TWITCH_SERVER) || args.length > 1) {
					Utils.sendLocalizedMessage(sender, "irc.general.serverOnlyCommand");
				} else {
					Utils.sendLocalizedMessage(sender, "irc.basic.connecting", "Twitch");
					ServerConfig serverConfig = ConfigurationHandler.getServerConfig(Globals.TWITCH_SERVER);
					Utils.connectTo(serverConfig);
				}
				return true;
			} else {
				if(EiraIRC.instance.isConnectedTo(Globals.TWITCH_SERVER)) {
					Utils.sendLocalizedMessage(sender, "irc.general.alreadyConnected", "Twitch");
					return true;
				}
				if(args.length <= 2 && !ConfigurationHandler.hasServerConfig(Globals.TWITCH_SERVER)) {
					throw new WrongUsageException("EiraIRC:irc.commands.twitch", commandName);
				}
				ServerConfig serverConfig = ConfigurationHandler.getServerConfig(Globals.TWITCH_SERVER);
				if(args.length > 2) {
					serverConfig.setNick(args[1]);
					serverConfig.setServerPassword(args[2]);
					String userChannel = "#" + args[1];
					if(!serverConfig.hasChannelConfig(userChannel)) {
						ChannelConfig channelConfig = new ChannelConfig(serverConfig, userChannel);
						channelConfig.defaultTwitch();
						serverConfig.addChannelConfig(channelConfig);
					}
					ConfigurationHandler.addServerConfig(serverConfig);
					ConfigurationHandler.save();
				}
				Utils.sendLocalizedMessage(sender, "irc.basic.connecting", "Twitch");
				IRCConnection connection = new IRCConnection(Globals.TWITCH_SERVER, serverConfig.getNick());
				if(connection.connect()) {
					EiraIRC.instance.addConnection(connection);
				}
			}
			return true;
		} else if(cmd.equals("nickserv")) { // servirc nickserv [target] <nick> <password>
			if(serverSide && !Utils.isOP(sender)) {
				Utils.sendLocalizedMessage(sender, "irc.general.noPermission");
				return true;
			}
			if(!serverSide) {
				Utils.sendLocalizedMessage(sender, "irc.general.serverOnlyCommand");
				return true;
			} else {
				if(args.length <= 2) {
					throw new WrongUsageException("EiraIRC:irc.commands.nickserv", commandName);
				}
				IRCConnection connection = null;
				String username = null;
				String password = null;
				if(args.length <= 3) {
					if(EiraIRC.instance.getConnectionCount() > 1) {
						Utils.sendLocalizedMessage(sender, "irc.specifyServer");
						throw new WrongUsageException("EiraIRC:irc.commands.nickserv", commandName);
					} else {
						connection = EiraIRC.instance.getDefaultConnection();
						if(connection == null) {
							Utils.sendLocalizedMessage(sender, "irc.general.notConnected", "IRC");
							return true;
						}
					}
					username = args[1];
					password = args[2];
				} else {
					String host = args[1];
					connection = EiraIRC.instance.getConnection(host);
					if(connection == null) {
						Utils.sendLocalizedMessage(sender, "irc.general.notConnected", host);
						return true;
					}
					username = args[2];
					password = args[3];
				}
				ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
				serverConfig.setNickServ(username, password);
				ConfigurationHandler.save();
				Utils.doNickServ(connection, serverConfig);
				Utils.sendLocalizedMessage(sender, "irc.basic.nickServUpdated", connection.getHost());
			}
			return true;
		} else if(cmd.equals("disconnect")) { // [serv]irc disconnect [target]
			if(serverSide && !Utils.isOP(sender)) {
				Utils.sendLocalizedMessage(sender, "irc.general.noPermission");
				return true;
			}
			if(args.length <= 1) {
				if(EiraIRC.instance.getConnectionCount() == 0) {
					Utils.sendLocalizedMessage(sender, "irc.general.notConnected", "IRC");
					return true;
				}
				for(IRCConnection connection : EiraIRC.instance.getConnections()) {
					connection.disconnect(Utils.getQuitMessage(connection));
				}
				EiraIRC.instance.clearConnections();
				Utils.sendLocalizedMessage(sender, "irc.basic.disconnecting", "IRC");
				return true;
			} else {
				String host = args[1];
				IRCConnection connection = EiraIRC.instance.getConnection(host);
				if(connection == null) {
					Utils.sendLocalizedMessage(sender, "irc.general.notConnected", host);
					return true;
				}
				Utils.sendLocalizedMessage(sender, "irc.basic.disconnecting", host);
				connection.disconnect(Utils.getQuitMessage(connection));
			}
			return true;
		} else if(cmd.equals("nick")) { // [serv]irc nick [target] <nick>
			if(serverSide && !Utils.isOP(sender)) {
				Utils.sendLocalizedMessage(sender, "irc.general.noPermission");
				return true;
			}
			if(args.length <= 1) {
				throw new WrongUsageException("EiraIRC:irc.commands.nick", commandName);
			}
			if(args.length <= 2) {
				String nick = args[1];
				Utils.sendLocalizedMessage(sender, "irc.basic.changingNick", "Global", nick);
				GlobalConfig.nick = nick;
				for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
					if(serverConfig.getNick() == null || serverConfig.getNick().isEmpty()) {
						IRCConnection connection = EiraIRC.instance.getConnection(serverConfig.getHost());
						if(connection != null) {
							connection.nick(nick);
						}
					}
				}
			} else {
				Object target = Utils.resolveIRCTarget(args[1], true, false, false, false, false, false);
				if(target instanceof IRCTargetError) {
					switch((IRCTargetError) target) {
					case InvalidTarget: Utils.sendLocalizedMessage(sender, "irc.target.invalidTarget");
						break;
					case ServerNotFound: Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[1]);
						break;
					default: Utils.sendLocalizedMessage(sender, "irc.target.unknownError");
						break;
					}
					return true;
				}
				ServerConfig serverConfig = (ServerConfig) target;
				String nick = args[2];
				Utils.sendLocalizedMessage(sender, "irc.basic.changingNick", serverConfig.getHost(), nick);
				serverConfig.setNick(nick);
				IRCConnection connection = EiraIRC.instance.getConnection(serverConfig.getHost());
				if(connection != null) {
					connection.nick(nick);
				}
			}
			ConfigurationHandler.save();
			return true;
		} else if(cmd.equals("join")) { // [serv]irc join <target> [password]
			if(serverSide && !Utils.isOP(sender)) {
				Utils.sendLocalizedMessage(sender, "irc.general.noPermission");
				return true;
			}
			if(args.length <= 1) {
				throw new WrongUsageException("EiraIRC:irc.commands.join", commandName);
			}
			Object target = Utils.resolveIRCTarget(args[1], false, true, true, false, false, false);
			if(target instanceof IRCTargetError) {
				switch((IRCTargetError) target) {
				case NotConnected: Utils.sendLocalizedMessage(sender, "irc.target.notConnected", args[1]);
					break;
				case ServerNotFound: Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[1]);
					break;
				case SpecifyServer: Utils.sendLocalizedMessage(sender, "irc.target.specifyServer");
					break;
				default: Utils.sendLocalizedMessage(sender, "irc.target.unknown");
					break;
				}
				return true;
			}
			ChannelConfig channelConfig = (ChannelConfig) target;
			channelConfig.setAutoJoin(true);
			String password = null;
			if(args.length > 2) {
				password = args[2];
			}
			IRCConnection connection = EiraIRC.instance.getConnection(channelConfig.getServerConfig().getHost());
			Utils.sendLocalizedMessage(sender, "irc.basic.joiningChannel", channelConfig.getName(), connection.getHost());
			connection.join(channelConfig.getName(), password);
			return true;
		} else if(cmd.equals("leave") || cmd.equals("part")) { // [serv]irc (leave|part) <target>
			if(serverSide && !Utils.isOP(sender)) {
				Utils.sendLocalizedMessage(sender, "irc.general.noPermission");
				return true;
			}
			if(args.length <= 1) {
				throw new WrongUsageException("EiraIRC:irc.commands.who", commandName);
			}
			Object target = Utils.resolveIRCTarget(args[1], false, false, true, true, false, false);
			if(target instanceof IRCTargetError) {
				switch((IRCTargetError) target) {
				case ChannelNotFound:
					break;
				case InvalidTarget:
					break;
				case NotConnected:
					break;
				case ServerNotFound:
					break;
				case SpecifyServer:
					break;
				case NotOnChannel:
					break;
				default:
					break;
				}
				return true;
			}
			IRCChannel channel = (IRCChannel) target;
			Utils.getServerConfig(channel.getConnection()).getChannelConfig(channel).setAutoJoin(false);
			Utils.sendLocalizedMessage(sender, "irc.basic.leavingChannel", channel.getName(), channel.getConnection().getHost());
			channel.getConnection().part(channel.getName());
			return true;
		} else if(cmd.equals("who")) { // [serv]irc who [target]
			if(args.length <= 1) {
				for(IRCConnection connection : EiraIRC.instance.getConnections()) {
					for(IRCChannel channel : connection.getChannels()) {
						Utils.sendUserList(sender, connection, channel);
					}
				}
			} else {
				Object target = Utils.resolveIRCTarget(args[1], true, true, true, true, false, false);
				if(target instanceof IRCTargetError) {
					switch((IRCTargetError) target) {
					case ChannelNotFound: Utils.sendLocalizedMessage(sender, "irc.target.channelNotFound", args[1]);
						break;
					case InvalidTarget: Utils.sendLocalizedMessage(sender, "irc.target.invalidTarget");
						break;
					case NotConnected: Utils.sendLocalizedMessage(sender, "irc.target.notConnected", args[1]);
						break;
					case NotOnChannel: Utils.sendLocalizedMessage(sender, "irc.target.notOnChannel", args[1]);
						break;
					case ServerNotFound: Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[1]);
						break;
					case SpecifyServer: Utils.sendLocalizedMessage(sender, "irc.target.specifyServer");
						break;
					default: Utils.sendLocalizedMessage(sender, "irc.target.unknownError");
						break;
					}
					return true;
				}
				if(target instanceof IRCConnection) {
					IRCConnection connection = (IRCConnection) target;
					for(IRCChannel channel : connection.getChannels()) {
						Utils.sendUserList(sender, connection, channel);
					}
				} else if (target instanceof IRCChannel) {
					IRCChannel channel = (IRCChannel) target;
					Utils.sendUserList(sender, channel.getConnection(), channel);
				}
			}
			return true;
		} else if(cmd.equals("msg")) { // [serv]irc msg <target> <text>
			if(!GlobalConfig.allowPrivateMessages) {
				Utils.sendLocalizedMessage(sender, "irc.msg.disabled");
				return true;
			}
			if(args.length <= 2) {
				throw new WrongUsageException("EiraIRC:irc.commands.msg", commandName);
			}
			Object target = Utils.resolveIRCTarget(args[1], false, false, false, false, true, serverSide);
			if(target instanceof IRCTargetError) {
				switch((IRCTargetError) target) {
				case NotConnected: Utils.sendLocalizedMessage(sender, "irc.target.notConnected", args[1]);
					break;
				case NotOnChannel: Utils.sendLocalizedMessage(sender, "irc.target.notOnChannel", args[1]);
					break;
				case ServerNotFound: Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[1]);
					break;
				case SpecifyServer: Utils.sendLocalizedMessage(sender, "irc.target.specifyServer");
					break;
				case UserNotFound: Utils.sendLocalizedMessage(sender, "irc.target.userNotFound", args[1]);
					break;
				default:
					break;
				}
				return true;
			}
			IRCUser targetUser = (IRCUser) target;
			String message = "";
			for(int i = 2; i < args.length; i++) {
				message += " " + args[i];
			}
			message = message.trim();
			if(message.isEmpty()) {
				throw new WrongUsageException("EiraIRC:irc.commands.msg", commandName);
			}
			targetUser.getConnection().sendPrivateMessage(targetUser, "<" + Utils.getAliasForPlayer((EntityPlayer) sender) + "> " + message);
			String mcMessage = "[-> " + targetUser.getNick() + "] <" + Utils.getColorAliasForPlayer((EntityPlayer) sender) + "> " + message;
			Utils.sendUnlocalizedMessage(sender, mcMessage);
			return true;
		} else if(cmd.equals("config")) { // [serv]irc config global|<host> <option> [value]
			if(args.length <= 2) {
				throw new WrongUsageException("EiraIRC:irc.commands.config", commandName);
			}
			String target = args[1];
			String config = args[2];
			if(args.length > 3) {
				ConfigurationHandler.handleConfigCommand(sender, target, config, args[3]);
			} else {
				ConfigurationHandler.handleConfigCommand(sender, target, config);
			}
			return true;
		} else if(cmd.equals("help")) { // [serv]irc help <topic>
			if(args.length <= 1) {
				Utils.sendLocalizedMessage(sender, "irc.help.validTopics");
				Utils.sendLocalizedMessage(sender, "irc.help.topicList");
				return true;
			}
			String topic = args[1];
			if(topic.equals("alias")) {
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Aliases are a way to alter the display names of certain users.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "They can be used for things like clan tags, for roleplay purposes or simply to assign another name to a player.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Aliases only affect the chat, including emotes and IRC messages.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "To prevent abuse, they can only be set by OPs and have to be enabled in the config file.");
			} else if(topic.equals("color")) {
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Name colors are an easy way to distinguish between players in the chat.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "The colors are limited to Minecraft's vanilla chat colors and are mostly similar to wool colors.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "OPs can disallow certain colors by putting them on the blacklist or disable this function altogether in the config.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "The config also has the two options opColor and ircColor, which can be set to none if not wanted.");
			} else if(topic.equals("msg")) {
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "On the serverside, private messages can only be sent to users in the same channel as the bot in order to prevent abuse.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Private messages work both ways - IRC users can use the bot's MSG command to communicate with a specific player.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "The private chat functionality can be disabled in the config.");
			} else if(topic.equals("config")) {
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Whenever the config is mentioned, it usually refers to either the config file or the /irc config command.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "For most things, the command works just fine. Some config options can only be changed in the config file itself.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "This is either out of technical reasons or to prevent possible abuse.");
			} else if(topic.equals("commands")) {
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "You can see all the possible commands by typing /irc and pressing enter or by looping through them with the TAB key.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "The most commonly used commands are: ");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "* connect, twitch, join, msg, who, nick");
			} else if(topic.equals("twitch")) {
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Using the /irc twitch command, you can easily connect your client or server to your twitch chat.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "For that purpose, you need to specify your twitch username and oauth key.");
				Utils.sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Keep in mind that Minecraft will show the oauth key in readable form while typing!");
			} else {
				Utils.sendLocalizedMessage(sender, "irc.help.invalidTopic", topic);
				Utils.sendLocalizedMessage(sender, "irc.help.topicList");
			}
			return true;
		} else if(cmd.equals("list")) { // [serv]irc list
			if(EiraIRC.instance.getConnectionCount() == 0) {
				Utils.sendLocalizedMessage(sender, "irc.general.notConnected", "IRC");
				return true;
			}
			Utils.sendLocalizedMessage(sender, "irc.list.activeConnections");
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				String channels = "";
				for(IRCChannel channel : connection.getChannels()) {
					if(channels.length() > 0) {
						channels += ", ";
					}
					channels += channel.getName();
				}
				Utils.sendUnlocalizedMessage(sender, " * " + connection.getHost() + " (" + channels + ")");
			}
			return true;
		} else if(cmd.equals("op") || cmd.equals("deop") || cmd.equals("voice") || cmd.equals("devoice") || cmd.equals("kick") || cmd.equals("ban") || cmd.equals("unban") || cmd.equals("umode")) {
			if(args.length <= 2) {
				throw new WrongUsageException("EiraIRC:irc.commands.interop." + cmd);
			}
			Object target = Utils.resolveIRCTarget(args[1], false, false, true, true, false, false);
			IRCChannel targetChannel = null;
			IRCUser targetUser = null;
			if(target instanceof IRCTargetError) {
				switch((IRCTargetError) target) {
					case ChannelNotFound: Utils.sendLocalizedMessage(sender, "irc.target.channelNotFound", args[2]); break;
					case InvalidTarget: Utils.sendLocalizedMessage(sender, "irc.target.invalidTarget"); break;
					case NotConnected: Utils.sendLocalizedMessage(sender, "irc.target.notConnected", args[2]); break;
					case NotOnChannel: Utils.sendLocalizedMessage(sender, "irc.target.notOnChannel", args[2]); break;
					case ServerNotFound: Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[2]); break;
					case SpecifyServer: Utils.sendLocalizedMessage(sender, "irc.target.specifyServer"); break;
					default: Utils.sendLocalizedMessage(sender, "irc.target.unknown"); break;
				}
				return true;
			}
			targetChannel = (IRCChannel) target;
			target = Utils.resolveIRCTarget(args[2], false, false, false, false, true, false);
			if(target instanceof IRCTargetError) {
				switch((IRCTargetError) target) {
					case InvalidTarget: Utils.sendLocalizedMessage(sender, "irc.target.invalidTarget"); break;
					case NotConnected: Utils.sendLocalizedMessage(sender, "irc.target.notConnected", args[2]); break;
					case NotOnChannel: Utils.sendLocalizedMessage(sender, "irc.target.notOnChannel", args[2]); break;
					case ServerNotFound: Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[2]); break;
					case SpecifyServer: Utils.sendLocalizedMessage(sender, "irc.target.specifyServer"); break;
					case UserNotFound: Utils.sendLocalizedMessage(sender, "irc.target.userNotFound", args[2]); break;
					default: Utils.sendLocalizedMessage(sender, "irc.target.unknown"); break;
				}
				return true;
			}
			targetUser = (IRCUser) target;
			if(!checkInterOP(sender, targetChannel, serverSide)) {
				return true;
			}
			if(cmd.equals("kick")) {
				String reason = null;
				if(args.length > 3) {
					reason = args[3];
				}
				targetChannel.getConnection().kick(targetChannel.getName(), targetUser.getNick(), reason);
			} else if(cmd.equals("ban")) {
				String reason = null;
				if(args.length > 3) {
					reason = args[3];
				}
				targetChannel.getConnection().mode(targetChannel.getName(), "+b", targetUser.getUsername());
				targetChannel.getConnection().kick(targetChannel.getName(), targetUser.getNick(), reason);
			} else if(cmd.equals("unban")) {
				targetChannel.getConnection().mode(targetChannel.getName(), "-b", targetUser.getUsername());
			} else if(cmd.equals("op")) {
				targetChannel.getConnection().mode(targetChannel.getName(), "+o", targetUser.getNick());
			} else if(cmd.equals("deop")) {
				targetChannel.getConnection().mode(targetChannel.getName(), "-o", targetUser.getNick());
			} else if(cmd.equals("voice")) {
				targetChannel.getConnection().mode(targetChannel.getName(), "+v", targetUser.getNick());
			} else if(cmd.equals("devoice")) {
				targetChannel.getConnection().mode(targetChannel.getName(), "-v", targetUser.getNick());
			} else if(cmd.equals("umode")) {
				if(args.length <= 3) {
					throw new WrongUsageException("EiraIRC:irc.commands.interop.umode");
				}
				targetChannel.getConnection().mode(targetChannel.getName(), args[3], targetUser.getUsername());
			}
		} else if(cmd.equals("topic") || cmd.equals("mode")) {
			if(args.length <= 2) {
				throw new WrongUsageException("EiraIRC:irc.commands.interop." + cmd);
			}
			IRCChannel targetChannel = null;
			Object target = Utils.resolveIRCTarget(args[1], false, false, true, true, false, false);
			if(target instanceof IRCTargetError) {
				switch((IRCTargetError) target) {
					case ChannelNotFound: Utils.sendLocalizedMessage(sender, "irc.target.channelNotFound", args[2]); break;
					case InvalidTarget: Utils.sendLocalizedMessage(sender, "irc.target.invalidTarget"); break;
					case NotConnected: Utils.sendLocalizedMessage(sender, "irc.target.notConnected", args[2]); break;
					case NotOnChannel: Utils.sendLocalizedMessage(sender, "irc.target.notOnChannel", args[2]); break;
					case ServerNotFound: Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[2]); break;
					case SpecifyServer: Utils.sendLocalizedMessage(sender, "irc.target.specifyServer"); break;
					default: Utils.sendLocalizedMessage(sender, "irc.target.unknown"); break;
				}
				return true;
			}
			targetChannel = (IRCChannel) target;
			if(!checkInterOP(sender, targetChannel, serverSide)) {
				return true;
			}
			if(cmd.equals("topic")) {
				targetChannel.getConnection().topic(targetChannel.getName(), args[2]);
			} else if(cmd.equals("mode")) {
				targetChannel.getConnection().mode(targetChannel.getName(), args[2]);
			}
		}
		return false;
	}
	
	private static boolean checkInterOP(ICommandSender sender, IRCChannel channel, boolean serverSide) {
		if(serverSide && !Utils.isOP(sender)) {
			Utils.sendLocalizedMessage(sender, "irc.general.noPermission");
			return false;
		}
		if(!GlobalConfig.interOp) {
			Utils.sendLocalizedMessage(sender, "irc.interop.disabled");
			return false;
		}
		return true;
	}
	
	public static List<String> addTabCompletionOptions(String cmd, ICommandSender sender, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			list.add("msg");
			list.add("who");
			if(cmd.equals("irc")) {
				list.add("color");
				list.add("alias");
			}
			list.add("join");
			list.add("leave");
			list.add("connect");
			list.add("twitch");
			list.add("nickserv");
			list.add("disconnect");
			list.add("list");
			list.add("nick");
			list.add("config");
			list.add("help");
			if(GlobalConfig.interOp) {
				list.add("op");
				list.add("deop");
				list.add("kick");
				list.add("ban");
				list.add("unban");
				list.add("voice");
				list.add("devoice");
				list.add("mode");
				list.add("topic");
			}
		} else if(args.length == 2) {
			if(args[0].equals("color")) {
				Utils.addValidColorsToList(list);
				list.add("none");
			} else if(args[0].equals("help")) {
				list.add("alias");
				list.add("color");
				list.add("mode");
				list.add("commands");
				list.add("msg");
			} else if(args[0].equals("config")) {
				list.add("global");
				Utils.addConnectionsToList(list);
			} else if(args[0].equals("disconnect")) {
				Utils.addConnectionsToList(list);
			} else if(args[0].equals("connect")) {
				list.add("irc.esper.net");
			}
		} else if(args.length == 3) {
			if(args[0].equals("config")) {
				if(args[1].equals("global")) {
					GlobalConfig.addOptionsToList(list);
				} else if(args[1].contains("#")) {
					ChannelConfig.addOptionsToList(list);
				} else {
					ServerConfig.addOptionstoList(list);
				}
			} else if(args[0].equals("alias")) {
				list.add("none");
			}
		} else if(args.length == 4) {
			if(args[0].equals("config")) {
				if(args[1].equals("global")) {
					GlobalConfig.addValuesToList(list, args[2]);
				} else if(args[1].contains("#")) {
					ChannelConfig.addValuesToList(list, args[2]);
				} else {
					ServerConfig.addValuesToList(list, args[2]);
				}
			}
		}
		return list;
	}
	
	public static String[] getShiftedArgs(String[] args, String cmd) {
		String[] shiftedArgs = new String[args.length + 1];
		shiftedArgs[0] = cmd;
		for(int i = 0; i < args.length; i++) {
			shiftedArgs[i + 1] = args[i];
		}
		return shiftedArgs;
	}

	public static boolean isUsernameIndex(String[] args, int i) {
		return false;
	}

	public static void sendIRCUsage(ICommandSender sender) {
		Utils.sendLocalizedMessage(sender, "irc.general.usage", Utils.getLocalizedMessage("irc.commands.irc"));
		Utils.sendLocalizedMessage(sender, "irc.cmdlist.general");
		Utils.sendLocalizedMessage(sender, "irc.cmdlist.irc");
		Utils.sendLocalizedMessage(sender, "irc.cmdlist.special");
	}
}
