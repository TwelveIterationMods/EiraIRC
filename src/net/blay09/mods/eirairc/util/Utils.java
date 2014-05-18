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
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCContext;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.bot.EiraIRCBot;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.ServiceConfig;
import net.blay09.mods.eirairc.config.ServiceSettings;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.StatCollector;

public class Utils {

	private static final char INVALID_COLOR = 'n';
	private static final int MAX_CHAT_LENGTH = 100;
	private static final Pattern pattern = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
	private static final String DEFAULT_USERNAME = "EiraBot";
	
	public static void sendLocalizedMessage(ICommandSender sender, String key, Object... args) {
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(sender.getCommandSenderName());
		if(playerInfo.modInstalled) {
			sender.sendChatToPlayer(getLocalizedChatMessage(key, args));
		} else {
			sendUnlocalizedMessage(sender, getLocalizedChatMessage(key, args).toString());
		}
	}
	
	public static void sendUnlocalizedMessage(ICommandSender sender, String text) {
		sender.sendChatToPlayer(getUnlocalizedChatMessage(text));
	}
	
	public static String getLocalizedMessageNoPrefix(String key, Object... args) {
		return StatCollector.translateToLocalFormatted(key, args);
	}
	
	public static String getLocalizedMessage(String key, Object... args) {
		return StatCollector.translateToLocalFormatted(EiraIRC.MOD_ID + ":" + key, args);
	}
	
