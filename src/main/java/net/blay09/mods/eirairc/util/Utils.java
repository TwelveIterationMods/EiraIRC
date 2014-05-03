// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.base.IIRCChannel;
import net.blay09.mods.eirairc.api.base.IIRCConnection;
import net.blay09.mods.eirairc.api.base.IIRCUser;
import net.blay09.mods.eirairc.bot.EiraIRCBot;
import net.blay09.mods.eirairc.config.BotProfile;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.ServiceConfig;
import net.blay09.mods.eirairc.config.ServiceSettings;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.irc.IRCTarget;
import net.blay09.mods.eirairc.irc.IRCUser;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;

public class Utils {

	private static final char INVALID_COLOR = 'n';
	private static final int MAX_CHAT_LENGTH = 100;
	private static final Pattern pattern = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
	private static final String DEFAULT_USERNAME = "EiraBot";
	
	public static void sendLocalizedMessage(ICommandSender sender, String key, Object... args) {
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(sender.getCommandSenderName());
		if(playerInfo.modInstalled) {
			sender.addChatMessage(getLocalizedChatMessage(key, args));
		} else {
			sendUnlocalizedMessage(sender, getLocalizedChatMessage(key, args).getUnformattedText());
		}
	}
	
	public static void sendUnlocalizedMessage(ICommandSender sender, String text) {
		sender.addChatMessage(getUnlocalizedChatMessage(text));
	}
	
	public static String getLocalizedMessageNoPrefix(String key, Object... args) {
		return StatCollector.translateToLocalFormatted(key, args);
	}
	
	public static String getLocalizedMessage(String key, Object... args) {
		return StatCollector.translateToLocalFormatted(EiraIRC.MOD_ID + ":" + key, args);
	}
	
	public static ChatComponentTranslation getLocalizedChatMessage(String key, Object... args) {
		return new ChatComponentTranslation(EiraIRC.MOD_ID + ":" + key, args);
	}
	
	public static ChatComponentTranslation getLocalizedChatMessageNoPrefix(String key, Object... args) {
		return new ChatComponentTranslation(key, args);
	}
	
	public static ChatComponentText getUnlocalizedChatMessage(String text) {
		return new ChatComponentText(text);
	}
	
	public static void addMessageToChat(String text) {
		for(String string : wrapString(text, MAX_CHAT_LENGTH)) {
			if(MinecraftServer.getServer() != null) {
				MinecraftServer.getServer().getConfigurationManager().sendChatMsg(Utils.getUnlocalizedChatMessage(string));
			} else {
				if(Minecraft.getMinecraft().thePlayer != null) {
					Minecraft.getMinecraft().thePlayer.addChatMessage(Utils.getUnlocalizedChatMessage(string));
				}
			}
		}
	}
	
