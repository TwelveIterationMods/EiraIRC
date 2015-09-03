// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.util;

import com.google.common.collect.Lists;
import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.AuthManager;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.base.ServiceConfig;
import net.blay09.mods.eirairc.config.base.ServiceSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.Sys;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

public class Utils {

	public static void sendLocalizedMessage(ICommandSender sender, String key, Object... args) {
		if(EiraIRCAPI.hasClientSideInstalled(sender)) {
			sender.addChatMessage(new ChatComponentTranslation("eirairc:" + key, args));
		} else {
			sender.addChatMessage(new ChatComponentText(new ChatComponentTranslation("eirairc:" + key, args).getUnformattedText()));
		}
	}
	
	public static void addMessageToChat(IChatComponent chatComponent) {
		if(MinecraftServer.getServer() != null && MinecraftServer.getServer().getConfigurationManager() != null && !MinecraftServer.getServer().isSinglePlayer()) {
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(translateToDefault(chatComponent));
		} else {
			if(Minecraft.getMinecraft().thePlayer != null) {
				Minecraft.getMinecraft().thePlayer.addChatMessage(chatComponent);
			}
		}
	}
	
	public static String unquote(String s) {
		return s.startsWith("\"") ? s.substring(1, s.length() - 1) : s;
	}
	
	public static String quote(String s) {
		return "\"" + s + "\"";
	}
	
	public static String getNickIRC(EntityPlayer player, IRCContext context) {
		return MessageFormat.formatNick(player.getDisplayName(), context, MessageFormat.Target.IRC, MessageFormat.Mode.Message);
	}

	public static String getNickGame(EntityPlayer player) {
		return player.getDisplayName();
	}

	public static String getServerAddress() {
		ServerData serverData = Minecraft.getMinecraft().func_147104_D(); // getCurrentServerData
		if(serverData != null) {
			return serverData.serverIP;
		}
		return null;
	}

	public static boolean isOP(ICommandSender sender) {
		if(MinecraftServer.getServer() == null || (MinecraftServer.getServer().isSinglePlayer() && !MinecraftServer.getServer().isDedicatedServer())) {
			return true;
		}
		return sender.canCommandSenderUseCommand(3, "");
	}

	public static void addConnectionsToList(List<String> list) {
		for(IRCConnection connection : ConnectionManager.getConnections()) {
			list.add(connection.getHost());
		}
	}

	public static void doNickServ(IRCConnection connection, ServerConfig config) {
		ServiceSettings settings = ServiceConfig.getSettings(connection.getHost(), connection.getServerType());
		AuthManager.NickServData nickServData = AuthManager.getNickServData(config.getIdentifier());
		if(nickServData != null) {
			connection.irc(settings.getIdentifyCommand(nickServData.username, nickServData.password));
		}
	}
	
