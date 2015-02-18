// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.event.IRCChannelChatEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelTopicEvent;
import net.blay09.mods.eirairc.api.event.IRCPrivateChatEvent;
import net.blay09.mods.eirairc.api.event.IRCUserJoinEvent;
import net.blay09.mods.eirairc.api.event.IRCUserLeaveEvent;
import net.blay09.mods.eirairc.api.event.IRCUserNickChangeEvent;
import net.blay09.mods.eirairc.api.event.IRCUserQuitEvent;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.*;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
			if(event.bot.processCommand(null, event.sender, event.message)) {
				return;
			} else {
				if(event.bot.isServerSide()) {
					event.sender.notice(Utils.getLocalizedMessage("irc.bot.unknownCommand"));
					return;
				}
			}
		}
		BotSettings botSettings = ConfigHelper.getBotSettings(null);
		if(ConfigHelper.getGeneralSettings(event.sender).isMuted()) {
			return;
		}
		if(!botSettings.getBoolean(BotBooleanComponent.AllowPrivateMessages)) {
			if(!event.isNotice) {
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
		EiraIRC.proxy.publishNotification(NotificationType.PrivateMessage, notifyMsg);
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
		if(!event.isNotice && event.message.startsWith("!") && event.bot.processCommand(event.channel, event.sender, event.message.substring(1))) {
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
		if(event.isNotice) {
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