	private static List<String> tmpStrings = new ArrayList<String>();
	public static List<String> wrapString(String text, int maxLength) {
		tmpStrings.clear();
		if(text == null) {
			return tmpStrings;
		}
		text = text.trim();
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
			text = text.substring(i + 1).trim();
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
	
	public static String addPreSuffix(String name) {
		return GlobalConfig.nickPrefix + name + GlobalConfig.nickSuffix;
	}
	
	public static String getAliasForPlayer(EntityPlayer player, boolean irc) {
		if(!GlobalConfig.enableAliases) {
			return addPreSuffix(player.getCommandSenderName());
		}
		String name = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("EiraIRC").getString("Alias");
		if(name.isEmpty()) {
			name = player.getCommandSenderName();
		}
		return irc ? addPreSuffix(name) : name;
	}
	
	public static boolean isOP(ICommandSender sender) {
		if(MinecraftServer.getServer() == null) {
			return false;
		}
		if(MinecraftServer.getServer().isSinglePlayer()) {
			return true;
		}
		if(sender instanceof EntityPlayer) {
			return MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(sender.getCommandSenderName().toLowerCase());
		}
		return true;
	}
	
	public static String getColoredName(String name, char colorCode) {
		if(colorCode == INVALID_COLOR) {
			return name;
		}
		return Globals.COLOR_CODE_PREFIX + String.valueOf(colorCode) + name + Globals.COLOR_CODE_PREFIX + "f";
	}
	
	public static String getColorAliasForPlayer(EntityPlayer player) {
		NBTTagCompound tagCompound = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("EiraIRC");
		String alias = getAliasForPlayer(player, false);
		boolean isOP = isOP(player);
		if(!DisplayConfig.enableNameColors && !isOP) {
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
			if(!DisplayConfig.opColor.isEmpty()) {
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
		message = message.replaceAll(Globals.COLOR_CODE_PREFIX, "");
		message = message.replaceAll("\u0003[0-9][0-9]?[,]?[0-9]?[0-9]?", "");
		return message;
	}

	public static ServerConfig getServerConfig(IRCConnection connection) {
		return ConfigurationHandler.getServerConfig(connection.getHost());
	}

	public static IRCConnection connectTo(ServerConfig config) {
		IRCConnection connection = new IRCConnection(config.getHost(), IRCConnection.IRC_DEFAULT_PORT, config.getServerPassword(), ConfigHelper.getFormattedNick(config), config.getIdent(), config.getDescription());
		connection.setCharset(GlobalConfig.charset);
		connection.setConnectionHandler(EiraIRC.instance.getIRCConnectionHandler());
		connection.setBot(new EiraIRCBot(connection, new BotProfile("default")));
		if(connection.connect()) {
			return connection;
		}
		return null;
	}
	
	public static void doNickServ(IRCConnection connection, ServerConfig config) {
		ServiceSettings settings = ServiceConfig.getSettings(connection.getHost(), connection.getServerType());
		String username = config.getNickServName();
		String password = config.getNickServPassword();
		if(username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return;
		}
		connection.sendIRC(settings.getIdentifyCommand(username, password));
	}

	public static String getQuitMessage(IRCConnection connection) {
		ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
		if(serverConfig.getQuitMessage() != null && !serverConfig.getQuitMessage().isEmpty()) {
			return serverConfig.getQuitMessage();
		}
		return DisplayConfig.quitMessage;
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
			if(s.length() + user.getName().length() > Globals.CHAT_MAX_LENGTH) {
				sendUnlocalizedMessage(sender, s);
				s = " * ";
			}
			if(s.length() > 3) {
				s += ", ";
			}
			s += user.getName();
		}
		if(s.length() > 3) {
			sendUnlocalizedMessage(sender, s);
		}
	}

	public static void sendUserList(IIRCUser user) {
		if(MinecraftServer.getServer() == null || MinecraftServer.getServer().isSinglePlayer()) {
			return;
		}
		List<EntityPlayer> userList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		if(userList.size() == 0) {
			user.notice(getLocalizedMessage("irc.bot.noPlayersOnline"));
			return;
		}
		user.notice(getLocalizedMessage("irc.bot.playersOnline", userList.size()));
		String s = " * ";
		for(int i = 0; i < userList.size(); i++) {
			EntityPlayer entityPlayer = userList.get(i);
			String alias = Utils.getAliasForPlayer(entityPlayer, true);
			if(s.length() + alias.length() > Globals.CHAT_MAX_LENGTH) {
				user.notice(s);
				s = " * ";
			}
			if(s.length() > 3) {
				s += ", ";
			}
			s += alias;
		}
		if(s.length() > 3) {
			user.notice(s);
		}
	}
	
	public static IRCConnection getSuggestedConnection() {
		if(EiraIRC.instance.getChatSessionHandler().getIRCTarget() != null) {
			IRCTarget activeTarget = EiraIRC.instance.getChatSessionHandler().getIRCTarget();
			if(activeTarget != null) {
				return activeTarget.getConnection();
			}
		}
		if(EiraIRC.instance.getConnectionCount() == 1) {
			return EiraIRC.instance.getDefaultConnection();
		}
		return null;
	}
	
	public static IRCTarget getSuggestedTarget() {
		IRCTarget result = getSuggestedUser();
		if(result == null) {
			return getSuggestedChannel();
		}
		return result;
	}
	
	public static IRCChannel getSuggestedChannel() {
		if(EiraIRC.instance.getChatSessionHandler().getIRCTarget() != null) {
			IRCTarget activeTarget = EiraIRC.instance.getChatSessionHandler().getIRCTarget();
			if(activeTarget instanceof IRCChannel) {
				return (IRCChannel) activeTarget;
			}
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
	
	public static IRCUser getSuggestedUser() {
		if(EiraIRC.instance.getChatSessionHandler().getIRCTarget() != null) {
			IRCTarget activeTarget = EiraIRC.instance.getChatSessionHandler().getIRCTarget();
			if(activeTarget instanceof IRCUser) {
				return (IRCUser) activeTarget;
			}
		}
		return null;
	}
	
	public static String getUsername() {
		String username = EiraIRC.proxy.getUsername();
		if(username == null) {
			return DEFAULT_USERNAME + Math.round(Math.random() * 10000);
		}
		return username;
	}
	
	public static void openWebpage(String url) {
		try {
			openWebpage(new URL(url).toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setClipboardString(String s) {
		StringSelection selection = new StringSelection(s);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	public static boolean isServerSide() {
		return MinecraftServer.getServer() != null && !MinecraftServer.getServer().isSinglePlayer();
	}
	
	public static String[] shiftArgs(String[] args, int offset) {
		String[] shiftedArgs = new String[args.length - offset];
		for(int i = offset; i < args.length; i++) {
			shiftedArgs[i - offset] = args[i];
		}
		return shiftedArgs;
	}
	
	public static String joinArgs(String[] args, int startIdx) {
		StringBuilder sb = new StringBuilder();
		for(int i = startIdx; i < args.length; i++) {
			sb.append(args[i]);
		}
		return sb.toString();
	}

	public static String formatMessageNew(String format, IIRCConnection connection, IIRCChannel channel, IIRCUser user, String message) {
		String result = format;
		result = result.replaceAll("\\{SERVER\\}", connection.getIdentifier());
		if(channel != null) {
			result = result.replaceAll("\\{CHANNEL\\}", channel.getName());
		}
		if(user != null) {
			result = result.replaceAll("\\{USER\\}", user.getIdentifier());
			result = result.replaceAll("\\{NICK\\}", user.getName());
		}
		result = result.replaceAll("\\{MESSAGE\\}", Matcher.quoteReplacement(message)).replaceAll("\\\\$", "\\$");
		return result;
	}
	
}
