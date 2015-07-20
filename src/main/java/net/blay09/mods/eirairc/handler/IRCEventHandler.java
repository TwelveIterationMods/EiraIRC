// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.addon.Compatibility;
import net.blay09.mods.eirairc.addon.EiraMoticonsAddon;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.IRCReplyCodes;
import net.blay09.mods.eirairc.api.event.*;
import net.blay09.mods.eirairc.api.irc.*;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.*;
import net.blay09.mods.eirairc.config.settings.*;
import net.blay09.mods.eirairc.irc.*;
import net.blay09.mods.eirairc.util.*;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IRCEventHandler {

	private static final Logger logger = LogManager.getLogger();

	public static void fireNickChangeEvent(IRCConnectionImpl connection, IRCMessage msg, IRCUser user, String oldNick, String newNick) {
		IRCUserNickChangeEvent event = new IRCUserNickChangeEvent(connection, msg, user, oldNick, newNick);
		MinecraftForge.EVENT_BUS.post(event);
		switch (event.getResult()) {
			case DEFAULT:
				if (ConfigHelper.getGeneralSettings(user).isMuted() || IgnoreList.isIgnored(user)) {
					return;
				}
				if (SharedGlobalConfig.botSettings.getBoolean(BotBooleanComponent.RelayNickChanges)) {
					String format = ConfigHelper.getBotSettings(user).getMessageFormat().mcUserNickChange;
					format = format.replace("{OLDNICK}", oldNick);
					EiraIRCAPI.getChatHandler().addChatMessage(MessageFormat.formatChatComponent(format, connection, null, user, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
				}
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void fireUserJoinEvent(IRCConnection connection, IRCMessage message, IRCChannel channel, IRCUser user) {
		BotSettings botSettings = ConfigHelper.getBotSettings(channel);
		if(botSettings.getBoolean(BotBooleanComponent.SendAutoWho)) {
			Utils.sendPlayerList(user);
		}
		IRCUserJoinEvent event = new IRCUserJoinEvent(connection, message, channel, user);
		MinecraftForge.EVENT_BUS.post(event);
		switch (event.getResult()) {
			case DEFAULT:
				if (ConfigHelper.getGeneralSettings(channel).isMuted()) {
					return;
				}
				if (botSettings.getBoolean(BotBooleanComponent.RelayIRCJoinLeave)) {
					String format = ConfigHelper.getBotSettings(channel).getMessageFormat().mcUserJoin;
					EiraIRCAPI.getChatHandler().addChatMessage(MessageFormat.formatChatComponent(format, connection, channel, user, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
				}
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void fireUserLeaveEvent(IRCConnection connection, IRCMessage message, IRCChannel channel, IRCUser user, String quitMessage) {
		IRCUserLeaveEvent event = new IRCUserLeaveEvent(connection, message, channel, user, quitMessage);
		MinecraftForge.EVENT_BUS.post(event);
		switch (event.getResult()) {
			case DEFAULT:
				if (ConfigHelper.getGeneralSettings(channel).isMuted()) {
					return;
				}
				if (ConfigHelper.getBotSettings(channel).getBoolean(BotBooleanComponent.RelayIRCJoinLeave)) {
					String format = ConfigHelper.getBotSettings(channel).getMessageFormat().mcUserLeave;
					EiraIRCAPI.getChatHandler().addChatMessage(MessageFormat.formatChatComponent(format, connection, channel, user, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
				}
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void fireUserQuitEvent(IRCConnection connection, IRCMessage message, IRCUser user, String quitMessage) {
		IRCUserQuitEvent event = new IRCUserQuitEvent(connection, message, user, quitMessage);
		MinecraftForge.EVENT_BUS.post(event);
		switch (event.getResult()) {
			case DEFAULT:
				if (ConfigHelper.getGeneralSettings(user).isMuted()) {
					return;
				}
				if (SharedGlobalConfig.botSettings.getBoolean(BotBooleanComponent.RelayIRCJoinLeave)) {
					String format = ConfigHelper.getBotSettings(user).getMessageFormat().mcUserQuit;
					EiraIRCAPI.getChatHandler().addChatMessage(MessageFormat.formatChatComponent(format, connection, null, user, quitMessage, MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
				}
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void firePrivateChatEvent(IRCConnection connection, IRCUser sender, IRCMessage rawMessage, String message, boolean isEmote, boolean isNotice) {
		if(sender != null) {
			if (IgnoreList.isIgnored(sender)) {
				logger.info("Ignored message by " + sender.getName() + ": " + message);
				return;
			}
			if (!isNotice) {
				if (((IRCBotImpl) connection.getBot()).processCommand(null, sender, message)) {
					return;
				} else {
					if (connection.getBot().isServerSide()) {
						sender.notice(I19n.format("eirairc:bot.unknownCommand"));
						return;
					}
				}
			}
			if (sender.getName().equals("tmi.twitch.tv") && isNotice && connection.getHost().equals(Globals.TWITCH_SERVER) && message.equals("Login unsuccessful")) {
				connection.disconnect("");
				fireConnectionFailedEvent(connection, new RuntimeException("Wrong username or invalid oauth token."));
				return;
			}
		}
		IRCPrivateChatEvent event = new IRCPrivateChatEvent(connection, sender, rawMessage, message, isEmote, isNotice);
		MinecraftForge.EVENT_BUS.post(event);
		switch (event.getResult()) {
			case DEFAULT:
				BotSettings botSettings = ConfigHelper.getBotSettings(null);
				if (ConfigHelper.getGeneralSettings(sender).isMuted()) {
					return;
				}
				if (connection.isTwitch() && event.sender.getName().equals("jtv")) {
					return;
				}
				if (!botSettings.getBoolean(BotBooleanComponent.AllowPrivateMessages)) {
					if (!isNotice && sender != null) {
						sender.notice(I19n.format("eirairc:commands.msg.disabled"));
					}
					return;
				}
				if (botSettings.getBoolean(BotBooleanComponent.FilterLinks)) {
					message = MessageFormat.filterLinks(message);
				}
				String format;
				if (connection.isTwitch() && sender != null && sender.getName().equals("twitchnotify")) {
					format = "{MESSAGE}";
				} else if (isNotice) {
					format = botSettings.getMessageFormat().mcPrivateNotice;
				} else if (isEmote) {
					format = botSettings.getMessageFormat().mcPrivateEmote;
				} else {
					format = botSettings.getMessageFormat().mcPrivateMessage;
				}
				IChatComponent chatComponent = MessageFormat.formatChatComponent(format, connection, null, sender, message, MessageFormat.Target.Minecraft, (isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message));
				if (event.isNotice && botSettings.getBoolean(BotBooleanComponent.HideNotices)) {
					System.out.println(chatComponent.getUnformattedText());
					return;
				}
				String notifyMsg = chatComponent.getUnformattedText();
				if (notifyMsg.length() > 42) {
					notifyMsg = notifyMsg.substring(0, 42) + "...";
				}
				if (!event.isNotice) {
					EiraIRC.proxy.publishNotification(NotificationType.PrivateMessage, notifyMsg);
				}
				EiraIRC.instance.getChatSessionHandler().addTargetUser(sender);
				ThemeSettings theme = ConfigHelper.getTheme(sender);
				EnumChatFormatting emoteColor = theme.getColor(ThemeColorComponent.emoteTextColor);
				EnumChatFormatting twitchNameColor = (sender != null && SharedGlobalConfig.twitchNameColors.get()) ? ((IRCUserImpl) sender).getNameColor() : null;
				EnumChatFormatting noticeColor = theme.getColor(ThemeColorComponent.ircNoticeTextColor);
				if (isEmote && (emoteColor != null || twitchNameColor != null)) {
					chatComponent.getChatStyle().setColor(twitchNameColor != null ? twitchNameColor : emoteColor);
				} else if (isNotice && noticeColor != null) {
					chatComponent.getChatStyle().setColor(noticeColor);
				}
				EiraIRCAPI.getChatHandler().addChatMessage(chatComponent);
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void fireChannelChatEvent(IRCConnection connection, IRCChannel channel, IRCUser sender, IRCMessage rawMessage, String message, boolean isEmote, boolean isNotice) {
		if(sender != null && IgnoreList.isIgnored(sender)) {
			logger.info("Ignored message by " + sender.getName() + ": " + message);
			return;
		}
		if(!isNotice && message.startsWith(SharedGlobalConfig.ircCommandPrefix.get()) && ((IRCBotImpl) connection.getBot()).processCommand(channel, sender, message.substring(SharedGlobalConfig.ircCommandPrefix.get().length()))) {
			return;
		}
		if(connection.isTwitch()) {
			IRCUserImpl user = (IRCUserImpl) sender;
			if (user != null) {
				String userColor = rawMessage.getTagByKey("color");
				if (userColor != null && !userColor.isEmpty()) {
					user.setNameColor(IRCFormatting.getColorFromTwitch(userColor));
				}
				String subscriber = rawMessage.getTagByKey("subscriber");
				if (subscriber != null) {
					user.setTwitchSubscriber(subscriber.equals("1"));
				}
				String turbo = rawMessage.getTagByKey("turbo");
				if (turbo != null) {
					user.setTwitchTurbo(turbo.equals("1"));
				}
				String userType = rawMessage.getTagByKey("user-type");
				if (userType != null && !userType.isEmpty()) {
					user.setChannelUserMode(channel, IRCChannelUserMode.OPER);
				} else {
					user.setChannelUserMode(channel, null);
				}
				String displayName = rawMessage.getTagByKey("display-name");
				if(displayName != null && !displayName.isEmpty()) {
					user.setDisplayName(displayName);
				} else {
					user.setDisplayName(null);
				}
			}
		}

		IRCChannelChatEvent event = new IRCChannelChatEvent(connection, channel, sender, rawMessage, message, isEmote, isNotice);
		switch (event.getResult()) {
			case DEFAULT:
				if (ConfigHelper.getGeneralSettings(channel).isMuted()) {
					return;
				}
				if (EiraIRC.proxy.checkClientBridge(event)) {
					return;
				}
				if(sender != null && connection.isTwitch() && sender.getName().equals("jtv")) {
					return;
				}
				BotSettings botSettings = ConfigHelper.getBotSettings(channel);
				if (botSettings.getBoolean(BotBooleanComponent.FilterLinks)) {
					message = MessageFormat.filterLinks(message);
				}
				String format;
				if (connection.isTwitch() && sender != null && sender.getName().equals("twitchnotify")) {
					format = "{MESSAGE}";
				} else if (isNotice) {
					format = botSettings.getMessageFormat().mcChannelNotice;
				} else if (isEmote) {
					format = botSettings.getMessageFormat().mcChannelEmote;
				} else {
					format = botSettings.getMessageFormat().mcChannelMessage;
				}
				IChatComponent chatComponent = MessageFormat.formatChatComponent(format, connection, channel, sender, message, MessageFormat.Target.Minecraft, event.isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message);
				if (isNotice && botSettings.getBoolean(BotBooleanComponent.HideNotices)) {
					System.out.println(chatComponent.getUnformattedText());
					return;
				}
				ThemeSettings theme = ConfigHelper.getTheme(event.channel);
				EnumChatFormatting emoteColor = theme.getColor(ThemeColorComponent.emoteTextColor);
				EnumChatFormatting twitchNameColor = (sender != null && SharedGlobalConfig.twitchNameColors.get()) ? ((IRCUserImpl) sender).getNameColor() : null;
				EnumChatFormatting noticeColor = theme.getColor(ThemeColorComponent.ircNoticeTextColor);
				if (isEmote && (emoteColor != null || twitchNameColor != null)) {
					chatComponent.getChatStyle().setColor(twitchNameColor != null ? twitchNameColor : emoteColor);
				} else if (isNotice && noticeColor != null) {
					chatComponent.getChatStyle().setColor(noticeColor);
				}
				EiraIRCAPI.getChatHandler().addChatMessage(chatComponent);
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void fireChannelTopicEvent(IRCConnection connection, IRCMessage message, IRCChannel channel, IRCUser user, String topic) {
		IRCChannelTopicEvent event = new IRCChannelTopicEvent(connection, message, channel, user, topic);
		MinecraftForge.EVENT_BUS.post(event);
		switch (event.getResult()) {
			case DEFAULT:
				if (ConfigHelper.getGeneralSettings(channel).isMuted()) {
					return;
				}
				if (user == null) {
					IChatComponent chatComponent = MessageFormat.formatChatComponent(ConfigHelper.getBotSettings(channel).getMessageFormat().mcTopic, connection, channel, null, channel.getTopic(), MessageFormat.Target.Minecraft, MessageFormat.Mode.Message);
					EiraIRCAPI.getChatHandler().addChatMessage(chatComponent);
				} else {
					EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:general.topicChange", user.getName(), channel.getName(), channel.getTopic()));
				}
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void fireConnectedEvent(IRCConnection connection, IRCMessage message) {
		ServerConfig serverConfig = ConfigHelper.getServerConfig(connection);
		// If this is a Twitch connection, tell the server that we're a JTVCLIENT so we receive name colors.
		if(connection.isTwitch()) {
			connection.irc("CAP REQ :twitch.tv/tags");
		}
		Utils.doNickServ(connection, serverConfig);
		for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
			GeneralSettings generalSettings = channelConfig.getGeneralSettings();
			if(generalSettings.getBoolean(GeneralBooleanComponent.AutoJoin)) {
				connection.join(channelConfig.getName(), AuthManager.getChannelPassword(channelConfig.getIdentifier()));
			}
		}
		if(!SharedGlobalConfig.defaultChat.get().equals("Minecraft")) {
			IRCContext chatTarget = EiraIRCAPI.parseContext(null, SharedGlobalConfig.defaultChat.get(), IRCContext.ContextType.IRCChannel);
			if(chatTarget.getContextType() != IRCContext.ContextType.Error) {
				EiraIRC.instance.getChatSessionHandler().setChatTarget(chatTarget);
			}
		}
		IRCConnectEvent event = new IRCConnectEvent(connection, message);
		MinecraftForge.EVENT_BUS.post(event);
		switch (event.getResult()) {
			case DEFAULT:
				EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:general.connected", event.connection.getHost()));
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void fireConnectionFailedEvent(IRCConnection connection, Exception exception) {
		if(EiraIRC.instance.getConnectionManager().isLatestConnection(connection)) {
			for(IRCChannel channel : connection.getChannels()) {
				EiraIRC.instance.getChatSessionHandler().removeTargetChannel(channel);
			}
			EiraIRC.instance.getConnectionManager().removeConnection(connection);
		}
		IRCConnectionFailedEvent event = new IRCConnectionFailedEvent(connection, exception);
		MinecraftForge.EVENT_BUS.post(event);
		switch(event.getResult()) {
			case DEFAULT:
				EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:error.couldNotConnect", connection.getHost(), exception));
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void fireReconnectEvent(IRCConnection connection, int waitingTime) {
		IRCReconnectEvent event = new IRCReconnectEvent(connection, waitingTime);
		MinecraftForge.EVENT_BUS.post(event);
		switch(event.getResult()) {
			case DEFAULT:
				EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:general.reconnecting", connection.getHost(), waitingTime / 1000));
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void fireDisconnectEvent(IRCConnection connection) {
		if(EiraIRC.instance.getConnectionManager().isLatestConnection(connection)) {
			for(IRCChannel channel : connection.getChannels()) {
				EiraIRC.instance.getChatSessionHandler().removeTargetChannel(channel);
			}
			EiraIRC.instance.getConnectionManager().removeConnection(connection);
		}
		IRCDisconnectEvent event = new IRCDisconnectEvent(connection);
		switch(event.getResult()) {
			case DEFAULT:
				EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:general.disconnected", connection.getHost()));
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void fireIRCErrorEvent(IRCConnection connection, IRCMessage message, int numeric, String[] args) {
		switch(numeric) {
			case IRCReplyCodes.ERR_NICKNAMEINUSE:
			case IRCReplyCodes.ERR_NICKCOLLISION:
				String failNick = args[1];
				String tryNick = failNick + "_";
				Utils.addMessageToChat(new ChatComponentTranslation("eirairc:error.nickInUse", failNick, tryNick));
				connection.nick(tryNick);
				break;
			case IRCReplyCodes.ERR_ERRONEUSNICKNAME:
				Utils.addMessageToChat(new ChatComponentTranslation("eirairc:error.nickInvalid", args[1]));
				ServerConfig serverConfig = ConfigHelper.getServerConfig(connection);
				if(serverConfig.getNick() != null) {
					serverConfig.setNick(connection.getNick());
				}
				break;
		}
		IRCErrorEvent event = new IRCErrorEvent(connection, message, numeric, args);
		switch (event.getResult()) {
			case DEFAULT:
				switch (numeric) {
					case IRCReplyCodes.ERR_NONICKCHANGE:
						EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:error.noNickChange"));
						break;
					case IRCReplyCodes.ERR_SERVICESDOWN:
						EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:error.servicesDown"));
						break;
					case IRCReplyCodes.ERR_TARGETTOOFAST:
						EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:error.targetTooFast"));
						break;
					case IRCReplyCodes.ERR_CANNOTSENDTOCHAN:
					case IRCReplyCodes.ERR_TOOMANYCHANNELS:
					case IRCReplyCodes.ERR_TOOMANYTARGETS:
					case IRCReplyCodes.ERR_UNKNOWNERROR:
					case IRCReplyCodes.ERR_NOSUCHSERVER:
					case IRCReplyCodes.ERR_NOSUCHSERVICE:
					case IRCReplyCodes.ERR_NOTOPLEVEL:
					case IRCReplyCodes.ERR_WILDTOPLEVEL:
					case IRCReplyCodes.ERR_BADMASK:
					case IRCReplyCodes.ERR_UNKNOWNCOMMAND:
					case IRCReplyCodes.ERR_NOADMININFO:
					case IRCReplyCodes.ERR_NOTONCHANNEL:
					case IRCReplyCodes.ERR_WASNOSUCHNICK:
					case IRCReplyCodes.ERR_NOSUCHNICK:
					case IRCReplyCodes.ERR_NOSUCHCHANNEL:
					case IRCReplyCodes.ERR_NOLOGIN:
					case IRCReplyCodes.ERR_BANNEDFROMCHAN:
					case IRCReplyCodes.ERR_CHANOPRIVSNEEDED:
					case IRCReplyCodes.ERR_BADCHANMASK:
					case IRCReplyCodes.ERR_BADCHANNELKEY:
					case IRCReplyCodes.ERR_INVITEONLYCHAN:
					case IRCReplyCodes.ERR_UNKNOWNMODE:
					case IRCReplyCodes.ERR_CHANNELISFULL:
					case IRCReplyCodes.ERR_KEYSET:
					case IRCReplyCodes.ERR_NEEDMOREPARAMS:
						EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:error.genericTarget", args[1], args[2]));
						break;
					case IRCReplyCodes.ERR_NOORIGIN:
					case IRCReplyCodes.ERR_NORECIPIENT:
					case IRCReplyCodes.ERR_NOTEXTTOSEND:
					case IRCReplyCodes.ERR_NOMOTD:
					case IRCReplyCodes.ERR_FILEERROR:
					case IRCReplyCodes.ERR_NONICKNAMEGIVEN:
					case IRCReplyCodes.ERR_SUMMONDISABLED:
					case IRCReplyCodes.ERR_USERSDISABLED:
					case IRCReplyCodes.ERR_NOTREGISTERED:
					case IRCReplyCodes.ERR_PASSWDMISMATCH:
					case IRCReplyCodes.ERR_YOUREBANNEDCREEP:
					case IRCReplyCodes.ERR_USERSDONTMATCH:
					case IRCReplyCodes.ERR_UMODEUNKNOWNFLAG:
					case IRCReplyCodes.ERR_NOOPERHOST:
					case IRCReplyCodes.ERR_NOPRIVILEGES:
					case IRCReplyCodes.ERR_ALREADYREGISTERED:
					case IRCReplyCodes.ERR_NOPERMFORHOST:
					case IRCReplyCodes.ERR_CANTKILLSERVER:
						EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:error.generic", args[1]));
						break;
					default:
						System.out.println("Unhandled error code: " + numeric + " (" + args.length + " arguments)");
						break;
				}
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void fireChannelCTCPEvent(IRCConnection connection, IRCChannel channel, IRCUser sender, IRCMessage rawMessage, String message, boolean isNotice) {
		if(sender != null && IgnoreList.isIgnored(sender)) {
			logger.info("Ignored message by " + sender.getName() + ": " + message);
			return;
		}
		IRCChannelCTCPEvent event = new IRCChannelCTCPEvent(connection, channel, sender, rawMessage, message, isNotice);
		MinecraftForge.EVENT_BUS.post(event);
		switch (event.getResult()) {
			case DEFAULT:
				if (ConfigHelper.getGeneralSettings(event.channel).isMuted()) {
					return;
				}
				if (EiraIRC.proxy.checkClientBridge(event)) {
					return;
				}
				BotSettings botSettings = ConfigHelper.getBotSettings(event.channel);
				if (botSettings.getBoolean(BotBooleanComponent.FilterLinks)) {
					message = MessageFormat.filterLinks(message);
				}
				// append CTCP tag
				message = "[CTCP] " + message;
				String format;
				if (event.connection.isTwitch() && event.sender != null && event.sender.getName().equals("twitchnotify")) {
					format = "{MESSAGE}";
				} else if (event.isNotice) {
					format = botSettings.getMessageFormat().mcChannelNotice;
				} else {
					format = botSettings.getMessageFormat().mcChannelMessage;
				}
				IChatComponent chatComponent = MessageFormat.formatChatComponent(format, event.connection, event.channel, event.sender, message, MessageFormat.Target.Minecraft, MessageFormat.Mode.Message);
				if (event.isNotice && botSettings.getBoolean(BotBooleanComponent.HideNotices)) {
					System.out.println(chatComponent.getUnformattedText());
					return;
				}
				ThemeSettings theme = ConfigHelper.getTheme(event.channel);
				EnumChatFormatting noticeColor = theme.getColor(ThemeColorComponent.ircNoticeTextColor);
				if (event.isNotice && noticeColor != null) {
					chatComponent.getChatStyle().setColor(noticeColor);
				}
				EiraIRCAPI.getChatHandler().addChatMessage(chatComponent);
				break;
			case ALLOW:
				if(event.result != null) {
					EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				}
				break;
		}
	}

	public static void firePrivateCTCPEvent(IRCConnection connection, IRCUser sender, IRCMessage rawMessage, String message, boolean isNotice) {
		if(sender != null && IgnoreList.isIgnored(sender)) {
			logger.info("Ignored message by " + sender.getName() + ": " + message);
			return;
		}
		IRCPrivateCTCPEvent event = new IRCPrivateCTCPEvent(connection, sender, rawMessage, message, isNotice);
		MinecraftForge.EVENT_BUS.post(event);
		switch (event.getResult()) {
			case DEFAULT:
				BotSettings botSettings = ConfigHelper.getBotSettings(null);
				if (ConfigHelper.getGeneralSettings(event.sender).isMuted()) {
					return;
				}
				if (!botSettings.getBoolean(BotBooleanComponent.AllowPrivateMessages)) {
					if (!event.isNotice && event.sender != null) {
						event.sender.notice(I19n.format("eirairc:commands.ctcp.disabled"));
					}
					return;
				}
				if (botSettings.getBoolean(BotBooleanComponent.FilterLinks)) {
					message = MessageFormat.filterLinks(message);
				}
				// append CTCP tag
				message = "[CTCP] " + message;
				String format;
				if (event.connection.isTwitch() && event.sender != null && event.sender.getName().equals("twitchnotify")) {
					format = "{MESSAGE}";
				} else if (event.isNotice) {
					format = botSettings.getMessageFormat().mcPrivateNotice;
				} else {
					format = botSettings.getMessageFormat().mcPrivateMessage;
				}
				IChatComponent chatComponent = MessageFormat.formatChatComponent(format, event.connection, null, event.sender, message, MessageFormat.Target.Minecraft, MessageFormat.Mode.Message);
				if (event.isNotice && botSettings.getBoolean(BotBooleanComponent.HideNotices)) {
					System.out.println(chatComponent.getUnformattedText());
					return;
				}
				String notifyMsg = chatComponent.getUnformattedText();
				if (notifyMsg.length() > 42) {
					notifyMsg = notifyMsg.substring(0, 42) + "...";
				}
				if (!event.isNotice) {
					EiraIRC.proxy.publishNotification(NotificationType.PrivateMessage, notifyMsg);
				}
				EiraIRC.instance.getChatSessionHandler().addTargetUser(event.sender);
				ThemeSettings theme = ConfigHelper.getTheme(event.sender);
				EnumChatFormatting noticeColor = theme.getColor(ThemeColorComponent.ircNoticeTextColor);
				if (event.isNotice && noticeColor != null) {
					chatComponent.getChatStyle().setColor(noticeColor);
				}
				EiraIRCAPI.getChatHandler().addChatMessage(chatComponent);
				break;
			case ALLOW:
				EiraIRCAPI.getChatHandler().addChatMessage(event.result);
				break;
		}
	}

	public static void fireConnectingEvent(IRCConnection connection) {
		EiraIRC.instance.getConnectionManager().addConnection(connection);
	}

	public static void fireChannelJoinedEvent(IRCConnection connection, IRCMessage message, IRCChannel channel) {
		EiraIRC.instance.getChatSessionHandler().addTargetChannel(channel);
		if(ConfigHelper.getGeneralSettings(channel).getBoolean(GeneralBooleanComponent.AutoWho)) {
			Utils.sendUserList(null, connection, channel);
		}
		if(Compatibility.isEiraMoticonsInstalled() && SharedGlobalConfig.twitchNameBadges.get() && channel.getConnection().isTwitch()) {
			// Pre-load this channels sub badge
			EiraMoticonsAddon.getSubscriberBadge(channel);
		}
	}

	public static void fireChannelLeftEvent(IRCConnection connection, IRCChannel channel) {
		EiraIRC.instance.getChatSessionHandler().removeTargetChannel(channel);
	}
}
