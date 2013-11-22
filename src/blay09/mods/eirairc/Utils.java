// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.StatCollector;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.ConfigHelper;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.Globals;
import blay09.mods.eirairc.config.NickServSettings;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IRCChannel;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.irc.IRCUser;

public class Utils {

	private static final char INVALID_COLOR = 'n';
	private static final int MAX_CHAT_LENGTH = 100;
	private static final Pattern pattern = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
	
	public static void sendLocalizedMessage(ICommandSender sender, String key, Object... args) {
		sender.sendChatToPlayer(getUnlocalizedChatMessage(getLocalizedMessage(key, args)));
	}
	
	public static void sendUnlocalizedMessage(ICommandSender sender, String text) {
		sender.sendChatToPlayer(getUnlocalizedChatMessage(text));
	}
	
	public static String getLocalizedMessageNoPrefix(String key, Object... args) {
		return StatCollector.translateToLocalFormatted(key, args);
	}
	
	public static String getLocalizedMessage(String key, Object... args) {
		return StatCollector.translateToLocalFormatted(Globals.MOD_ID + ":" + key, args);
	}
	
	public static ChatMessageComponent getLocalizedChatMessage(String key, Object... args) {
		return ChatMessageComponent.createFromTranslationWithSubstitutions(Globals.MOD_ID + ":" + key, args);
	}
	
	public static ChatMessageComponent getLocalizedChatMessageNoPrefix(String key, Object... args) {
		return ChatMessageComponent.createFromTranslationWithSubstitutions(key, args);
	}
	
	public static ChatMessageComponent getUnlocalizedChatMessage(String text) {
		return ChatMessageComponent.createFromText(text);
	}
	
	public static void addMessageToChat(String text) {
		for(String string : wrapString(text, MAX_CHAT_LENGTH)) {
			if(MinecraftServer.getServer() != null) {
				MinecraftServer.getServer().getConfigurationManager().sendChatMsg(Utils.getUnlocalizedChatMessage(string));
			} else {
				if(Minecraft.getMinecraft().thePlayer != null) {
					Minecraft.getMinecraft().thePlayer.addChatMessage(string);
				}
			}
		}
	}
	
	private static List<String> tmpStrings = new ArrayList<String>();
	public static List<String> wrapString(String text, int maxLength) {
		tmpStrings.clear();
		if(text.length() <= maxLength) {
			tmpStrings.add(text);
			return tmpStrings;
		}
		while(text.length() > maxLength) {
			int i = maxLength;
			while(true) {
				if(text.charAt(i) == ' ') {
					break;
				} else if(i == 0) {
					i = maxLength;
					break;
				}
				i--;
			}
			tmpStrings.add(text.substring(0, i));
			text = text.substring(i);
		}
		if(text.length() > 0) {
			tmpStrings.add(text);
		}
		return tmpStrings;
	}
	
	public static String unquote(String s) {
		return s.startsWith("\"") ? s.substring(1, s.length() - 1) : s;
	}
	
	public static String quote(String s) {
		return "\"" + s + "\"";
	}
	
	public static String getNickFromUser(String user) {
		int i = user.indexOf("!");
		if(i == -1) {
			return user;
		}
		return user.substring(0, i);
	}
	
	public static String getAliasForPlayer(EntityPlayer player) {
		if(!GlobalConfig.enableAliases) {
			return player.username;
		}
		String name = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("EiraIRC").getString("Alias");
		if(name.isEmpty()) {
			name = player.username;
		}
		return name;
	}
	
