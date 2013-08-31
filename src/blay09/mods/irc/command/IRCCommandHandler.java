// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.EnumChatFormatting;
import blay09.mods.irc.EiraIRC;
import blay09.mods.irc.IRCConnection;
import blay09.mods.irc.Utils;
import blay09.mods.irc.config.ConfigurationHandler;
import blay09.mods.irc.config.GlobalConfig;
import blay09.mods.irc.config.Globals;
import blay09.mods.irc.config.ServerConfig;

public class IRCCommandHandler {

	public static boolean processCommand(ICommandSender sender, String[] args, boolean serverSide) {
		if(args.length < 1) {
			throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage", serverSide ? "servirc" : "irc");
		}
		String cmd = args[0];
		if(cmd.equals("connect")) {
			if(serverSide && !Utils.isOP(sender)) {
				sender.sendChatToPlayer(EnumChatFormatting.RED + sender.translateString(Globals.MOD_ID + ":irc.nopermission"));
				return true;
			}
			if(args.length < 2) {
				throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.connect", serverSide ? "servirc" : "irc");
			}
			if(EiraIRC.instance.isConnectedTo(args[1])) {
				if(serverSide) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.alreadyConnected", args[1]));
				} else {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.alreadyConnected", args[1]));
				}
				return true;
			}
			sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.connecting", args[1]));
			IRCConnection connection = new IRCConnection(args[1], !serverSide);
			if(args.length >= 3) {
				connection.getConfig().serverPassword = args[2];
				ConfigurationHandler.save();
			}
			if(connection.connect()) {
				EiraIRC.instance.addConnection(connection);				
			} else {
				ConfigurationHandler.removeServerConfig(connection.getHost());
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.connectionError", args[1]));
			}
			return true;
		} else if(cmd.equals("twitch")) {
			if(serverSide && !Utils.isOP(sender)) {
				sender.sendChatToPlayer(EnumChatFormatting.RED + sender.translateString(Globals.MOD_ID + ":irc.nopermission"));
				return true;
			}
			if(!serverSide) {
				if(ConfigurationHandler.hasServerConfig(Globals.TWITCH_SERVER) || args.length > 1) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.serverOnlyCommand"));
				} else {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.connecting", "Twitch"));
					IRCConnection connection = new IRCConnection(Globals.TWITCH_SERVER, true);
					if(connection.connect()) {
						EiraIRC.instance.addConnection(connection);
					}
				}
				return true;
			} else {
				if(args.length < 3) {
					throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.twitch", "servirc");
				}
				String username = args[1];
				String host = Globals.TWITCH_SERVER;
				if(EiraIRC.instance.isConnectedTo(host)) {
					if(serverSide) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.alreadyConnected", host));
					} else {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.alreadyConnected", host));
					}
					return true;
				}
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.connecting", host));
				IRCConnection connection = new IRCConnection(host, !serverSide);
				connection.getConfig().nick = username;
				connection.getConfig().serverPassword = args[2];
				if(!connection.getConfig().channels.contains("#" + username)) {
					connection.getConfig().channels.add("#" + username);
				}
				ConfigurationHandler.save();
				if(connection.connect()) {
					EiraIRC.instance.addConnection(connection);
				}
			}
			return true;
		} else if(cmd.equals("nickserv")) {
			if(serverSide && !Utils.isOP(sender)) {
				sender.sendChatToPlayer(EnumChatFormatting.RED + sender.translateString(Globals.MOD_ID + ":irc.nopermission"));
				return true;
			}
			if(!serverSide) {
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.serverOnlyCommand"));
				return true;
			} else {
				if(args.length < 3) {
					throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.nickserv", "servirc");
				}
				IRCConnection connection = null;
				String username = null;
				String password = null;
				if(args.length < 4) {
					if(EiraIRC.instance.getConnectionCount() > 1) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.specifyServer"));
						throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.nickserv", "servirc");
					} else {
						connection = EiraIRC.instance.getDefaultConnection();
						if(connection == null) {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", "IRC"));
							return true;
						}
					}
					username = args[1];
					password = args[2];
				} else {
					connection = EiraIRC.instance.getConnection(args[1]);
					if(connection == null) {
						if(serverSide) {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", args[1]));
						} else {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", args[1]));
						}
						return true;
					}
					username = args[2];
					password = args[3];
				}
				connection.getConfig().nickServName = username;
				connection.getConfig().nickServPassword = password;
				connection.nickServ();
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.nickServUpdated", connection.getHost()));
			}
			return true;
		} else if(cmd.equals("disconnect")) {
			if(serverSide && !Utils.isOP(sender)) {
				sender.sendChatToPlayer(EnumChatFormatting.RED + sender.translateString(Globals.MOD_ID + ":irc.nopermission"));
				return true;
			}
			if(args.length < 2) {
				if(EiraIRC.instance.getConnectionCount() == 0) {
					if(serverSide) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", "IRC"));
					} else {					
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", "IRC"));
					}
					return true;
				}
				for(IRCConnection connection : EiraIRC.instance.getConnections()) {
					connection.disconnect();
				}
				EiraIRC.instance.clearConnections();
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.disconnecting", "IRC"));
				return true;
			} else {
				IRCConnection connection = EiraIRC.instance.getConnection(args[1]);
				if(connection == null) {
					if(serverSide) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", args[1]));
					} else {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", args[1]));
					}
					return true;
				}
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.disconnecting", args[1]));
				connection.disconnect();
				EiraIRC.instance.removeConnection(connection);
			}
			return true;
		} else if(cmd.equals("nick")) {
			if(serverSide && !Utils.isOP(sender)) {
				sender.sendChatToPlayer(EnumChatFormatting.RED + sender.translateString(Globals.MOD_ID + ":irc.nopermission"));
				return true;
			}
			if(args.length < 2) {
				throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.nick", serverSide ? "servirc" : "irc");
			}
			try {
				if(args.length < 3) {
					GlobalConfig.nick = args[1];
					for(IRCConnection connection : EiraIRC.instance.getConnections()) {
						connection.changeNick(args[1]);
					}
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.nickChange", "Global", args[1]));
				} else {
					IRCConnection connection = EiraIRC.instance.getConnection(args[1]);
					if(connection != null) {
						connection.changeNick(args[2]);
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.nickChange", args[1], args[2]));
					} else {
						if(serverSide) {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", args[1]));
						} else {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", args[1]));
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else if(cmd.equals("join")) {
			if(serverSide && !Utils.isOP(sender)) {
				sender.sendChatToPlayer(EnumChatFormatting.RED + sender.translateString(Globals.MOD_ID + ":irc.nopermission"));
				return true;
			}
			if(args.length < 2) {
				throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.join", serverSide ? "servirc" : "irc");
			}
			IRCConnection connection = null;
			if(args.length < 3) {
				if(EiraIRC.instance.getConnectionCount() > 1) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.specifyServer"));
					throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.join", serverSide ? "servirc" : "irc");
				} else {
					connection = EiraIRC.instance.getDefaultConnection();
					if(connection == null) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", "IRC"));
						return true;
					}
				}
			} else {
				connection = EiraIRC.instance.getConnection(args[2]);
				if(connection == null) {
					if(serverSide) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", args[2]));
					} else {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", args[2]));
					}
					return true;
				}
			}
			try {
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.joinChannel", args[1], connection.getHost()));
				connection.joinChannel(args[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else if(cmd.equals("leave")) {
			if(serverSide && !Utils.isOP(sender)) {
				sender.sendChatToPlayer(EnumChatFormatting.RED + sender.translateString(Globals.MOD_ID + ":irc.nopermission"));
				return true;
			}
			if(args.length < 2) {
				throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.leave", serverSide ? "servirc" : "irc");
			}
			IRCConnection connection = null;
			if(args.length < 3) {
				if(EiraIRC.instance.getConnectionCount() > 1) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.specifyServer"));
					throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.leave", serverSide ? "servirc" : "irc");
				} else {
					connection = EiraIRC.instance.getDefaultConnection();
					if(connection == null) {
						if(serverSide) {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", "IRC"));
						} else {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", "IRC"));
						}
						return true;
					}
				}
			} else {
				connection = EiraIRC.instance.getConnection(args[2]);
				if(connection == null) {
					if(serverSide) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", args[2]));
					} else {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", args[2]));
					}
					return true;
				}
			}
			try {
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.leaveChannel", args[1], connection.getHost()));
				connection.leaveChannel(args[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else if(cmd.equals("who")) {
			IRCConnection connection = null;
			int c = 1;
			if(EiraIRC.instance.getConnectionCount() > 1) {
				if(args.length < c + 1) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.specifyServer"));
					throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.who", serverSide ? "servirc" : "irc");
				} else {
					connection = EiraIRC.instance.getConnection(args[c]);
					if(connection == null) {
						if(serverSide) {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", args[c]));							
						} else {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", args[c]));
						}
						return true;
					}
					c++;
				}
			} else {
				connection = EiraIRC.instance.getDefaultConnection();
				if(connection == null) {
					if(serverSide) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", "IRC"));							
					} else {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", "IRC"));
					}
					return true;
				}
			}
			String channel = null;
			List<String> channels = connection.getConfig().channels;
			if(channels.size() > 1) {
				if(args.length < c + 1) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.specifyChannel"));
					throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.who", serverSide ? "servirc" : "irc");
				}
				channel = args[c];
				if(!channels.contains(channel)) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.channelNotFound", channel));
					return true;
				}
			} else {
				channel = channels.get(0);
			}
			List<String> userList = connection.getUserList(channel);
			sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.usersOnline", userList.size(), channel, connection.getHost()));
			String s = "* ";
			for(int i = 0; i < userList.size(); i++) {
				String user = userList.get(i);
				if(s.length() + user.length() > 100) {
					sender.sendChatToPlayer(s);
					s = "* ";
				}
				if(s.length() > 2) {
					s += ", ";
				}
				s += userList.get(i);
			}
			if(s.length() > 2) {
				sender.sendChatToPlayer(s);
			}
			return true;
		} else if(cmd.equals("mode")) {
			IRCConnection connection = null;
			int c = 1;
			if(EiraIRC.instance.getConnectionCount() > 1) {
				if(args.length < c + 1) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.specifyServer"));
					throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.mode", serverSide ? "servirc" : "irc");
				} else {
					connection = EiraIRC.instance.getConnection(args[c]);
					if(connection == null) {
						if(serverSide) {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", args[c]));							
						} else {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", args[c]));
						}
						return true;
					}
					c++;
				}
			} else {
				connection = EiraIRC.instance.getDefaultConnection();
				if(connection == null) {
					if(serverSide) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", "IRC"));							
					} else {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", "IRC"));
					}
					return true;
				}
			}
			String channel = null;
			List<String> channels = connection.getConfig().channels;
			if(channels.size() > 1) {
				if(args.length < c + 1) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.specifyChannel"));
					throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.mode", serverSide ? "servirc" : "irc");
				}
				channel = args[c];
				c++;
			} else {
				channel = channels.get(0);
			}
			if(args.length < c + 1) {
				throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.mode", serverSide ? "servirc" : "irc");
			}
			connection.getConfig().alterChannelFlags(channel, args[c]);
			sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.channelMode", args[c], channel));
			return true;
		} else if(cmd.equals("msg")) {
			if(!GlobalConfig.allowPrivateMessages) {
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.msgDisabled"));
				return true;
			}
			IRCConnection connection = null;
			int c = 1;
			if(EiraIRC.instance.getConnectionCount() > 1) {
				if(args.length < c + 1) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.specifyServer"));
					throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.msg", serverSide ? "servirc" : "irc");
				} else {
					connection = EiraIRC.instance.getConnection(args[c]);
					if(connection == null) {
						if(serverSide) {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", args[c]));							
						} else {
							sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", args[c]));
						}
						return true;
					}
					c++;
				}
			} else {
				connection = EiraIRC.instance.getDefaultConnection();
				if(connection == null) {
					if(serverSide) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", "IRC"));							
					} else {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", "IRC"));
					}
					return true;
				}
			}
			if(args.length < c + 1) {
				throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.msg", serverSide ? "servirc" : "irc");
			}
			String nick = args[c];
			c++;
			if(serverSide) {
				boolean foundNick = false;
				for(String channel : connection.getConfig().channels) {
					if(connection.getUserList(channel).contains(nick)) {
						foundNick = true;
						break;
					}
				}
				if(!foundNick) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.msgInvalidTarget", foundNick));
					return true;
				}
			}
			String message = "";
			for(int i = c; i < args.length; i++) {
				message += " " + args[i];
			}
			message = message.trim();
			if(message.isEmpty()) {
				throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.msg", serverSide ? "servirc" : "irc");
			}
			connection.sendPrivateMessage(nick, message);
			String mcMessage = "[" + nick + "] <" + Minecraft.getMinecraft().thePlayer.username + "> " + message;
			sender.sendChatToPlayer(mcMessage);
			return true;
		} else if(cmd.equals("config")) {
			if(args.length < 3) {
				throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.config", serverSide ? "servirc" : "irc");
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
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.configNoAbuse"));
					return true;
				} else if(config.equals("allowPrivateMessages")) {
					GlobalConfig.allowPrivateMessages = Boolean.parseBoolean(value);
				} else if(config.equals("showDeathMessages")) {
					GlobalConfig.showDeathMessages = Boolean.parseBoolean(value);
				} else if(config.equals("showIRCJoinLeave")) {
					GlobalConfig.showIRCJoinLeave = Boolean.parseBoolean(value);
				} else if(config.equals("showMinecraftJoinLeave")) {
					GlobalConfig.showMinecraftJoinLeave = Boolean.parseBoolean(value);
				} else {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.invalidConfigChange", "Global", config));
					return true;
				}
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.configChange", "Global", config, value));
			} else {
				IRCConnection connection = EiraIRC.instance.getConnection(host);
				if(connection == null) {
					if(serverSide) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", args[2]));
					} else {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", args[2]));
					}
					return true;
				}
				ServerConfig serverConfig = connection.getConfig();
				if(config.equals("nickServName")) {
					serverConfig.nickServName = value;
				} else if(config.equals("nickServPassword")) {
					serverConfig.nickServPassword = value;
				} else if(config.equals("allowPrivateMessages")) {
					serverConfig.allowPrivateMessages = Boolean.parseBoolean(value);
				} else if(config.equals("autoConnect")) {
					serverConfig.autoConnect = Boolean.parseBoolean(value);
				} else if(config.equals("saveCredentials")) {
					serverConfig.saveCredentials = Boolean.parseBoolean(value);
				} else {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.invalidConfigChange", host, config));
					return true;
				}
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.configChange", host, config, value));
			}
			return true;
		} else if(cmd.equals("help")) {
			if(args.length < 2) {
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.helpValidTopics"));
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.helpTopics"));
				return true;
			}
			String scmd = args[1];
			if(scmd.equals("alias")) {
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "Aliases are a way to alter the display names of certain users.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "They can be used for things like clan tags, for roleplay purposes or simply to assign another name to a player.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "Aliases only affect the chat, including emotes and IRC messages.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "To prevent abuse, they can only be set by OPs and have to be enabled in the config file.");
			} else if(scmd.equals("color")) {
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "Name colors are an easy way to distinguish between players in the chat.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "The colors are disabled to Minecraft's vanilla chat colors and are mostly similar to wool colors.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "OPs can disallow certain colors by putting them on the blacklist or disable this function altogether in the config.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "The config also has the two options opColor and ircColor, which can be set to nothing if not wanted.");
			} else if(scmd.equals("mode")) {
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "Each channel this mod is connected to gets assigned some default client mode flags.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "These are not to be confused with IRC mode flags, as they only define how a channel is handled by this mod.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "Usually, you won't have to mess with these as the global config can easily do the same, so this is more for special needs.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "Visit <...> for a more indepth explanation on each of the mode flags.");
			} else if(scmd.equals("msg")) {
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "On the serverside, private messages can only be sent to users in the same channel as the bot in order to prevent abuse.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "Private messages work both ways - IRC users can use the bot's MSG command to communicate with a specific player.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "The private chat functionality can be disabled in the config.");
			} else if(scmd.equals("config")) {
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "Whenever the config is mentioned, it usually refers to either the config file or the /irc config command.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "For most things, the command works just fine. Some config options can only be changed in the config file itself.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "This is either out of technical reasons or to prevent possible abuse.");
			} else if(scmd.equals("commands")) {
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "You can see all the possible commands by typing /irc and pressing enter or by looping through them with the TAB key.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "The most commonly used commands are: ");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "* connect, twitch, join, msg, who, nick");
			} else if(scmd.equals("twitch")) {
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "Using the /irc twitch command, you can easily connect your client or server to your twitch chat.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "For that purpose, you need to specify your twitch username and password.");
				sender.sendChatToPlayer(EnumChatFormatting.GREEN + "Keep in mind that Minecraft will show the password in readable form while typing!");
			} else {
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.helpInvalidTopic"));
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.helpTopics"));
			}
			return true;
		} else if(cmd.equals("list")) {
			if(EiraIRC.instance.getConnectionCount() == 0) {
				if(serverSide) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.notConnected", "IRC"));
				} else {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.notConnected", "IRC"));
				}
				return true;
			}
			if(serverSide) {
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.server.activeConnections"));
			} else {
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.client.activeConnections"));
			}
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				String channels = "";
				for(String channel : connection.getConfig().channels) {
					if(channels.length() > 0) {
						channels += ", ";
					}
					channels += channel;
				}
				sender.sendChatToPlayer("* " + connection.getHost() + " (" + channels + ")");
			}
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
			list.add("config");
			list.add("help");
		} else if(args.length == 2) {
			if(args[0].equals("color")) {
				Utils.addValidColorsToList(list);
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
				} else {
					list.add("allowPrivateMessages");
				}
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
