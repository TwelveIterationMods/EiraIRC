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
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.ServiceConfig;
import net.blay09.mods.eirairc.config.ServiceSettings;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.irc.ssl.IRCConnectionSSLImpl;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class Utils {

	private static final char INVALID_COLOR = 'n';
	private static final int MAX_CHAT_LENGTH = 100;
	private static final Pattern urlPattern = Pattern
			.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
	private static final Pattern ircColorPattern = Pattern
			.compile("\u0003([0-9][0-9]?)(?:[,][0-9][0-9]?)?");
	private static final Pattern mcColorPattern = Pattern.compile("\u00a7([0-9a-f])");
	private static final Pattern playerTagPattern = Pattern
			.compile("[\\[][^\\]]+[\\]]");
	private static final String DEFAULT_USERNAME = "EiraBot";

	public static void sendLocalizedMessage(ICommandSender sender, String key,
			Object... args) {
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler()
				.getPlayerInfo(sender.getCommandSenderName());
		if (playerInfo.modInstalled) {
			sender.sendChatToPlayer(getLocalizedChatMessage(key, args));
		} else {
			sendUnlocalizedMessage(sender, getLocalizedChatMessage(key, args)
					.toString());
		}
	}

	public static void sendUnlocalizedMessage(ICommandSender sender, String text) {
		sender.sendChatToPlayer(getUnlocalizedChatMessage(text));
	}

	public static String getLocalizedMessageNoPrefix(String key, Object... args) {
		return StatCollector.translateToLocalFormatted(key, args);
	}

	public static String getLocalizedMessage(String key, Object... args) {
		return StatCollector.translateToLocalFormatted(EiraIRC.MOD_ID + ":"
				+ key, args);
	}

	public static ChatMessageComponent getLocalizedChatMessage(String key,
			Object... args) {
		return ChatMessageComponent.createFromTranslationWithSubstitutions(
				EiraIRC.MOD_ID + ":" + key, args);
	}

	public static ChatMessageComponent getLocalizedChatMessageNoPrefix(
			String key, Object... args) {
		return ChatMessageComponent.createFromTranslationWithSubstitutions(key,
				args);
	}

	public static ChatMessageComponent getUnlocalizedChatMessage(String text) {
		return ChatMessageComponent.createFromText(text);
	}

	public static void addMessageToChat(String text) {
		if (text == null) {
			return;
		}
		if (text.length() <= MAX_CHAT_LENGTH) {
			if (MinecraftServer.getServer() != null) {
				MinecraftServer.getServer().getConfigurationManager()
						.sendChatMsg(Utils.getUnlocalizedChatMessage(text));
			} else {
				if (Minecraft.getMinecraft().thePlayer != null) {
					Minecraft.getMinecraft().thePlayer.sendChatToPlayer(Utils
							.getUnlocalizedChatMessage(text));
				}
			}
		} else {
			for (String string : wrapString(text, MAX_CHAT_LENGTH)) {
				if (MinecraftServer.getServer() != null) {
					MinecraftServer
							.getServer()
							.getConfigurationManager()
							.sendChatMsg(
									Utils.getUnlocalizedChatMessage(string));
				} else {
					if (Minecraft.getMinecraft().thePlayer != null) {
						Minecraft.getMinecraft().thePlayer
								.sendChatToPlayer(Utils
										.getUnlocalizedChatMessage(string));
					}
				}
			}
		}
	}

	public static void addMessageToChat(ChatMessageComponent chatComponent) {
		if (MinecraftServer.getServer() != null && !MinecraftServer.getServer().isSinglePlayer()) {
			MinecraftServer.getServer().getConfigurationManager()
					.sendChatMsg(chatComponent);
		} else {
			if (Minecraft.getMinecraft().thePlayer != null) {
				Minecraft.getMinecraft().thePlayer
						.sendChatToPlayer(chatComponent);
			}
		}
	}

	public static List<String> wrapString(String text, int maxLength) {
		List<String> tmpStrings = new ArrayList<String>();
		if (text == null) {
			return tmpStrings;
		}
		text = text.trim();
		if (text.length() <= maxLength) {
			tmpStrings.add(text);
			return tmpStrings;
		}
		while (text.length() > maxLength) {
			int i = maxLength;
			while (true) {
				if (text.charAt(i) == ' ') {
					break;
				} else if (i == 0) {
					i = maxLength;
					break;
				}
				i--;
			}
			tmpStrings.add(text.substring(0, i));
			text = text.substring(i + 1).trim();
		}
		if (text.length() > 0) {
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
		if (colorCode == INVALID_COLOR) {
			return name;
		}
		return Globals.COLOR_CODE_PREFIX + String.valueOf(colorCode) + name
				+ Globals.COLOR_CODE_PREFIX + "f";
	}

	public static String getNickIRC(EntityPlayer player) {
		return addPreSuffix(getAliasForPlayer(player));
	}

	public static String getNickGame(EntityPlayer player) {
		return getAliasForPlayer(player);
	}

	public static String getNickGame(IRCChannel channel, IRCUser user) {
		return user.getName();
	}

	public static char getColorCodeForUser(IRCChannel channel, IRCUser user) {
		if (channel == null) {
			return getColorCode(DisplayConfig.ircPrivateColor);
		}
		if (user.isOperator(channel)) {
			if (!DisplayConfig.ircOpColor.isEmpty()) {
				return getColorCode(DisplayConfig.ircOpColor);
			}
		} else if (user.hasVoice(channel)) {
			if (!DisplayConfig.ircVoiceColor.isEmpty()) {
				return getColorCode(DisplayConfig.ircVoiceColor);
			}
		}
		return getColorCode(DisplayConfig.ircColor);
	}

	public static char getColorCodeForPlayer(EntityPlayer player) {
		NBTTagCompound tagCompound = player.getEntityData()
				.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
				.getCompoundTag("EiraIRC");
		boolean isOP = Utils.isServerSide() && isOP(player);
		if (!DisplayConfig.enableNameColors && !isOP) {
			return INVALID_COLOR;
		}
		String colorName = tagCompound.getString("NameColor");
		if (!colorName.isEmpty()) {
			return getColorCode(colorName);
		} else if (isOP) {
			if (!DisplayConfig.mcOpColor.isEmpty()) {
				return getColorCode(DisplayConfig.mcOpColor);
			}
		}
		return getColorCode(DisplayConfig.mcColor);
	}

	public static EnumChatFormatting getColorFormatting(String colorName) {
		if (colorName.isEmpty()) {
			return null;
		}
		colorName = colorName.toLowerCase();
		EnumChatFormatting colorFormatting = EnumChatFormatting.RESET;
		if (colorName.equals("black")) {
			colorFormatting = EnumChatFormatting.BLACK;
		} else if (colorName.equals("darkblue")
				|| colorName.equals("dark blue")) {
			colorFormatting = EnumChatFormatting.DARK_BLUE;
		} else if (colorName.equals("green")) {
			colorFormatting = EnumChatFormatting.DARK_GREEN;
		} else if (colorName.equals("cyan")) {
			colorFormatting = EnumChatFormatting.DARK_AQUA;
		} else if (colorName.equals("darkred") || colorName.equals("dark red")) {
			colorFormatting = EnumChatFormatting.DARK_RED;
		} else if (colorName.equals("purple")) {
			colorFormatting = EnumChatFormatting.DARK_PURPLE;
		} else if (colorName.equals("gold") || colorName.equals("orange")) {
			colorFormatting = EnumChatFormatting.GOLD;
		} else if (colorName.equals("gray") || colorName.equals("grey")) {
			colorFormatting = EnumChatFormatting.GRAY;
		} else if (colorName.equals("darkgray") || colorName.equals("darkgrey")
				|| colorName.equals("dark gray")
				|| colorName.equals("dark grey")) {
			colorFormatting = EnumChatFormatting.DARK_GRAY;
		} else if (colorName.equals("blue")) {
			colorFormatting = EnumChatFormatting.BLUE;
		} else if (colorName.equals("lime")) {
			colorFormatting = EnumChatFormatting.GREEN;
		} else if (colorName.equals("lightblue")
				|| colorName.equals("light blue")) {
			colorFormatting = EnumChatFormatting.AQUA;
		} else if (colorName.equals("red")) {
			colorFormatting = EnumChatFormatting.RED;
		} else if (colorName.equals("magenta") || colorName.equals("pink")) {
			colorFormatting = EnumChatFormatting.LIGHT_PURPLE;
		} else if (colorName.equals("yellow")) {
			colorFormatting = EnumChatFormatting.YELLOW;
		} else if (colorName.equals("white")) {
			colorFormatting = EnumChatFormatting.WHITE;
		}
		return colorFormatting;
	}

	public static EnumChatFormatting getColorFormattingForUser(
			IRCChannel channel, IRCUser user) {
		if (channel == null) {
			return getColorFormatting(DisplayConfig.ircPrivateColor);
		}
		if (user.isOperator(channel)) {
			if (!DisplayConfig.ircOpColor.isEmpty()) {
				return getColorFormatting(DisplayConfig.ircOpColor);
			}
		} else if (user.hasVoice(channel)) {
			if (!DisplayConfig.ircVoiceColor.isEmpty()) {
				return getColorFormatting(DisplayConfig.ircVoiceColor);
			}
		}
		return getColorFormatting(DisplayConfig.ircColor);
	}

	public static EnumChatFormatting getColorFormattingForPlayer(
			EntityPlayer player) {
		NBTTagCompound tagCompound = player.getEntityData()
				.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
				.getCompoundTag(Globals.NBT_EIRAIRC);
		boolean isOP = Utils.isServerSide() && isOP(player);
		if (!DisplayConfig.enableNameColors && !isOP) {
			return null;
		}
		String colorName = tagCompound.getString(Globals.NBT_NAMECOLOR);
		if (!colorName.isEmpty()) {
			return getColorFormatting(colorName);
		} else if (isOP) {
			if (!DisplayConfig.mcOpColor.isEmpty()) {
				return getColorFormatting(DisplayConfig.mcOpColor);
			}
		}
		return getColorFormatting(DisplayConfig.mcColor);
	}

	public static String getAliasForPlayer(EntityPlayer player) {
		if (!GlobalConfig.enableAliases) {
			return player.getCommandSenderName();
		}
		String name = player.getEntityData()
				.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
				.getCompoundTag("EiraIRC").getString("Alias");
		if (name.isEmpty()) {
			name = player.getCommandSenderName();
		}
		return name;
	}

	public static boolean isOP(ICommandSender sender) {
		if (MinecraftServer.getServer() == null || MinecraftServer.getServer().isSinglePlayer()) {
			return true;
		}
		if (sender instanceof EntityPlayer) {
			return MinecraftServer.getServer().getConfigurationManager()
					.isPlayerOpped(sender.getCommandSenderName().toLowerCase());
		}
		return true;
	}

	public static boolean isValidColor(String colorName) {
		return getColorCode(colorName) != INVALID_COLOR;
	}

	public static char getColorCode(String colorName) {
		if (colorName.isEmpty()) {
			return INVALID_COLOR;
		}
		colorName = colorName.toLowerCase();
		char colorCode = INVALID_COLOR;
		if (colorName.equals("black")) {
			colorCode = '0';
		} else if (colorName.equals("darkblue")) {
			colorCode = '1';
		} else if (colorName.equals("green")) {
			colorCode = '2';
		} else if (colorName.equals("cyan")) {
			colorCode = '3';
		} else if (colorName.equals("darkred")) {
			colorCode = '4';
		} else if (colorName.equals("purple")) {
			colorCode = '5';
		} else if (colorName.equals("gold")) {
			colorCode = '6';
		} else if (colorName.equals("gray") || colorName.equals("grey")) {
			colorCode = '7';
		} else if (colorName.equals("darkgray") || colorName.equals("darkgrey")) {
			colorCode = '8';
		} else if (colorName.equals("blue")) {
			colorCode = '9';
		} else if (colorName.equals("lime")) {
			colorCode = 'a';
		} else if (colorName.equals("lightblue")) {
			colorCode = 'b';
		} else if (colorName.equals("red")) {
			colorCode = 'c';
		} else if (colorName.equals("magenta") || colorName.equals("pink")) {
			colorCode = 'd';
		} else if (colorName.equals("yellow")) {
			colorCode = 'e';
		} else if (colorName.equals("white")) {
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
		for (IRCConnection connection : EiraIRC.instance.getConnections()) {
			list.add(connection.getHost());
		}
	}

	public static void addBooleansToList(List<String> list) {
		list.add("true");
		list.add("false");
	}

	public static String getCurrentServerName() {
		if (MinecraftServer.getServer() != null) {
			if (MinecraftServer.getServer().isSinglePlayer()) {
				return "Singleplayer";
			} else {
				return MinecraftServer.getServer().getServerHostname();
			}
		} else {
			return "Multiplayer";
		}
	}

	public static String filterLinks(String message) {
		String[] s = message.split(" ");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length; i++) {
			Matcher matcher = urlPattern.matcher(s[i]);
			sb.append(((i > 0) ? " " : "")
					+ matcher.replaceAll(Utils
							.getLocalizedMessage("irc.general.linkRemoved")));
		}
		return sb.toString();
	}

	public static EnumChatFormatting getColorFromIRCColorCode(int code) {
		switch (code) {
		case 0:
			return EnumChatFormatting.WHITE;
		case 1:
			return EnumChatFormatting.BLACK;
		case 2:
			return EnumChatFormatting.DARK_BLUE;
		case 3:
			return EnumChatFormatting.DARK_GREEN;
		case 4:
			return EnumChatFormatting.RED;
		case 5:
			return EnumChatFormatting.DARK_RED;
		case 6:
			return EnumChatFormatting.DARK_PURPLE;
		case 7:
			return EnumChatFormatting.GOLD;
		case 8:
			return EnumChatFormatting.YELLOW;
		case 9:
			return EnumChatFormatting.GREEN;
		case 10:
			return EnumChatFormatting.AQUA;
		case 11:
			return EnumChatFormatting.BLUE;
		case 12:
			return EnumChatFormatting.DARK_AQUA;
		case 13:
			return EnumChatFormatting.LIGHT_PURPLE;
		case 14:
			return EnumChatFormatting.DARK_GRAY;
		case 15:
			return EnumChatFormatting.GRAY;
		}
		return null;
	}

	public static String filterAllowedCharacters(String message,
			boolean killMCColorCodes, boolean convertIRCColorCodes) {
		if (killMCColorCodes) {
			Matcher matcher = mcColorPattern.matcher(message);
			while(matcher.find()) {
				message = message.replaceFirst(Matcher.quoteReplacement(matcher.group()), "");
			}
		}
		if (convertIRCColorCodes) {
			Matcher matcher = ircColorPattern.matcher(message);
			while (matcher.find()) {
				String colorMatch = matcher.group(1);
				int colorCode = Integer.parseInt(colorMatch);
				EnumChatFormatting colorFormat = getColorFromIRCColorCode(colorCode);
				String repl = Matcher.quoteReplacement(matcher.group());
				message = message.replaceFirst(repl, Globals.COLOR_CODE_PREFIX + colorFormat.func_96298_a());
			}
			message = message.replaceAll("\u000f", Globals.COLOR_CODE_PREFIX + "r"); // reset
			message = message.replaceAll("\u0002", Globals.COLOR_CODE_PREFIX + "l"); // bold
			message = message.replaceAll("\u001f", Globals.COLOR_CODE_PREFIX + "n"); // underline
			message = message.replaceAll("\u0016", Globals.COLOR_CODE_PREFIX + "o"); // italic
		} else {
			message = ircColorPattern.matcher(message).replaceAll("");
		}
		StringBuilder sb = new StringBuilder();
		char[] charArray = message.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if (isAllowedCharacter(charArray[i])) {
				sb.append(charArray[i]);
			}
		}
		return sb.toString();
	}

	private static boolean isAllowedCharacter(char c) {
		return c >= 32 && c != 127;
	}

	private static String filterPlayerTags(String playerName) {
		return playerTagPattern.matcher(playerName).replaceAll("");
	}

	public static IRCConnectionImpl connectTo(ServerConfig config) {
		IRCConnectionImpl connection;
		if (config.isSecureConnection()) {
			connection = new IRCConnectionSSLImpl(config.getHost(),
					config.getServerPassword(),
					ConfigHelper.getFormattedNick(config), config.getIdent(),
					config.getDescription());
		} else {
			connection = new IRCConnectionImpl(config.getHost(),
					config.getServerPassword(),
					ConfigHelper.getFormattedNick(config), config.getIdent(),
					config.getDescription());
		}
		connection.setCharset(GlobalConfig.charset);
		connection.setBot(new IRCBotImpl(connection));
		if (connection.start()) {
			return connection;
		}
		return null;
	}

	public static void doNickServ(IRCConnection connection, ServerConfig config) {
		ServiceSettings settings = ServiceConfig.getSettings(
				connection.getHost(), connection.getServerType());
		String username = config.getNickServName();
		String password = config.getNickServPassword();
		if (username == null || username.isEmpty() || password == null
				|| password.isEmpty()) {
			return;
		}
		connection.irc(settings.getIdentifyCommand(username, password));
	}

	public static void sendUserList(ICommandSender player,
			IRCConnection connection, IRCChannel channel) {
		Collection<IRCUser> userList = channel.getUserList();
		if (userList.size() == 0) {
			if (player == null) {
				addMessageToChat(Utils.getLocalizedMessage(
						"irc.who.noUsersOnline", connection.getHost(),
						channel.getName()));
			} else {
				sendLocalizedMessage(player, "irc.who.noUsersOnline",
						connection.getHost(), channel.getName());
			}
			return;
		}
		if (player == null) {
			addMessageToChat(Utils.getLocalizedMessage("irc.who.usersOnline",
					connection.getHost(), userList.size(), channel.getName()));
		} else {
			sendLocalizedMessage(player, "irc.who.usersOnline",
					connection.getHost(), userList.size(), channel.getName());
		}
		String s = " * ";
		for (IRCUser user : userList) {
			if (s.length() + user.getName().length() > Globals.CHAT_MAX_LENGTH) {
				if (player == null) {
					addMessageToChat(s);
				} else {
					sendUnlocalizedMessage(player, s);
				}
				s = " * ";
			}
			if (s.length() > 3) {
				s += ", ";
			}
			s += user.getName();
		}
		if (s.length() > 3) {
			if (player == null) {
				addMessageToChat(s);
			} else {
				sendUnlocalizedMessage(player, s);
			}
		}
	}

	public static void sendPlayerList(IRCUser user) {
		if (MinecraftServer.getServer() == null
				|| MinecraftServer.getServer().isSinglePlayer()) {
			return;
		}
		List<EntityPlayer> playerList = MinecraftServer.getServer()
				.getConfigurationManager().playerEntityList;
		if (playerList.size() == 0) {
			user.notice(getLocalizedMessage("irc.bot.noPlayersOnline"));
			return;
		}
		user.notice(getLocalizedMessage("irc.bot.playersOnline",
				playerList.size()));
		String s = " * ";
		for (int i = 0; i < playerList.size(); i++) {
			EntityPlayer entityPlayer = playerList.get(i);
			String alias = getNickIRC(entityPlayer);
			if (s.length() + alias.length() > Globals.CHAT_MAX_LENGTH) {
				user.notice(s);
				s = " * ";
			}
			if (s.length() > 3) {
				s += ", ";
			}
			s += alias;
		}
		if (s.length() > 3) {
			user.notice(s);
		}
	}

	public static IRCContext getSuggestedTarget() {
		IRCContext result = EiraIRC.instance.getChatSessionHandler()
				.getIRCTarget();
		if (result == null) {
			IRCConnection connection = EiraIRC.instance.getDefaultConnection();
			if (connection != null) {
				if (connection.getChannels().size() == 1) {
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
		if (username == null) {
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
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop()
				: null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
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
		return MinecraftServer.getServer() != null
				&& !MinecraftServer.getServer().isSinglePlayer();
	}

	public static String[] shiftArgs(String[] args, int offset) {
		String[] shiftedArgs = new String[args.length - offset];
		for (int i = offset; i < args.length; i++) {
			shiftedArgs[i - offset] = args[i];
		}
		return shiftedArgs;
	}

	public static String joinStrings(String[] arr, String delimiter) {
		return joinStrings(arr, delimiter, 0);
	}

	public static String joinStrings(String[] args, String delimiter,
			int startIdx) {
		StringBuilder sb = new StringBuilder();
		for (int i = startIdx; i < args.length; i++) {
			if (i > startIdx) {
				sb.append(delimiter);
			}
			sb.append(args[i]);
		}
		return sb.toString();
	}

	public static String getMessageFormat(IRCBot bot, IRCContext context,
			boolean isEmote) {
		if (context instanceof IRCUser) {
			if (isEmote) {
				return ConfigHelper.getDisplayFormat(bot
						.getDisplayFormat(context)).ircPrivateEmote;
			} else {
				return ConfigHelper.getDisplayFormat(bot
						.getDisplayFormat(context)).ircPrivateMessage;
			}
		} else {
			if (isEmote) {
				return ConfigHelper.getDisplayFormat(bot
						.getDisplayFormat(context)).ircChannelEmote;
			} else {
				return ConfigHelper.getDisplayFormat(bot
						.getDisplayFormat(context)).ircChannelMessage;
			}
		}
	}

	public static String formatMessage(String format, IRCContext context, ICommandSender sender,
			String message, boolean colorName, boolean stripTags, boolean addPreSuffix) {
		return formatChatComponent(format, context, sender, message, colorName,
				stripTags, addPreSuffix).toString();
	}

	public static ChatMessageComponent formatChatComponent(String format, IRCContext context, ICommandSender sender, String message, boolean colorName, boolean stripTags, boolean addPreSuffix) {
		ChatMessageComponent root = ChatMessageComponent.createFromText("");
		StringBuilder sb = new StringBuilder();
		int currentIdx = 0;
		while (currentIdx < format.length()) {
			char c = format.charAt(currentIdx);
			if (c == '{') {
				int tokenEnd = format.indexOf('}', currentIdx);
				if (tokenEnd != -1) {
					boolean validToken = true;
					String token = format.substring(currentIdx + 1, tokenEnd);
					ChatMessageComponent component = null;
					if (token.equals("SERVER")) {
						component = ChatMessageComponent.createFromText(getCurrentServerName());
					} else if (token.equals("USER")) {
						component = ChatMessageComponent.createFromText(sender.getCommandSenderName());
					} else if(token.equals("CHANNEL")) {
						component = ChatMessageComponent.createFromText(context != null ? context.getName() : "");
					} else if (token.equals("NICK")) {
						if (sender instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) sender;
							String displayName = player.getDisplayName();
							if (stripTags) {
								displayName = filterPlayerTags(displayName);
							}
							if (addPreSuffix) {
								displayName = addPreSuffix(displayName);
							}
							if (colorName) {
								EnumChatFormatting nameColor = Utils.getColorFormattingForPlayer(player);
								if(nameColor != null) {
									displayName = Globals.COLOR_CODE_PREFIX + nameColor.func_96298_a() + displayName + Globals.COLOR_CODE_PREFIX + 'f';
								}
							}
							component = ChatMessageComponent.createFromText(displayName);
						} else {
							component = ChatMessageComponent.createFromText(sender.getCommandSenderName());
						}

					} else if (token.equals("MESSAGE")) {
						component = ChatMessageComponent
								.createFromText(message);
					} else {
						validToken = false;
					}
					if (validToken) {
						if (sb.length() > 0) {
							root.appendComponent(ChatMessageComponent
									.createFromText(sb.toString()));
							sb = new StringBuilder();
						}
						root.appendComponent(component);
						currentIdx += token.length() + 2;
						continue;
					}
				}
			}
			sb.append(c);
			currentIdx++;
		}
		if (sb.length() > 0) {
			root.appendComponent(ChatMessageComponent.createFromText(sb
					.toString()));
		}
		return root;
	}

	public static String formatMessage(String format,
			IRCConnection connection, IRCChannel channel, IRCUser user,
			String message, boolean colorName) {
		return formatChatComponent(format, connection, channel, user, message,
				colorName).toString();
	}

	public static ChatMessageComponent formatChatComponent(String format,
			IRCConnection connection, IRCChannel channel, IRCUser user,
			String message, boolean colorName) {
		ChatMessageComponent root = ChatMessageComponent.createFromText("");
		StringBuilder sb = new StringBuilder();
		int currentIdx = 0;
		while (currentIdx < format.length()) {
			char c = format.charAt(currentIdx);
			if (c == '{') {
				int tokenEnd = format.indexOf('}', currentIdx);
				if (tokenEnd != -1) {
					boolean validToken = true;
					String token = format.substring(currentIdx + 1, tokenEnd);
					ChatMessageComponent component = null;
					if (token.equals("SERVER")) {
						component = ChatMessageComponent
								.createFromText(connection.getIdentifier());
					} else if (token.equals("CHANNEL")) {
						component = ChatMessageComponent.createFromText(channel
								.getName());
					} else if (token.equals("USER")) {
						if(user != null) {
							component = ChatMessageComponent.createFromText(user.getIdentifier());
						} else {
							component = ChatMessageComponent.createFromText(connection.getIdentifier());
						}
					} else if (token.equals("NICK")) {
						if(user != null) {
							String displayName = getNickGame(channel, user);
							if (colorName) {
								displayName = Globals.COLOR_CODE_PREFIX + Utils.getColorFormattingForUser(channel, user).func_96298_a() + displayName + Globals.COLOR_CODE_PREFIX + 'f';
							}
							component = ChatMessageComponent.createFromText(displayName);
							if (colorName) {
								component.setColor(Utils.getColorFormattingForUser(channel, user));
							}
						} else {
							component = ChatMessageComponent.createFromText(connection.getIdentifier());
						}
					} else if (token.equals("MESSAGE")) {
						component = ChatMessageComponent
								.createFromText(message);
					} else {
						validToken = false;
					}
					if (validToken) {
						if (sb.length() > 0) {
							root.appendComponent(ChatMessageComponent
									.createFromText(sb.toString()));
							sb = new StringBuilder();
						}
						root.appendComponent(component);
						currentIdx += token.length() + 2;
						continue;
					}
				}
			}
			sb.append(c);
			currentIdx++;
		}
		if (sb.length() > 0) {
			root.appendComponent(ChatMessageComponent.createFromText(sb
					.toString()));
		}
		return root;
	}

	public static String extractHost(String url) {
		int portIdx = url.indexOf(':');
		if (portIdx != -1) {
			return url.substring(0, portIdx);
		} else {
			return url;
		}
	}

	public static int extractPort(String url, int defaultPort) {
		int portIdx = url.indexOf(':');
		if (portIdx != -1) {
			try {
				return Integer.parseInt(url.substring(portIdx + 1));
			} catch (NumberFormatException e) {
				return defaultPort;
			}
		}
		return defaultPort;
	}

}
