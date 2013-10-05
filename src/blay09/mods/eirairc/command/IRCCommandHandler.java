// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.EnumChatFormatting;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.Utils;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.Globals;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IRCConnection;

public class IRCCommandHandler {

	public static void sendLocalizedMessage(ICommandSender sender, String key, Object... args) {
		sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":" + key, args));
	}
	
	public static void sendUnlocalizedMessage(ICommandSender sender, String text) {
		sender.sendChatToPlayer(text);
	}
	
	public static boolean processCommand(ICommandSender sender, String[] args, boolean serverSide) {
		String commandName = serverSide ? "servirc" : "irc";
		if(args.length < 1) {
			throw new WrongUsageException("commands.irc.usage", commandName);
		}
		String cmd = args[0];
		if(cmd.equals("connect")) { // [serv]irc connect <host> [password]
			if(serverSide && !Utils.isOP(sender)) {
				sendLocalizedMessage(sender, "irc.nopermissions");
				return true;
			}
			if(args.length <= 1) {
				throw new WrongUsageException("commands.irc.usage.connect", commandName);
			}
			String host = args[1];
			if(EiraIRC.instance.isConnectedTo(host)) {
				if(serverSide) {
					sendLocalizedMessage(sender, "irc.server.alreadyConnected", host);
				} else {
					sendLocalizedMessage(sender, "irc.client.alreadyConnected", host);
				}
				return true;
			}
			sendLocalizedMessage(sender, "irc.connecting", host);
			IRCConnection connection = new IRCConnection(host, !serverSide);
			if(args.length > 2) {
				connection.getConfig().serverPassword = args[2];
				ConfigurationHandler.save();
			}
			if(connection.connect()) {
				EiraIRC.instance.addConnection(connection);				
			} else {
				ConfigurationHandler.removeServerConfig(connection.getHost());
				ConfigurationHandler.save();
				sendLocalizedMessage(sender, "irc.connectionError", host);
			}
			return true;
		} else if(cmd.equals("twitch")) { // servirc twitch <username> <oauth> OR irc twitch
			if(serverSide && !Utils.isOP(sender)) {
				sendLocalizedMessage(sender, "irc.nopermissions");
				return true;
			}
			if(!serverSide) {
				if(!ConfigurationHandler.hasServerConfig(Globals.TWITCH_SERVER) || args.length > 1) {
					sendLocalizedMessage(sender, "irc.serverOnlyCommand");
				} else {
					sendLocalizedMessage(sender, "irc.connecting", "Twitch");
					IRCConnection connection = new IRCConnection(Globals.TWITCH_SERVER, true);
					if(connection.connect()) {
						EiraIRC.instance.addConnection(connection);
					}
				}
				return true;
			} else {
				if(args.length <= 2) {
					throw new WrongUsageException("commands.irc.usage.twitch", commandName);
				}
				String username = args[1];
				String password = args[2];
				if(EiraIRC.instance.isConnectedTo(Globals.TWITCH_SERVER)) {
					if(serverSide) {
						sendLocalizedMessage(sender, "irc.server.alreadyConnected", "Twitch");
					} else {
						sendLocalizedMessage(sender, "irc.client.alreadyConnected", "Twitch");
					}
					return true;
				}
				sendLocalizedMessage(sender, "irc.connecting", "Twitch");
				IRCConnection connection = new IRCConnection(Globals.TWITCH_SERVER, !serverSide);
				connection.getConfig().nick = username;
				connection.getConfig().serverPassword = password;
				if(!connection.getConfig().channels.contains("#" + username)) {
					connection.getConfig().channels.add("#" + username);
				}
				ConfigurationHandler.save();
				if(connection.connect()) {
					EiraIRC.instance.addConnection(connection);
				}
			}
			return true;
		} else if(cmd.equals("nickserv")) { // servirc nickserv <nick> <password>
			if(serverSide && !Utils.isOP(sender)) {
				sendLocalizedMessage(sender, "irc.nopermissions");
				return true;
			}
			if(!serverSide) {
				sendLocalizedMessage(sender, "irc.serverOnlyCommand");
				return true;
			} else {
				if(args.length <= 2) {
					throw new WrongUsageException("commands.irc.usage.nickserv", commandName);
				}
				IRCConnection connection = null;
				String username = null;
				String password = null;
				if(args.length <= 3) {
					if(EiraIRC.instance.getConnectionCount() > 1) {
						sendLocalizedMessage(sender, "irc.specifyServer");
						throw new WrongUsageException("commands.irc.usage.nickserv", commandName);
					} else {
						connection = EiraIRC.instance.getDefaultConnection();
						if(connection == null) {
							sendLocalizedMessage(sender, "irc.server.notConnected", "IRC");
							return true;
						}
					}
					username = args[1];
					password = args[2];
				} else {
					String host = args[1];
					connection = EiraIRC.instance.getConnection(host);
					if(connection == null) {
						if(serverSide) {
							sendLocalizedMessage(sender, "irc.server.notConnected", host);
						} else {
							sendLocalizedMessage(sender, "irc.client.notConnected", host);
						}
						return true;
					}
					username = args[2];
					password = args[3];
				}
				connection.getConfig().nickServName = username;
				connection.getConfig().nickServPassword = password;
				connection.nickServ();
				sendLocalizedMessage(sender, "irc.nickServUpdated", connection.getHost());
			}
			return true;
		} else if(cmd.equals("disconnect")) { // [serv]irc disconnect [host]
			if(serverSide && !Utils.isOP(sender)) {
				sendLocalizedMessage(sender, "irc.nopermission");
				return true;
			}
			if(args.length <= 1) {
				if(EiraIRC.instance.getConnectionCount() == 0) {
					if(serverSide) {
						sendLocalizedMessage(sender, "irc.server.notConnected", "IRC");
					} else {
						sendLocalizedMessage(sender, "irc.client.notConnected", "IRC");
					}
					return true;
				}
				for(IRCConnection connection : EiraIRC.instance.getConnections()) {
					connection.disconnect();
				}
				EiraIRC.instance.clearConnections();
				sendLocalizedMessage(sender, "irc.disconnecting", "IRC");
				return true;
			} else {
				String host = args[1];
				IRCConnection connection = EiraIRC.instance.getConnection(host);
				if(connection == null) {
					if(serverSide) {
						sendLocalizedMessage(sender, "irc.server.notConnected", host);
					} else {
						sendLocalizedMessage(sender, "irc.client.notConnected", host);
					}
					return true;
				}
				sendLocalizedMessage(sender, "irc.disconnecting", host);
				connection.disconnect();
				EiraIRC.instance.removeConnection(connection);
			}
			return true;
		} else if(cmd.equals("nick")) { // [serv]irc nick [host] <nick>
			if(serverSide && !Utils.isOP(sender)) {
				sendLocalizedMessage(sender, "irc.nopermissions");
				return true;
			}
			if(args.length <= 1) {
				throw new WrongUsageException("commands.irc.usage.nick", commandName);
			}
			try {
				if(args.length <= 2) {
					String nick = args[1];
					GlobalConfig.nick = nick;
					for(IRCConnection connection : EiraIRC.instance.getConnections()) {
						if(connection.getConfig().nick.isEmpty()) {
							connection.changeNick(nick);
						}
					}
					sendLocalizedMessage(sender, "irc.nickChange", "Global", nick);
				} else {
					String host = args[1];
					String nick = args[2];
					IRCConnection connection = EiraIRC.instance.getConnection(host);
					if(connection != null) {
						connection.getConfig().nick = nick;
						connection.changeNick(nick);
						sendLocalizedMessage(sender, "irc.nickChange", host, nick);
					} else {
						if(serverSide) {
							sendLocalizedMessage(sender, "irc.server.notConnected", host);
						} else {
							sendLocalizedMessage(sender, "irc.client.notConnected", host);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else if(cmd.equals("join")) { // [serv]irc join [host] <channel> [password]
			if(serverSide && !Utils.isOP(sender)) {
				sendLocalizedMessage(sender, "irc.nopermission");
				return true;
			}
			if(args.length <= 1) {
				throw new WrongUsageException("commands.irc.usage.join", commandName);
			}
			IRCConnection connection = null;
			String channel = null;
			String password = null;
			if(args.length <= 2 || (args.length <= 3 && args[1].startsWith("#"))) {
				channel = args[1];
				if(args.length > 2) {
					password = args[2];
				}
				if(EiraIRC.instance.getConnectionCount() > 1) {
					sendLocalizedMessage(sender, "irc.specifyServer");
					throw new WrongUsageException("commands.irc.usage.join", commandName);
				} else {
					connection = EiraIRC.instance.getDefaultConnection();
					if(connection == null) {
						if(serverSide) {
							sendLocalizedMessage(sender, "irc.server.notConnected", "IRC");
						} else {
							sendLocalizedMessage(sender, "irc.client.notConnected", "IRC");
						}
						return true;
					}
				}
			} else {
				String host = args[1];
				channel = args[2];
				if(args.length > 3) {
					password = args[3];
				}
				connection = EiraIRC.instance.getConnection(host);
				if(connection == null) {
					if(serverSide) {
						sendLocalizedMessage(sender, "irc.server.notConnected", host);
					} else {
						sendLocalizedMessage(sender, "irc.client.notConnected", host);
					}
					return true;
				}
			}
			try {
				sendLocalizedMessage(sender, "irc.joinChannel", channel, connection.getHost());
				connection.joinChannel(channel, password);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else if(cmd.equals("leave")) { // [serv]irc leave [host] <channel>
			if(serverSide && !Utils.isOP(sender)) {
				sendLocalizedMessage(sender, "irc.nopermission");
				return true;
			}
			if(args.length <= 1) {
				throw new WrongUsageException("commands.irc.usage.leave", commandName);
			}
			IRCConnection connection = null;
			String channel = null;
			if(args.length <= 2) {
				channel = args[1];
				if(EiraIRC.instance.getConnectionCount() > 1) {
					for(IRCConnection con : EiraIRC.instance.getConnections()) {
						if(con.getConfig().channels.contains(channel)) {
							if(connection != null) {
								connection = null;
								break;
							}
							connection = con;
						}
					}
					if(connection == null) {
						sendLocalizedMessage(sender, "irc.specifyServer");
						throw new WrongUsageException("commands.irc.usage.leave", commandName);
					}
				} else {
					connection = EiraIRC.instance.getDefaultConnection();
					if(connection == null) {
						if(serverSide) {
							sendLocalizedMessage(sender, "irc.server.notConnected", "IRC");
						} else {
							sendLocalizedMessage(sender, "irc.client.notConnected", "IRC");
						}
						return true;
					}
				}
			} else {
				String host = args[1];
				channel = args[2];
				connection = EiraIRC.instance.getConnection(host);
				if(connection == null) {
					if(serverSide) {
						sendLocalizedMessage(sender, "irc.server.notConnected", host);
					} else {
						sendLocalizedMessage(sender, "irc.client.notConnected", host);
					}
					return true;
				}
			}
			try {
				sendLocalizedMessage(sender, "irc.leaveChannel", channel, connection.getHost());
				connection.leaveChannel(channel);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else if(cmd.equals("who")) { // [serv]irc who [host] <channel>
			IRCConnection connection = null;
			String channel = null;
			if(args.length <= 1) {
				if(EiraIRC.instance.getConnectionCount() > 1) {
					sendLocalizedMessage(sender, "irc.specifyServer");
					throw new WrongUsageException("commands.irc.usage.who", commandName);
				} else {
					connection = EiraIRC.instance.getDefaultConnection();
					if(connection == null) {
						if(serverSide) {
							sendLocalizedMessage(sender, "irc.server.notConnected", "IRC");
						} else {
							sendLocalizedMessage(sender, "irc.client.notConnected", "IRC");
						}
						return true;
					}
				}
				if(connection.getConfig().channels.size() > 1) {
					sendLocalizedMessage(sender, "irc.specifyChannel");
					throw new WrongUsageException("commands.irc.usage.who", commandName);
				} else {
					channel = connection.getConfig().channels.get(0);
				}
			} else if(args.length <= 2) {
				channel = args[1];
				if(EiraIRC.instance.getConnectionCount() > 1) {
					for(IRCConnection con : EiraIRC.instance.getConnections()) {
						if(con.getConfig().channels.contains(channel)) {
							if(connection != null) {
								connection = null;
								break;
							}
							connection = con;
						}
					}
					if(connection == null) {
						sendLocalizedMessage(sender, "irc.specifyServer");
						throw new WrongUsageException("commands.irc.usage.who", commandName);
					}
				} else {
					connection = EiraIRC.instance.getDefaultConnection();
					if(connection == null) {
						if(serverSide) {
							sendLocalizedMessage(sender, "irc.server.notConnected", "IRC");
						} else {
							sendLocalizedMessage(sender, "irc.client.notConnected", "IRC");
						}
						return true;
					}
				}
			} else {
				String host = args[1];
				channel = args[2];
				connection = EiraIRC.instance.getConnection(host);
				if(connection == null) {
					if(serverSide) {
						sendLocalizedMessage(sender, "irc.server.notConnected", host);
					} else {
						sendLocalizedMessage(sender, "irc.client.notConnected", host);
					}
					return true;
				}
			}
			if(!connection.getConfig().channels.contains(channel)) {
				sendLocalizedMessage(sender, "irc.channelNotFound", channel);
				return true;
			}
			List<String> userList = connection.getUserList(channel);
			if(userList.size() == 0) {
				sendLocalizedMessage(sender, "irc.noUsersOnline", channel, connection.getHost());
				return true;
			}
			sendLocalizedMessage(sender, "irc.usersOnline", userList.size(), channel, connection.getHost());
			String s = "* ";
			for(int i = 0; i < userList.size(); i++) {
				String user = userList.get(i);
				if(s.length() + user.length() > Globals.CHAT_MAX_LENGTH) {
					sendUnlocalizedMessage(sender, s);
					s = "* ";
				}
				if(s.length() > 2) {
					s += ", ";
				}
				s += userList.get(i);
			}
			if(s.length() > 2) {
				sendUnlocalizedMessage(sender, s);
			}
			return true;
		} else if(cmd.equals("mode")) { // [serv]irc mode [host] [channel] [flags]
			IRCConnection connection = null;
			String channel = null;
			String flags = null;
			if(args.length <= 1) {
				if(args.length > 1) {
					flags = args[1];
				}
				if(EiraIRC.instance.getConnectionCount() > 1) {
					sendLocalizedMessage(sender, "irc.specifyServer");
					throw new WrongUsageException("commands.irc.usage.mode", commandName);
				} else {
					connection = EiraIRC.instance.getDefaultConnection();
					if(connection == null) {
						if(serverSide) {
							sendLocalizedMessage(sender, "irc.server.notConnected", "IRC");
						} else {
							sendLocalizedMessage(sender, "irc.client.notConnected", "IRC");
						}
						return true;
					}
				}
				if(connection.getConfig().channels.size() > 1) {
					sendLocalizedMessage(sender, "irc.specifyChannel");
					throw new WrongUsageException("commands.irc.usage.mode", commandName);
				} else {
					channel = connection.getConfig().channels.get(0);
				}
			} else if(args.length <= 2) {
				channel = args[1];
				if(args.length > 2) {
					flags = args[2];
				}
				if(EiraIRC.instance.getConnectionCount() > 1) {
					for(IRCConnection con : EiraIRC.instance.getConnections()) {
						if(con.getConfig().channels.contains(channel)) {
							if(connection != null) {
								connection = null;
								break;
							}
							connection = con;
						}
					}
					if(connection == null) {
						sendLocalizedMessage(sender, "irc.specifyServer");
						throw new WrongUsageException("commands.irc.usage.mode", commandName);
					}
				} else {
					connection = EiraIRC.instance.getDefaultConnection();
					if(connection == null) {
						if(serverSide) {
							sendLocalizedMessage(sender, "irc.server.notConnected", "IRC");
						} else {
							sendLocalizedMessage(sender, "irc.client.notConnected", "IRC");
						}
						return true;
					}
				}
			} else {
				String host = args[1];
				channel = args[2];
				if(args.length > 3) {
					flags = args[3];
				}
				connection = EiraIRC.instance.getConnection(host);
				if(connection == null) {
					if(serverSide) {
						sendLocalizedMessage(sender, "irc.server.notConnected", host);
					} else {
						sendLocalizedMessage(sender, "irc.client.notConnected", host);
					}
					return true;
				}
			}
			if(!connection.getConfig().channels.contains(channel)) {
				sendLocalizedMessage(sender, "irc.channelNotFound", channel);
				return true;
			}
			if(flags != null) {
				connection.getConfig().alterChannelFlags(channel, flags);
				sendLocalizedMessage(sender, "irc.channelModeChange", flags, channel, connection.getHost());
			} else {
				flags = connection.getConfig().channelFlags.get(channel);
				sendLocalizedMessage(sender, "irc.channelMode", channel, connection.getHost(), flags);
			}
			return true;
		} else if(cmd.equals("msg")) { // [serv]irc msg [host] <nick> <text>
			if(!GlobalConfig.allowPrivateMessages) {
				sendLocalizedMessage(sender, "irc.msgDisbaled");
				return true;
			}
			if(args.length <= 2) {
				throw new WrongUsageException("commands.irc.usage.msg", commandName);
			}
			IRCConnection connection = null;
			String nick = null;
			int msgStartIdx = 2;
			if(args.length <= 3) {
				nick = args[1];
				if(EiraIRC.instance.getConnectionCount() > 1) {
					boolean multipleFound = false;
					for(IRCConnection con : EiraIRC.instance.getConnections()) {
						for(String channel : con.getConfig().channels) {
							if(con.getUserList(channel).contains(nick)) {
								if(connection != null) {
									multipleFound = true;
									connection = null;
									break;
								}
								connection = con;
							}
						}
						if(multipleFound) {
							break;
						}
					}
					if(connection == null) {
						sendLocalizedMessage(sender, "irc.specifyServer");
						throw new WrongUsageException("commands.irc.usage.msg", commandName);
					}
				} else {
					connection = EiraIRC.instance.getDefaultConnection();
					if(connection == null) {
						if(serverSide) {
							sendLocalizedMessage(sender, "irc.server.notConnected", "IRC");
						} else {
							sendLocalizedMessage(sender, "irc.client.notConnected", "IRC");
						}
						return true;
					}
				}
			} else {
				String host = args[1];
				nick = args[2];
				msgStartIdx = 3;
				connection = EiraIRC.instance.getConnection(host);
				if(connection == null) {
					if(serverSide) {
						sendLocalizedMessage(sender, "irc.server.notConnected", host);
					} else {
						sendLocalizedMessage(sender, "irc.client.notConnected", host);
					}
					return true;
				}
			}
			if(serverSide) {
				boolean foundNick = false;
				for(String channel : connection.getConfig().channels) {
					if(connection.getUserList(channel).contains(nick)) {
						foundNick = true;
						break;
					}
				}
				if(!foundNick) {
					sendLocalizedMessage(sender, "irc.msgInvalidTarget", foundNick);
					return true;
				}
			}
			String message = "";
			for(int i = msgStartIdx; i < args.length; i++) {
				message += " " + args[i];
			}
			message = message.trim();
			if(message.isEmpty()) {
				throw new WrongUsageException("commands.irc.usage.msg", commandName);
			}
			connection.sendPrivateMessage(nick, message);
			String mcMessage = "[" + nick + "] <" + Minecraft.getMinecraft().thePlayer.username + "> " + message;
			sendUnlocalizedMessage(sender, mcMessage);
			return true;
		} else if(cmd.equals("config")) { // [serv]irc config global|<host> <option> <value>
			if(args.length <= 3) {
				throw new WrongUsageException("commands.irc.usage.config", commandName);
			}
			String host = args[1];
			String config = args[2];
			String value = args[3];
			if(host.equals("global")) {
				if(config.equals("opColor")) {
					GlobalConfig.opColor = value;
				} else if(config.equals("ircColor")) {
					GlobalConfig.ircColor = value;
				} else if(config.equals("enableNameColors")) {
					GlobalConfig.enableNameColors = Boolean.parseBoolean(value);
				} else if(config.equals("enableAliases")) {
					sendLocalizedMessage(sender, "irc.configNoAbuse");
					return true;
				} else if(config.equals("allowPrivateMessages")) {
					GlobalConfig.allowPrivateMessages = Boolean.parseBoolean(value);
				} else if(config.equals("showDeathMessages")) {
					GlobalConfig.showDeathMessages = Boolean.parseBoolean(value);
				} else if(config.equals("showIRCJoinLeave")) {
					GlobalConfig.showIRCJoinLeave = Boolean.parseBoolean(value);
				} else if(config.equals("showMinecraftJoinLeave")) {
					GlobalConfig.showMinecraftJoinLeave = Boolean.parseBoolean(value);
				} else if(config.equals("showNickChanges")) {
					GlobalConfig.showNickChanges = Boolean.parseBoolean(value);
				} else if(config.equals("enableLinkFilter")) {
					GlobalConfig.enableLinkFilter = Boolean.parseBoolean(value);
				} else if(config.equals("persistentConnection")) {
					GlobalConfig.persistentConnection = Boolean.parseBoolean(value);
				} else {
					sendLocalizedMessage(sender, "irc.invalidConfigChange", "Global", config);
					return true;
				}
				ConfigurationHandler.save();
				sendLocalizedMessage(sender, "irc.configChange", "Global", config, value);
			} else {
				IRCConnection connection = EiraIRC.instance.getConnection(host);
				if(connection == null) {
					if(serverSide) {
						sendLocalizedMessage(sender, "irc.server.notConnected", host);
					} else {
						sendLocalizedMessage(sender, "irc.client.notConnected", host);
					}
					return true;
				}
				ServerConfig serverConfig = connection.getConfig();
				if(config.equals("allowPrivateMessages")) {
					serverConfig.allowPrivateMessages = Boolean.parseBoolean(value);
				} else if(config.equals("autoConnect")) {
					serverConfig.autoConnect = Boolean.parseBoolean(value);
				} else if(config.equals("saveCredentials")) {
					serverConfig.saveCredentials = Boolean.parseBoolean(value);
				} else {
					sendLocalizedMessage(sender, "irc.invalidConfigChange", host, config);
					return true;
				}
				ConfigurationHandler.save();
				sendLocalizedMessage(sender, "irc.configChange", host, config, value);
			}
			return true;
		} else if(cmd.equals("help")) { // [serv]irc help <topic>
			if(args.length <= 1) {
				sendLocalizedMessage(sender, "irc.helpValidTopics");
				sendLocalizedMessage(sender, "irc.helpTopics");
				return true;
			}
			String topic = args[1];
			if(topic.equals("alias")) {
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Aliases are a way to alter the display names of certain users.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "They can be used for things like clan tags, for roleplay purposes or simply to assign another name to a player.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Aliases only affect the chat, including emotes and IRC messages.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "To prevent abuse, they can only be set by OPs and have to be enabled in the config file.");
			} else if(topic.equals("color")) {
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Name colors are an easy way to distinguish between players in the chat.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "The colors are disabled to Minecraft's vanilla chat colors and are mostly similar to wool colors.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "OPs can disallow certain colors by putting them on the blacklist or disable this function altogether in the config.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "The config also has the two options opColor and ircColor, which can be set to nothing if not wanted.");
			} else if(topic.equals("mode")) {
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Each channel this mod is connected to gets assigned some default client mode flags.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "These are not to be confused with IRC mode flags, as they only define how a channel is handled by this mod.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Usually, you won't have to mess with these as the global config can easily do the same, so this is more for special needs.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Visit <...> for a more indepth explanation on each of the mode flags.");
			} else if(topic.equals("msg")) {
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "On the serverside, private messages can only be sent to users in the same channel as the bot in order to prevent abuse.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Private messages work both ways - IRC users can use the bot's MSG command to communicate with a specific player.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "The private chat functionality can be disabled in the config.");
			} else if(topic.equals("config")) {
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Whenever the config is mentioned, it usually refers to either the config file or the /irc config command.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "For most things, the command works just fine. Some config options can only be changed in the config file itself.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "This is either out of technical reasons or to prevent possible abuse.");
			} else if(topic.equals("commands")) {
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "You can see all the possible commands by typing /irc and pressing enter or by looping through them with the TAB key.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "The most commonly used commands are: ");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "* connect, twitch, join, msg, who, nick");
			} else if(topic.equals("twitch")) {
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Using the /irc twitch command, you can easily connect your client or server to your twitch chat.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "For that purpose, you need to specify your twitch username and password.");
				sendUnlocalizedMessage(sender, EnumChatFormatting.GREEN + "Keep in mind that Minecraft will show the password in readable form while typing!");
			} else {
				sendLocalizedMessage(sender, "irc.helpInvalidTopic", topic);
				sendLocalizedMessage(sender, "irc.helpTopics");
			}
			return true;
		} else if(cmd.equals("list")) { // [serv]irc list
			if(EiraIRC.instance.getConnectionCount() == 0) {
				if(serverSide) {
					sendLocalizedMessage(sender, "irc.server.notConnected", "IRC");
				} else {
					sendLocalizedMessage(sender, "irc.client.notConnected", "IRC");
				}
				return true;
			}
			if(serverSide) {
				sendLocalizedMessage(sender, "irc.server.activeConnections");
			} else {
				sendLocalizedMessage(sender, "irc.client.activeConnections");
			}
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				String channels = "";
				for(String channel : connection.getConfig().channels) {
					if(channels.length() > 0) {
						channels += ", ";
					}
					channels += channel;
				}
				sendUnlocalizedMessage(sender, "* " + connection.getHost() + " (" + channels + ")");
			}
			return true;
		} else if(cmd.equals("op")) { // [serv]irc op <command>
			sendUnlocalizedMessage(sender, "Not yet implemented.");
			return true;
		}
		return false;
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
			list.add("mode");
			list.add("leave");
			list.add("connect");
			list.add("twitch");
			list.add("nickserv");
			list.add("disconnect");
			list.add("list");
			list.add("nick");
			list.add("op");
			list.add("config");
			list.add("help");
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
					list.add("opColor");
					list.add("ircColor");
					list.add("enableNameColors");
					list.add("allowPrivateMessages");
					list.add("showDeathMessages");
					list.add("showIRCJoinLeave");
					list.add("showMinecraftJoinLeave");
					list.add("enableLinkFilter");
					list.add("showNickChanges");
					list.add("persistentConnection");
				} else {
					list.add("allowPrivateMessages");
					list.add("autoConnect");
				}
			} else if(args[0].equals("alias")) {
				list.add("none");
			}
		} else if(args.length == 4) {
			if(args[0].equals("config")) {
				if(args[1].equals("global")) {
					if(args[2].equals("opColor") || args[2].equals("ircColor")) {
						Utils.addValidColorsToList(list);
					} else if(args[2].equals("enableNameColors") || args[2].equals("allowPrivateMessages") || args[2].equals("showDeathMessages") || args[2].equals("showIRCJoinLeave") || args[2].equals("showMinecraftJoinLeave")) {
						Utils.addBooleansToList(list);
					}
				} else {
					if(args[2].equals("allowPrivateMessages")) {
						Utils.addBooleansToList(list);
					}
				}
			}
		}
		return list;
	}
	
}