	public static void sendUserList(ICommandSender player, IRCConnection connection, IRCChannel channel) {
		Collection<IRCUser> userList = channel.getUserList();
		if(userList.size() == 0) {
			if(player == null) {
				addMessageToChat(new ChatComponentTranslation("eirairc:commands.who.noUsersOnline", connection.getHost(), channel.getName()));
			} else {
				sendLocalizedMessage(player, "commands.who.noUsersOnline", connection.getHost(), channel.getName());
			}
			return;
		}
		if(player == null) {
			addMessageToChat(new ChatComponentTranslation("eirairc:commands.who.usersOnline", connection.getHost(), userList.size(), channel.getName()));
		} else {
			sendLocalizedMessage(player, "commands.who.usersOnline", connection.getHost(), userList.size(), channel.getName());
		}
		String s = " * ";
		for(IRCUser user : userList) {
			if(s.length() + user.getName().length() > Globals.CHAT_MAX_LENGTH) {
				if(player == null) {
					addMessageToChat(new ChatComponentText(s));
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
				addMessageToChat(new ChatComponentText(s));
			} else {
				player.addChatMessage(new ChatComponentText(s));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void sendPlayerList(IRCContext context) {
		if(MinecraftServer.getServer() == null || MinecraftServer.getServer().isSinglePlayer()) {
			return;
		}
		List<EntityPlayer> playerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		if(playerList.size() == 0) {
			if(context instanceof IRCUser) {
				context.notice(I19n.format("eirairc:bot.noPlayersOnline"));
			} else if(context instanceof IRCChannel) {
				context.message(I19n.format("eirairc:bot.noPlayersOnline"));
			}
			return;
		}
		if(context instanceof IRCUser) {
			context.notice(I19n.format("eirairc:bot.playersOnline", playerList.size()));
		} else if(context instanceof IRCChannel) {
			context.message(I19n.format("eirairc:bot.playersOnline", playerList.size()));
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
			IRCConnection connection = ConnectionManager.getDefaultConnection();
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
			return "EiraBot" + Math.round(Math.random() * 10000);
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
		} catch (URISyntaxException | MalformedURLException e) {
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
	
	public static String extractHost(String url) {
		int colonIdx = url.indexOf(':');
		int lastColonIdx = url.lastIndexOf(':');
		boolean isIPV6 = colonIdx != lastColonIdx;
		if(isIPV6) {
			int endIdx = url.lastIndexOf(']');
			if(endIdx != -1) {
				return url.substring(0, endIdx);
			} else {
				return url;
			}
		} else {
			if(lastColonIdx != -1) {
				return url.substring(0, lastColonIdx);
			} else {
				return url;
			}
		}
	}

	public static int[] extractPorts(String url, int defaultPort) {
		int colonIdx = url.indexOf(':');
		int lastColonIdx = url.indexOf(':');
		boolean isIPV6 = colonIdx != lastColonIdx;
		int portIdx = lastColonIdx;
		if(isIPV6) {
			int endIdx = url.lastIndexOf(']');
			if(endIdx == -1) {
				portIdx = -1;
			}
		}
		if(portIdx != -1) {
			try {
				String[] portRanges = url.substring(portIdx + 1).split("\\+");
				List<Integer> portList = Lists.newArrayList();
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

	public static IChatComponent translateToDefault(IChatComponent component) {
		if(component instanceof ChatComponentText) {
			return translateChildrenToDefault((ChatComponentText) component);
		} else if(component instanceof ChatComponentTranslation) {
			return translateComponentToDefault((ChatComponentTranslation) component);
		}
		return null;
	}

	private static IChatComponent translateChildrenToDefault(ChatComponentText chatComponent) {
		ChatComponentText copyComponent = new ChatComponentText(chatComponent.getChatComponentText_TextValue());
		copyComponent.setChatStyle(chatComponent.getChatStyle());
		for(Object object : chatComponent.getSiblings()) {
			IChatComponent adjustedComponent = translateToDefault((IChatComponent) object);
			if(adjustedComponent != null) {
				copyComponent.appendSibling(adjustedComponent);
			}
		}
		return copyComponent;
	}

	public static IChatComponent translateComponentToDefault(ChatComponentTranslation chatComponent) {
		Object[] formatArgs = chatComponent.getFormatArgs();
		Object[] copyFormatArgs = new Object[formatArgs.length];
		for(int i = 0; i < formatArgs.length; i++) {
			if(formatArgs[i] instanceof IChatComponent) {
				copyFormatArgs[i] = translateToDefault((IChatComponent) formatArgs[i]);
			} else {
				ChatComponentText textComponent = new ChatComponentText(formatArgs[i] == null ? "null" : formatArgs[i].toString());
				textComponent.getChatStyle().setParentStyle(chatComponent.getChatStyle());
				copyFormatArgs[i] = textComponent;
			}
		}
		ChatComponentText translateComponent = new ChatComponentText(I19n.format(chatComponent.getKey(), copyFormatArgs));
		translateComponent.setChatStyle(chatComponent.getChatStyle());
		for(Object object : chatComponent.getSiblings()) {
			IChatComponent adjustedComponent = translateToDefault((IChatComponent) object);
			if(adjustedComponent != null) {
				translateComponent.appendSibling(adjustedComponent);
			}
		}
		return translateComponent;
	}
}
