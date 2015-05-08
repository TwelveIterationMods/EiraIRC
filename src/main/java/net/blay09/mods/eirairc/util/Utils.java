// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import io.netty.buffer.ByteBuf;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.base.ServiceConfig;
import net.blay09.mods.eirairc.config.base.ServiceSettings;
import net.blay09.mods.eirairc.config.settings.ThemeColorComponent;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.irc.IRCUserImpl;
import net.blay09.mods.eirairc.irc.ssl.IRCConnectionSSLImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.Sys;

import java.awt.*;
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

public class Utils {

	private static final int MAX_CHAT_LENGTH = 100;
	private static final String DEFAULT_USERNAME = "EiraBot";
	private static final String ENCODING = "UTF-8";
	
	public static void sendLocalizedMessage(ICommandSender sender, String key, Object... args) {
		if(EiraIRCAPI.hasClientSideInstalled(sender)) {
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
		if(MinecraftServer.getServer() != null && !MinecraftServer.getServer().isSinglePlayer()) {
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
		return MessageFormat.formatNick(getAliasForPlayer(player), context, MessageFormat.Target.IRC, MessageFormat.Mode.Message, null);
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

	public static String getServerName() {
		ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
		if(serverData != null) {
			return serverData.serverName;
		}
		return null;
	}

	public static String getServerAddress() {
		ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
		if(serverData != null) {
			return serverData.serverIP;
		}
		return null;
	}

	public static boolean isOP(ICommandSender sender) {
<<<<<<< HEAD
		return MinecraftServer.getServer() == null || (MinecraftServer.getServer().isSinglePlayer() && !MinecraftServer.getServer().isDedicatedServer()) || sender.canCommandSenderUseCommand(3, "");
=======
		if(MinecraftServer.getServer() == null || (MinecraftServer.getServer().isSinglePlayer() && !MinecraftServer.getServer().isDedicatedServer())) {
			return true;
		}
		return sender.canCommandSenderUseCommand(3, "");
>>>>>>> d248e1685dde1dafba3323d197ad61200374c3a9
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

	public static boolean redirectTo(ServerConfig serverConfig, boolean solo) {
		if(serverConfig == null) {
			EiraIRC.instance.getConnectionManager().stopIRC();
			return true;
		}
		IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getIdentifier());
		if(connection != null && solo) {
			connection.disconnect("Redirected by " + Utils.getCurrentServerName());
			connection = null;
		}
		if(connection == null) {
			connection = connectTo(serverConfig);
			if(connection == null) {
				return false;
			}
		} else {
			for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
				connection.join(channelConfig.getName(), channelConfig.getPassword());
			}
		}
		return true;
	}

	public static IRCConnectionImpl connectTo(ServerConfig config) {
		IRCConnection oldConnection = EiraIRC.instance.getConnectionManager().getConnection(config.getIdentifier());
		if(oldConnection != null) {
			oldConnection.disconnect("Reconnecting...");
		}
		IRCConnectionImpl connection;
		if(config.isSSL()) {
			connection = new IRCConnectionSSLImpl(config, ConfigHelper.getFormattedNick(config));
		} else {
			connection = new IRCConnectionImpl(config, ConfigHelper.getFormattedNick(config));
		}
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

	public static void sendPlayerList(IRCContext context) {
		if(MinecraftServer.getServer() == null || MinecraftServer.getServer().isSinglePlayer()) {
			return;
		}
		List<EntityPlayer> playerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		if(playerList.size() == 0) {
			if(context instanceof IRCUser) {
				context.notice(getLocalizedMessage("irc.bot.noPlayersOnline"));
			} else if(context instanceof IRCChannel) {
				context.message(getLocalizedMessage("irc.bot.noPlayersOnline"));
			}
			return;
		}
		if(context instanceof IRCUser) {
			context.notice(getLocalizedMessage("irc.bot.playersOnline", playerList.size()));
		} else if(context instanceof IRCChannel) {
			context.message(getLocalizedMessage("irc.bot.playersOnline", playerList.size()));
		}
		String s = " * ";
		for (EntityPlayer entityPlayer : playerList) {
			String alias = getNickIRC(entityPlayer, null);
			if (s.length() + alias.length() > Globals.CHAT_MAX_LENGTH) {
				if (context instanceof IRCUser) {
					context.notice(s);
				} else if (context instanceof IRCChannel) {
					context.message(s);
				}
				s = " * ";
			}
			if (s.length() > 3) {
				s += ", ";
			}
			s += alias;
		}
		if(s.length() > 3) {
			if(context instanceof IRCUser) {
				context.notice(s);
			} else if(context instanceof IRCChannel) {
				context.message(s);
			}
		}
	}
	
	public static IRCContext getSuggestedTarget() {
		IRCContext result = EiraIRC.instance.getChatSessionHandler().getChatTarget();
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
			} catch (Exception ignored) {}
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

	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
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
		try {
			clipboard.setContents(selection, selection);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
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
		System.arraycopy(args, offset, shiftedArgs, 0, args.length - offset);
		return shiftedArgs;
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

	public static int[] extractPorts(String url, int defaultPort) {
		int portIdx = url.indexOf(':');
		if(portIdx != -1) {
			try {
				String[] portRanges = url.substring(portIdx + 1).split("\\+");
				List<Integer> portList = new ArrayList<Integer>();
				for (String portRange : portRanges) {
					int sepIdx = portRange.indexOf('-');
					if (sepIdx != -1) {
						int min = Integer.parseInt(portRange.substring(0, sepIdx));
						int max = Integer.parseInt(portRange.substring(sepIdx + 1));
						if (min > max) {
							int oldMin = min;
							min = max;
							max = oldMin;
						}
						if (max - min > 5) {
							throw new RuntimeException("EiraIRC: Port ranges bigger than 5 are not allowed! Split them up if you really have to.");
						}
						for (int j = min; j <= max; j++) {
							portList.add(j);
						}
					} else {
						portList.add(Integer.parseInt(portRange));
					}
				}
				return ArrayUtils.toPrimitive(portList.toArray(new Integer[portList.size()]));
			} catch (NumberFormatException e) {
				return new int[] { defaultPort };
			}
		}
		return new int[] { defaultPort };
	}

	public static String getModpackId() {
		return "";
	}
}
