// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import io.netty.buffer.ByteBuf;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.base.ServiceConfig;
import net.blay09.mods.eirairc.config.base.ServiceSettings;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.BotStringComponent;
import net.blay09.mods.eirairc.config.settings.ThemeColorComponent;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.irc.ssl.IRCConnectionSSLImpl;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.Sys;

public class Utils {

	private static final int MAX_CHAT_LENGTH = 100;
	private static final String DEFAULT_USERNAME = "EiraBot";
	private static final String ENCODING = "UTF-8";
	
	public static void sendLocalizedMessage(ICommandSender sender, String key, Object... args) {
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(sender.getCommandSenderName());
		if(playerInfo.modInstalled) {
			sender.addChatMessage(getLocalizedChatMessage(key, args));
		} else {
			sender.addChatMessage(new ChatComponentText(getLocalizedChatMessage(key, args).getUnformattedText()));
		}
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
	
	public static void addMessageToChat(@NotNull IChatComponent chatComponent) {
		if(MinecraftServer.getServer() != null) {
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(chatComponent);
		} else {
			if(Minecraft.getMinecraft().thePlayer != null) {
				Minecraft.getMinecraft().thePlayer.addChatMessage(chatComponent);
			}
		}
	}
	
	public static void addMessageToChat(String text) {
		if(text == null) {
			return;
		}
		if(text.length() < MAX_CHAT_LENGTH) {
			addMessageToChat(new ChatComponentText(text));
		} else {
			for(String string : wrapString(text, MAX_CHAT_LENGTH)) {
				addMessageToChat(new ChatComponentText(string));
			}
		}
	}
	
	public static List<String> wrapString(String text, int maxLength) {
		List<String> tmpStrings = new ArrayList<String>();
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
	
	public static String getNickIRC(EntityPlayer player, IRCContext context) {
		return MessageFormat.formatNick(getAliasForPlayer(player), context, MessageFormat.Target.IRC, MessageFormat.Mode.Message);
	}

	public static String getNickGame(EntityPlayer player) {
		return getAliasForPlayer(player);
	}

	public static String getAliasForPlayer(EntityPlayer player) {
		if(!SharedGlobalConfig.enablePlayerAliases) {
			return player.getCommandSenderName();
		}
		String name = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag(Globals.NBT_EIRAIRC).getString(Globals.NBT_ALIAS);
		if(name.isEmpty()) {
			name = player.getCommandSenderName();
		}
		return name;
	}
	
	public static boolean isOP(ICommandSender sender) {
		if(MinecraftServer.getServer() == null || MinecraftServer.getServer().isSinglePlayer()) {
			return true;
		}
		if(sender instanceof EntityPlayer) {
			return MinecraftServer.getServer().getConfigurationManager().func_152603_m().func_152700_a(sender.getCommandSenderName().toLowerCase()) != null; // isPlayerOpped
		}
		return true;
	}
	
	public static boolean isValidColor(String colorName) {
		EnumChatFormatting colorFormatting = getColorFormatting(colorName);
		return colorFormatting != null && colorFormatting.isColor();
	}

	@Nullable
	public static EnumChatFormatting getColorFormatting(String colorName) {
		if(colorName == null || colorName.isEmpty()) {
			return null;
		}
		colorName = colorName.toLowerCase();
		EnumChatFormatting colorFormatting = null;
		if(colorName.equals("black")) {
			colorFormatting = EnumChatFormatting.BLACK;
		} else if(colorName.equals("darkblue") || colorName.equals("dark blue")) {
			colorFormatting = EnumChatFormatting.DARK_BLUE;
		} else if(colorName.equals("green")) {
			colorFormatting = EnumChatFormatting.DARK_GREEN;
		} else if(colorName.equals("cyan")) {
			colorFormatting = EnumChatFormatting.DARK_AQUA;
		} else if(colorName.equals("darkred") || colorName.equals("dark red")) {
			colorFormatting = EnumChatFormatting.DARK_RED;
		} else if(colorName.equals("purple")) {
			colorFormatting = EnumChatFormatting.DARK_PURPLE;
		} else if(colorName.equals("gold") || colorName.equals("orange")) {
			colorFormatting = EnumChatFormatting.GOLD;
		} else if(colorName.equals("gray") || colorName.equals("grey")) {
			colorFormatting = EnumChatFormatting.GRAY;
		} else if(colorName.equals("darkgray") || colorName.equals("darkgrey") || colorName.equals("dark gray") || colorName.equals("dark grey")) {
			colorFormatting = EnumChatFormatting.DARK_GRAY;
		} else if(colorName.equals("blue")) {
			colorFormatting = EnumChatFormatting.BLUE;
		} else if(colorName.equals("lime")) {
			colorFormatting = EnumChatFormatting.GREEN;
		} else if(colorName.equals("lightblue") || colorName.equals("light blue")) {
			colorFormatting = EnumChatFormatting.AQUA;
		} else if(colorName.equals("red")) {
			colorFormatting = EnumChatFormatting.RED;
		} else if(colorName.equals("magenta") || colorName.equals("pink")) {
			colorFormatting = EnumChatFormatting.LIGHT_PURPLE;
		} else if(colorName.equals("yellow")) {
			colorFormatting = EnumChatFormatting.YELLOW;
		} else if(colorName.equals("white")) {
			colorFormatting = EnumChatFormatting.WHITE;
		}
		return colorFormatting;
	}

	@Nullable
	public static EnumChatFormatting getColorFormattingForPlayer(EntityPlayer player) {
		NBTTagCompound tagCompound = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag(Globals.NBT_EIRAIRC);
		boolean isOP = Utils.isServerSide() && Utils.isOP(player);
		if(!SharedGlobalConfig.enablePlayerColors && !isOP) {
			return null;
		}
		ThemeSettings theme = SharedGlobalConfig.theme;
		String colorName = tagCompound.getString(Globals.NBT_NAMECOLOR);
		if(!colorName.isEmpty()) {
			return Utils.getColorFormatting(colorName);
		} else if(isOP) {
			if(theme.hasColor(ThemeColorComponent.mcOpNameColor)) {
				return theme.getColor(ThemeColorComponent.mcOpNameColor);
			}
		}
		return theme.getColor(ThemeColorComponent.mcNameColor);
	}

	@Nullable
	public static EnumChatFormatting getColorFormattingForUser(IRCChannel channel, IRCUser user) {
		ThemeSettings theme = ConfigHelper.getTheme(channel);
		if(channel == null) {
			return theme.getColor(ThemeColorComponent.ircPrivateNameColor);
		}
		if(user.isOperator(channel)) {
			if(theme.hasColor(ThemeColorComponent.ircOpNameColor)) {
				return theme.getColor(ThemeColorComponent.ircOpNameColor);
			}
		} else if(user.hasVoice(channel)) {
			if(theme.hasColor(ThemeColorComponent.ircVoiceNameColor)) {
				return theme.getColor(ThemeColorComponent.ircVoiceNameColor);
			}
		}
		return theme.getColor(ThemeColorComponent.ircNameColor);
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
		for(IRCConnection connection : EiraIRC.instance.getConnectionManager().getConnections()) {
			list.add(connection.getHost());
		}		
	}

	public static void addBooleansToList(List<String> list) {
		list.add("true");
		list.add("false");		
	}

	public static IRCConnectionImpl connectTo(ServerConfig config) {
		IRCConnectionImpl connection;
		BotSettings botSettings = config.getBotSettings();
		if(config.isSSL()) {
			connection = new IRCConnectionSSLImpl(config.getAddress(), config.getServerPassword(), ConfigHelper.getFormattedNick(config), botSettings.getString(BotStringComponent.Ident), botSettings.getString(BotStringComponent.Description));
		} else {
			connection = new IRCConnectionImpl(config.getAddress(), config.getServerPassword(), ConfigHelper.getFormattedNick(config), botSettings.getString(BotStringComponent.Ident), botSettings.getString(BotStringComponent.Description));
		}
		connection.setCharset(config.getCharset());
		connection.setBot(new IRCBotImpl(connection));
		if(connection.start()) {
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
		connection.irc(settings.getIdentifyCommand(username, password));
	}
	
	public static void sendUserList(ICommandSender player, IRCConnection connection, IRCChannel channel) {
		Collection<IRCUser> userList = channel.getUserList();
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
		for(IRCUser user : userList) {
			if(s.length() + user.getName().length() > Globals.CHAT_MAX_LENGTH) {
				if(player == null) {
					addMessageToChat(s);
				} else {
					player.addChatMessage(new ChatComponentText(s));
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
				player.addChatMessage(new ChatComponentText(s));
			}
		}
	}
	
	public static void sendPlayerList(IRCUser user) {
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
			String alias = getNickIRC(entityPlayer, null);
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
	
	public static IRCContext getSuggestedTarget() {
		IRCContext result = EiraIRC.instance.getChatSessionHandler().getIRCTarget();
		if(result == null) {
			IRCConnection connection = EiraIRC.instance.getConnectionManager().getDefaultConnection();
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

	public static void openDirectory(File dir) {
		if (Util.getOSType() == Util.EnumOS.OSX) {
			try {
				Runtime.getRuntime().exec(new String[] {"/usr/bin/open", dir.getAbsolutePath()});
				return;
			} catch (IOException ignored) {}
		} else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
			try {
				Runtime.getRuntime().exec(String.format("cmd.exe /C start \"Open file\" \"%s\"", dir.getAbsolutePath()));
				return;
			} catch (IOException ignored) {}
		}
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(dir.toURI());
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Sys.openURL("file://" + dir.getAbsolutePath());
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

	public static String readString(ByteBuf buf) {
		short len = buf.readShort();
		byte[] b = new byte[len];
		buf.readBytes(b);
		try {
			return new String(b, ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeString(ByteBuf buffer, String s) {
		try {
			byte[] b = s.getBytes(ENCODING);
			buffer.writeShort(b.length);
			buffer.writeBytes(b);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static String extractHost(String url) {
		int portIdx = url.indexOf(':');
		if(portIdx != -1) {
			return url.substring(0, portIdx);
		} else {
			return url;
		}
	}

	public static int extractPort(String url, int defaultPort) {
		int portIdx = url.indexOf(':');
		if(portIdx != -1) {
			try {
				return Integer.parseInt(url.substring(portIdx + 1));
			} catch (NumberFormatException e) {
				return defaultPort;
			}
		}
		return defaultPort;
	}
}