	public static boolean isOP(ICommandSender sender) {
		if(MinecraftServer.getServer().isSinglePlayer()) {
			return true;
		}
		if(sender instanceof EntityPlayer) {
			return MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).getOps().contains(((EntityPlayer)sender).username.toLowerCase().trim());
		}
		return true;
	}
	
	public static String getColoredName(String name, char colorCode) {
		if(colorCode == INVALID_COLOR) {
			return name;
		}
		return "§" + String.valueOf(colorCode) + name + "§f";
	}
	
	public static String getColorAliasForPlayer(EntityPlayer player) {
		NBTTagCompound tagCompound = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("EiraIRC");
		String alias = null;
		if(GlobalConfig.enableAliases) {
			alias = tagCompound.getString("Alias");
			if(alias.isEmpty()) {
				alias = player.username;
			}
		} else {
			alias = player.username;
		}
		boolean isOP = isOP(player);
		if(!GlobalConfig.enableNameColors && !isOP) {
			return alias;
		}
		String colorName = tagCompound.getString("NameColor");
		if(!colorName.isEmpty()) {
			char colorCode = getColorCode(colorName);
			if(colorCode == INVALID_COLOR) {
				return alias;
			}
			return getColoredName(alias, colorCode);
		} else if(isOP) {
			if(!GlobalConfig.opColor.isEmpty()) {
				char colorCode = getColorCode(colorName);
				if(colorCode == INVALID_COLOR) {
					return alias;
				}
				return getColoredName(alias, colorCode);
			}
		}
		return alias;
	}
	
	public static boolean isValidColor(String colorName) {
		return getColorCode(colorName) != INVALID_COLOR;
	}
	
	public static char getColorCode(String colorName) {
		if(colorName.isEmpty()) {
			return INVALID_COLOR;
		}
		colorName = colorName.toLowerCase();
		char colorCode = INVALID_COLOR;
		if(colorName.equals("black")) {
			colorCode = '0';
		} else if(colorName.equals("darkblue")) {
			colorCode = '1';
		} else if(colorName.equals("green")) {
			colorCode = '2';
		} else if(colorName.equals("cyan")) {
			colorCode = '3';
		} else if(colorName.equals("darkred")) {
			colorCode = '4';
		} else if(colorName.equals("purple")) {
			colorCode = '5';
		} else if(colorName.equals("gold")) {
			colorCode = '6';
		} else if(colorName.equals("gray") || colorName.equals("grey")) {
			colorCode = '7';
		} else if(colorName.equals("darkgray") || colorName.equals("darkgrey")) {
			colorCode = '8';
		} else if(colorName.equals("blue")) {
			colorCode = '9';
		} else if(colorName.equals("lime")) {
			colorCode = 'a';
		} else if(colorName.equals("lightblue")) {
			colorCode = 'b';
		} else if(colorName.equals("red")) {
			colorCode = 'c';
		} else if(colorName.equals("magenta") || colorName.equals("pink")) {
			colorCode = 'd';
		} else if(colorName.equals("yellow")) {
			colorCode = 'e';
		} else if(colorName.equals("white")) {
			colorCode = 'f';
		}
		return colorCode;
	}

	public static String getColoredName(String nick, String colorName) {
		return getColoredName(nick, getColorCode(colorName));
	}

	public static void addValidColorsToList(List<String> list) {
		list.add("black");
		list.add("darkblue");
		list.add("green");
		list.add("cyan");
		list.add("darkred");
		list.add("purple");
		list.add("gold");
		list.add("gray");
		list.add("darkgray");
		list.add("blue");
		list.add("lime");
		list.add("lightblue");
		list.add("red");
		list.add("magenta");
		list.add("yellow");
		list.add("white");		
	}

	public static void addConnectionsToList(List<String> list) {
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			list.add(connection.getHost());
		}		
	}

	public static void addBooleansToList(List<String> list) {
		list.add("true");
		list.add("false");		
	}

	public static String getCurrentServerName() {
		if(MinecraftServer.getServer() != null) {
			if(MinecraftServer.getServer().isSinglePlayer()) {
				return "Singleplayer";
			} else {
				return MinecraftServer.getServer().getServerHostname();
			}
		} else {
			return "Multiplayer";
//			A variable that could be useful? Let's make it private and remove all getters for it!
//			TODO Find out if there's another way to get this information...later
//			ServerData serverData = Minecraft.getMinecraft().getServerData();
//			if(serverData.isHidingAddress()) {
//				return serverData.serverName;
//			} else {
//				return serverData.serverIP;
//			}
		}
	}
	
	public static String formatMessage(String format, String user, String nick, String message) {
		return formatMessage(format, getCurrentServerName(), "", user, nick, message);
	}
	
	public static String formatMessage(String format, IRCConnection connection, String user, String nick, String message) {
		return formatMessage(format, connection.getHost(), "", user, nick, message);
	}

	public static String formatMessage(String format, String server, String channel, String user, String nick, String message) {
		String result = format;
		result = result.replaceAll("\\{SERVER\\}", server);
		result = result.replaceAll("\\{CHANNEL\\}", channel);
		result = result.replaceAll("\\{USER\\}", user);
		result = result.replaceAll("\\{NICK\\}", nick);
		result = result.replaceAll("\\{MESSAGE\\}", Matcher.quoteReplacement(message)).replaceAll("\\\\$", "\\$");
		return result;
	}
	
	public static String filterLinks(String message) {
		String[] s = message.split(" ");
		String result = "";
		for(int i = 0; i < s.length; i++) {
			Matcher matcher = pattern.matcher(s[i]);
			result += ((i > 0) ? " " : "") + matcher.replaceAll(Utils.getLocalizedMessage("irc.general.linkRemoved"));
		}
		return result;
	}
	
	public static String filterCodes(String message) {
		return message.replaceAll("\\§", "");
	}

	public static ServerConfig getServerConfig(IRCConnection connection) {
		return ConfigurationHandler.getServerConfig(connection.getHost());
	}

	public static IRCConnection connectTo(ServerConfig config) {
		IRCConnection connection = new IRCConnection(config.getHost(), IRCConnection.IRC_DEFAULT_PORT, config.getServerPassword(), ConfigHelper.getNick(config));
		connection.setCharset(GlobalConfig.charset);
		connection.setEventHandler(EiraIRC.instance.getEventHandler());
		if(connection.connect()) {
			EiraIRC.instance.addConnection(connection);
			return connection;
		}
		return null;
	}
	
	public static void doNickServ(IRCConnection connection, ServerConfig config) {
		NickServSettings settings = NickServSettings.settings.get(connection.getHost());
		if(settings == null) {
			return;
		}
		String username = config.getNickServName();
		String password = config.getNickServPassword();
		if(username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return;
		}
		connection.sendPrivateMessage(settings.getBotName(), settings.getCommand() + " " + username + " " + password);
	}

	public static String getQuitMessage(IRCConnection connection) {
		ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
		if(serverConfig.getQuitMessage() != null && !serverConfig.getQuitMessage().isEmpty()) {
			return serverConfig.getQuitMessage();
		}
		return GlobalConfig.quitMessage;
	}
	
	public static void sendUserList(ICommandSender sender, IRCConnection connection, IRCChannel channel) {
		Collection<IRCUser> userList = channel.getUserList();
		if(userList.size() == 0) {
			sendLocalizedMessage(sender, "irc.who.noUsersOnline", connection.getHost(), channel.getName());
			return;
		}
		sendLocalizedMessage(sender, "irc.who.usersOnline", connection.getHost(), userList.size(), channel.getName());
		String s = " * ";
		for(IRCUser user : userList) {
			if(s.length() + user.getNick().length() > Globals.CHAT_MAX_LENGTH) {
				sendUnlocalizedMessage(sender, s);
				s = " * ";
			}
			if(s.length() > 3) {
				s += ", ";
			}
			s += user.getNick();
		}
		if(s.length() > 3) {
			sendUnlocalizedMessage(sender, s);
		}
	}

	public static void sendUserList(IRCConnection connection, IRCUser user) {
		if(MinecraftServer.getServer() == null || MinecraftServer.getServer().isSinglePlayer()) {
			return;
		}
		List<EntityPlayer> userList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		if(userList.size() == 0) {
			connection.sendPrivateNotice(user, getLocalizedMessage("irc.bot.noPlayersOnline"));
			return;
		}
		connection.sendPrivateNotice(user, getLocalizedMessage("irc.bot.playersOnline", userList.size()));
		String s = " * ";
		for(int i = 0; i < userList.size(); i++) {
			EntityPlayer entityPlayer = userList.get(i);
			String alias = Utils.getAliasForPlayer(entityPlayer);
			if(s.length() + alias.length() > Globals.CHAT_MAX_LENGTH) {
				connection.sendPrivateNotice(user, s);
				s = " * ";
			}
			if(s.length() > 3) {
				s += ", ";
			}
			s += alias;
		}
		if(s.length() > 3) {
			connection.sendPrivateNotice(user, s);
		}
	}
	
	public static IRCConnection getSuggestedConnection() {
		if(EiraIRC.instance.getSuggestedChannel() != null) {
			return EiraIRC.instance.getSuggestedChannel().getConnection();
		}
		if(EiraIRC.instance.getConnectionCount() == 1) {
			return EiraIRC.instance.getDefaultConnection();
		}
		return null;
	}
	
	public static IRCChannel getSuggestedChannel() {
		if(EiraIRC.instance.getSuggestedChannel() != null) {
			return EiraIRC.instance.getSuggestedChannel();
		}
		IRCConnection connection = getSuggestedConnection();
		if(connection != null) {
			if(connection.getChannels().size() == 1) {
				return connection.getDefaultChannel();
			}
			return null;
		}
		return null;
	}
		
	public static Object resolveIRCTarget(String target, boolean allowServers, boolean requireConnected, boolean allowChannels, boolean requireOnChannel, boolean allowUsers, boolean channelUsersOnly) {
		String server = null;
		String channel = null;
		if(target.startsWith("#")) {
			if(!allowChannels) {
				return IRCTargetError.InvalidTarget;
			}
			channel = target;
			ChannelConfig foundConfig = null;
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				if(serverConfig.hasChannelConfig(channel)) {
					if(foundConfig != null) {
						return IRCTargetError.SpecifyServer;
					}
					foundConfig = serverConfig.getChannelConfig(channel);
				}
			}
			if(foundConfig == null) {
				if(EiraIRC.instance.getConnectionCount() > 1) {
					return IRCTargetError.SpecifyServer;
				} else {
					foundConfig = ConfigurationHandler.getDefaultServerConfig().getChannelConfig(channel);
				}
			}
			if(requireConnected || requireOnChannel) {
				ServerConfig serverConfig = foundConfig.getServerConfig();
				IRCConnection connection = EiraIRC.instance.getConnection(serverConfig.getHost());
				if(connection == null) {
					return IRCTargetError.NotConnected;
				}
				if(requireOnChannel) {
					IRCChannel foundChannel = connection.getChannel(foundConfig.getName());
					if(foundChannel == null) {
						return IRCTargetError.NotOnChannel;
					}
					return foundChannel;
				}
			}
			return foundConfig;
		} else {
			int channelIndex = target.indexOf('/');
			if(channelIndex != -1 && channelIndex < target.length() - 1) {
				server = target.substring(0, channelIndex);
				if(!ConfigurationHandler.hasServerConfig(server)) {
					return IRCTargetError.ServerNotFound;
				}
				ServerConfig serverConfig = ConfigurationHandler.getServerConfig(server);
				channel = target.substring(channelIndex + 1);
				if(channel.startsWith("#")) {
					if(!allowChannels) {
						return IRCTargetError.InvalidTarget;
					}
					if(requireConnected || requireOnChannel) {
						IRCConnection connection = EiraIRC.instance.getConnection(serverConfig.getHost());
						if(connection == null) {
							return IRCTargetError.NotConnected;
						}
						ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
						if(requireOnChannel) {
							IRCChannel foundChannel = connection.getChannel(channelConfig.getName());
							if(foundChannel == null) {
								return IRCTargetError.NotOnChannel;
							}
							return foundChannel;
						}
						return channelConfig;
					}
				} else {
					if(!allowUsers) {
						return IRCTargetError.InvalidTarget;
					}
					IRCConnection connection = EiraIRC.instance.getConnection(serverConfig.getHost());
					if(connection == null) {
						return IRCTargetError.NotConnected;
					}
					IRCUser user = connection.getUser(channel);
					if(user == null) {
						if(channelUsersOnly) {
							return IRCTargetError.UserNotFound;
						}
						return new IRCUser(connection, channel);
					}
					return user;
				}
			} else {
				if(target.endsWith("/")) {
					target = target.substring(0, target.length() - 1);
				}
				if(ConfigurationHandler.hasServerConfig(target)) {
					if(!allowServers) {
						return IRCTargetError.InvalidTarget;
					}
					if(requireConnected) {
						IRCConnection connection = EiraIRC.instance.getConnection(target);
						if(connection == null) {
							return IRCTargetError.NotConnected;
						}
						return connection;
					}
					return ConfigurationHandler.getServerConfig(target);
				} else {
					if(allowUsers) {
						IRCUser foundUser = null;
						for(IRCConnection connection : EiraIRC.instance.getConnections()) {
							IRCUser user = connection.getUser(target);
							if(user != null) {
								if(foundUser != null) {
									return IRCTargetError.SpecifyServer;
								}
								foundUser = user;
							}
						}
						if(foundUser == null) {
							if(channelUsersOnly) {
								return IRCTargetError.UserNotFound;
							} else {
								if(EiraIRC.instance.getConnectionCount() > 1) {
									return IRCTargetError.SpecifyServer;
								} else {
									return new IRCUser(EiraIRC.instance.getDefaultConnection(), target);
								}
							}
						}
						return foundUser;
					}
					if(allowServers && !allowUsers) {
						return IRCTargetError.ServerNotFound;
					} else if(allowUsers && !allowServers) {
						return IRCTargetError.UserNotFound;
					}
				}
			}
		}
		return IRCTargetError.TargetNotFound;
	}
}