	public static ChatMessageComponent getLocalizedChatMessage(String key, Object... args) {
		return ChatMessageComponent.createFromTranslationWithSubstitutions(EiraIRC.MOD_ID + ":" + key, args);
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
					Minecraft.getMinecraft().thePlayer.sendChatToPlayer(Utils.getUnlocalizedChatMessage(string));
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
	
	private static String addPreSuffix(String name) {
		return GlobalConfig.nickPrefix + name + GlobalConfig.nickSuffix;
	}
	
	public static String addColorCodes(String name, char colorCode) {
		if(colorCode == INVALID_COLOR) {
			return name;
		}
		return Globals.COLOR_CODE_PREFIX + String.valueOf(colorCode) + name + Globals.COLOR_CODE_PREFIX + "f";
	}

	public static String getNickIRC(EntityPlayer player) {
		return addPreSuffix(getAliasForPlayer(player));
	}
	
	public static String getNickGame(EntityPlayer player, boolean color) {
		if(color) {
			return addColorCodes(getAliasForPlayer(player), getColorCodeForPlayer(player));
		} else {
			return getAliasForPlayer(player);
		}
	}
	
	public static String getNickGame(IIRCChannel channel, IIRCUser user, boolean color) {
		if(color) {
			return addColorCodes(user.getName(), getColorCodeForUser(channel, user));
		} else {
			return user.getName();
		}
	}
	
	public static char getColorCodeForUser(IIRCChannel channel, IIRCUser user) {
		if(channel == null) {
			return getColorCode(DisplayConfig.ircPrivateColor);
		}
		if(user.isOperator(channel)) {
			if(!DisplayConfig.ircOpColor.isEmpty()) {
				return getColorCode(DisplayConfig.ircOpColor);
			}
		} else if(user.hasVoice(channel)) {
			if(!DisplayConfig.ircVoiceColor.isEmpty()) {
				return getColorCode(DisplayConfig.ircVoiceColor);
			}
		}
		return getColorCode(DisplayConfig.ircColor);
	}
	
	public static char getColorCodeForPlayer(EntityPlayer player) {
		NBTTagCompound tagCompound = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("EiraIRC");
		boolean isOP = Utils.isServerSide() && isOP(player);
		if(!DisplayConfig.enableNameColors && !isOP) {
			return INVALID_COLOR;
		}
		String colorName = tagCompound.getString("NameColor");
		if(!colorName.isEmpty()) {
			return getColorCode(colorName);
		} else if(isOP) {
			if(!DisplayConfig.mcOpColor.isEmpty()) {
				return getColorCode(DisplayConfig.mcOpColor);
			}
		}
		return getColorCode(DisplayConfig.mcColor);
	}
	
	public static String getAliasForPlayer(EntityPlayer player) {
		if(!GlobalConfig.enableAliases) {
			return player.getCommandSenderName();
		}
		String name = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("EiraIRC").getString("Alias");
		if(name.isEmpty()) {
			name = player.getCommandSenderName();
		}
		return name;
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
		for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
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

	public static IRCConnection connectTo(ServerConfig config) {
		IRCConnection connection = new IRCConnection(config.getHost(), config.getServerPassword(), ConfigHelper.getFormattedNick(config), config.getIdent(), config.getDescription());
		connection.setCharset(GlobalConfig.charset);
		connection.setBot(new EiraIRCBot(connection));
		if(connection.connect()) {
			return connection;
		}
		return null;
	}
	
	public static void doNickServ(IIRCConnection connection, ServerConfig config) {
		ServiceSettings settings = ServiceConfig.getSettings(connection.getHost(), connection.getServerType());
		String username = config.getNickServName();
		String password = config.getNickServPassword();
		if(username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return;
		}
		connection.irc(settings.getIdentifyCommand(username, password));
	}
	
	public static void sendUserList(ICommandSender player, IIRCConnection connection, IIRCChannel channel) {
		Collection<IIRCUser> userList = channel.getUserList();
		if(userList.size() == 0) {
			if(player == null) {
				addMessageToChat(Utils.getLocalizedMessage("irc.who.noUsersOnline", connection.getHost(), channel.getName()));
			} else {
				sendLocalizedMessage(player, "irc.who.noUsersOnline", connection.getHost(), channel.getName());
			}
			return;
		}
		if(player == null) {
			addMessageToChat(Utils.getLocalizedMessage("irc.who.usersOnline", connection.getHost(), userList.size(), channel.getName()));
		} else {
			sendLocalizedMessage(player, "irc.who.usersOnline", connection.getHost(), userList.size(), channel.getName());
		}
		String s = " * ";
		for(IIRCUser user : userList) {
			if(s.length() + user.getName().length() > Globals.CHAT_MAX_LENGTH) {
				if(player == null) {
					addMessageToChat(s);
				} else {
					sendUnlocalizedMessage(player, s);
				}
				s = " * ";
			}
			if(s.length() > 3) {
				s += ", ";
			}
			s += user.getName();
		}
		if(s.length() > 3) {
			if(player == null) {
				addMessageToChat(s);
			} else {
				sendUnlocalizedMessage(player, s);
			}
		}
	}
	
	public static void sendPlayerList(IIRCUser user) {
		if(MinecraftServer.getServer() == null || MinecraftServer.getServer().isSinglePlayer()) {
			return;
		}
		List<EntityPlayer> playerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		if(playerList.size() == 0) {
			user.notice(getLocalizedMessage("irc.bot.noPlayersOnline"));
			return;
		}
		user.notice(getLocalizedMessage("irc.bot.playersOnline", playerList.size()));
		String s = " * ";
		for(int i = 0; i < playerList.size(); i++) {
			EntityPlayer entityPlayer = playerList.get(i);
			String alias = getNickIRC(entityPlayer);
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
	
	public static IIRCContext getSuggestedTarget() {
		IIRCContext result = EiraIRC.instance.getChatSessionHandler().getIRCTarget();
		if(result == null) {
			IIRCConnection connection = EiraIRC.instance.getDefaultConnection();
			if(connection != null) {
				if(connection.getChannels().size() == 1) {
					return connection.getChannels().iterator().next();
				}
				return null;
			}
			return null;
		}
		return result;
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
	
	public static String joinStrings(String[] arr, String delimiter) {
		return joinStrings(arr, delimiter, 0);
	}
	
	public static String joinStrings(String[] args, String delimiter, int startIdx) {
		StringBuilder sb = new StringBuilder();
		for(int i = startIdx; i < args.length; i++) {
			if(i > startIdx) {
				sb.append(delimiter);
			}
			sb.append(args[i]);
		}
		return sb.toString();
	}

	public static String formatMessageNew(String format, IIRCConnection connection, IIRCChannel channel, IIRCUser user, String message, boolean colorName) {
		String result = format;
		result = result.replaceAll("\\{SERVER\\}", connection.getIdentifier());
		if(channel != null) {
			result = result.replaceAll("\\{CHANNEL\\}", channel.getName());
		}
		if(user != null) {
			result = result.replaceAll("\\{USER\\}", user.getIdentifier());
			result = result.replaceAll("\\{NICK\\}", getNickGame(channel, user, colorName));
		}
		result = result.replaceAll("\\{MESSAGE\\}", Matcher.quoteReplacement(message)).replaceAll("\\\\$", "\\$");
		return result;
	}

}
