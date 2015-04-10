// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.api.event.*;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.BotBooleanComponent;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.ThemeColorComponent;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.irc.IRCUserImpl;
import net.blay09.mods.eirairc.util.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;

public class IRCEventHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onNickChange(IRCUserNickChangeEvent event) {
		if(ConfigHelper.getGeneralSettings(event.user).isMuted()) {
			return;
		}
		if(SharedGlobalConfig.botSettings.getBoolean(BotBooleanComponent.RelayNickChanges)) {
			String format = ConfigHelper.getBotSettings(event.user).getMessageFormat().mcUserNickChange;
			format = format.replace("{OLDNICK}", event.oldNick);
			Utils.addMessageToChat(MessageFormat.formatChatComponent(format, event.connection, null, event.user, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUserJoin(IRCUserJoinEvent event) {
		if(ConfigHelper.getGeneralSettings(event.channel).isMuted()) {
			return;
		}
		BotSettings botSettings = ConfigHelper.getBotSettings(event.channel);
		if(botSettings.getBoolean(BotBooleanComponent.RelayIRCJoinLeave)) {
			String format = ConfigHelper.getBotSettings(event.channel).getMessageFormat().mcUserJoin;
			Utils.addMessageToChat(MessageFormat.formatChatComponent(format, event.connection, event.channel, event.user, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
		}
		if(botSettings.getBoolean(BotBooleanComponent.SendAutoWho)) {
			Utils.sendPlayerList(event.user);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUserLeave(IRCUserLeaveEvent event) {
		if(ConfigHelper.getGeneralSettings(event.channel).isMuted()) {
			return;
		}
		if(ConfigHelper.getBotSettings(event.channel).getBoolean(BotBooleanComponent.RelayIRCJoinLeave)) {
			String format = ConfigHelper.getBotSettings(event.channel).getMessageFormat().mcUserLeave;
			Utils.addMessageToChat(MessageFormat.formatChatComponent(format, event.connection, event.channel, event.user, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUserQuit(IRCUserQuitEvent event) {
		if(ConfigHelper.getGeneralSettings(event.user).isMuted()) {
			return;
		}
		if(SharedGlobalConfig.botSettings.getBoolean(BotBooleanComponent.RelayIRCJoinLeave)) {
			String format = ConfigHelper.getBotSettings(event.user).getMessageFormat().mcUserQuit;
			Utils.addMessageToChat(MessageFormat.formatChatComponent(format, event.connection, null, event.user, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPrivateChat(IRCPrivateChatEvent event) {
		if(!event.isNotice) {
			if(((IRCBotImpl) event.bot).processCommand(null, event.sender, event.message)) {
				return;
			} else {
				if(event.bot.isServerSide()) {
					event.sender.notice(Utils.getLocalizedMessage("irc.bot.unknownCommand"));
					return;
				}
			}
		}
		if(event.sender != null && event.sender.getName().equals("tmi.twitch.tv") && event.isNotice && event.connection.getHost().equals(Globals.TWITCH_SERVER) && event.message.equals("Login unsuccessful")) {
			event.connection.disconnect("");
			MinecraftForge.EVENT_BUS.post(new IRCConnectionFailedEvent(event.connection, new RuntimeException("Wrong username or invalid oauth token.")));
			return;
		}
		// Parse Twitch user colors if this is a message from jtv on irc.twitch.tv
		if(event.sender != null && event.sender.getName().equals("jtv") && event.connection.getHost().equals(Globals.TWITCH_SERVER)) {
			if(event.message.startsWith("USERCOLOR ")) {
				int lastSpace = event.message.lastIndexOf(' ');
				String targetNick = event.message.substring(10, lastSpace);
				String targetColor = event.message.substring(lastSpace + 1);
				IRCUserImpl user = (IRCUserImpl) event.connection.getOrCreateUser(targetNick);
				user.setNameColor(IRCFormatting.getColorFromTwitch(targetColor));
			}
			return;
		}
		BotSettings botSettings = ConfigHelper.getBotSettings(null);
		if(ConfigHelper.getGeneralSettings(event.sender).isMuted()) {
			return;
		}
		if(!botSettings.getBoolean(BotBooleanComponent.AllowPrivateMessages)) {
			if(!event.isNotice && event.sender != null) {
				event.sender.notice(Utils.getLocalizedMessage("irc.msg.disabled"));
			}
			return;
		}
		String message = event.message;
		if(botSettings.getBoolean(BotBooleanComponent.FilterLinks)) {
			message = MessageFormat.filterLinks(message);
		}
		String format;
		if(event.isNotice) {
			format = botSettings.getMessageFormat().mcPrivateNotice;
		} else if(event.isEmote) {
			format = botSettings.getMessageFormat().mcPrivateEmote;
		} else {
			format = botSettings.getMessageFormat().mcPrivateMessage;
		}
		IChatComponent chatComponent = MessageFormat.formatChatComponent(format, event.connection, null, event.sender, message, MessageFormat.Target.Minecraft, (event.isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message));
		if(event.isNotice && botSettings.getBoolean(BotBooleanComponent.HideNotices)) {
			System.out.println(chatComponent.getUnformattedText());
			return;
		}
		String notifyMsg = chatComponent.getUnformattedText();
		if(notifyMsg.length() > 42) {
			notifyMsg = notifyMsg.substring(0, 42) + "...";
		}
		if(!event.isNotice) {
			EiraIRC.proxy.publishNotification(NotificationType.PrivateMessage, notifyMsg);
		}
		EiraIRC.instance.getChatSessionHandler().addTargetUser(event.sender);
		ThemeSettings theme = ConfigHelper.getTheme(event.sender);
		EnumChatFormatting emoteColor = theme.getColor(ThemeColorComponent.emoteTextColor);
		EnumChatFormatting noticeColor = theme.getColor(ThemeColorComponent.ircNoticeTextColor);
		if(event.isEmote && emoteColor != null) {
			chatComponent.getChatStyle().setColor(emoteColor);
		} else if(event.isNotice && noticeColor != null) {
			chatComponent.getChatStyle().setColor(noticeColor);
		}
		Utils.addMessageToChat(chatComponent);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChannelChat(IRCChannelChatEvent event) {
		if(!event.isNotice && event.message.startsWith("!") && ((IRCBotImpl) event.bot).processCommand(event.channel, event.sender, event.message.substring(1))) {
			return;
		}
		if(ConfigHelper.getGeneralSettings(event.channel).isMuted()) {
			return;
		}
		if(EiraIRC.proxy.checkClientBridge(event)) {
			return;
		}
		String message = event.message;
		BotSettings botSettings = ConfigHelper.getBotSettings(event.channel);
		if(botSettings.getBoolean(BotBooleanComponent.FilterLinks)) {
			message = MessageFormat.filterLinks(message);
		}
		String format;
		if(event.connection.getHost().equals(Globals.TWITCH_SERVER) && event.sender != null && event.sender.getName().equals("twitchnotify")) {
			format = "{MESSAGE}";
		} else if(event.isNotice) {
			format = botSettings.getMessageFormat().mcChannelNotice;
		} else if(event.isEmote) {
			format = botSettings.getMessageFormat().mcChannelEmote;
		} else {
			format = botSettings.getMessageFormat().mcChannelMessage;
		}
		IChatComponent chatComponent = MessageFormat.formatChatComponent(format, event.connection, event.channel, event.sender, message, MessageFormat.Target.Minecraft, event.isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message);
		if(event.isNotice && botSettings.getBoolean(BotBooleanComponent.HideNotices)) {
			System.out.println(chatComponent.getUnformattedText());
			return;
		}
		ThemeSettings theme = ConfigHelper.getTheme(event.channel);
		EnumChatFormatting emoteColor = theme.getColor(ThemeColorComponent.emoteTextColor);
		EnumChatFormatting noticeColor = theme.getColor(ThemeColorComponent.ircNoticeTextColor);
		if(event.isEmote && emoteColor != null) {
			chatComponent.getChatStyle().setColor(emoteColor);
		} else if(event.isNotice && noticeColor != null) {
			chatComponent.getChatStyle().setColor(noticeColor);
		}
		Utils.addMessageToChat(chatComponent);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onTopicChange(IRCChannelTopicEvent event) {
		if(ConfigHelper.getGeneralSettings(event.channel).isMuted()) {
			return;
		}
		if(event.user == null) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.topic", event.channel.getName(), event.channel.getTopic());
			Utils.addMessageToChat(mcMessage);
		} else {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.topicChange", event.user.getName(), event.channel.getName(), event.channel.getTopic());
			Utils.addMessageToChat(mcMessage);
		}
	}

}
