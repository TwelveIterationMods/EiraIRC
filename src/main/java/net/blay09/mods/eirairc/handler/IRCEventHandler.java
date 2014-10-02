// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.bot.BotProfile;
import net.blay09.mods.eirairc.api.event.IRCChannelChatEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelTopicEvent;
import net.blay09.mods.eirairc.api.event.IRCPrivateChatEvent;
import net.blay09.mods.eirairc.api.event.IRCUserJoinEvent;
import net.blay09.mods.eirairc.api.event.IRCUserLeaveEvent;
import net.blay09.mods.eirairc.api.event.IRCUserNickChangeEvent;
import net.blay09.mods.eirairc.api.event.IRCUserQuitEvent;
import net.blay09.mods.eirairc.config2.ClientGlobalConfig;
import net.blay09.mods.eirairc.config2.TempPlaceholder;
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
		if(event.bot.isMuted(null)) {
			return;
		}
		if(event.bot.getBoolean(null, BotProfile.KEY_RELAYNICKCHANGES, true)) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.nickChange", event.connection.getHost(), event.oldNick, event.newNick);
			Utils.addMessageToChat(mcMessage);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUserJoin(IRCUserJoinEvent event) {
		if(event.bot.isMuted(event.channel)) {
			return;
		}
		if(event.bot.getBoolean(event.channel, BotProfile.KEY_RELAYIRCJOINLEAVE, true)) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.joinMsg", event.channel.getName(), event.user.getName());
			Utils.addMessageToChat(mcMessage);
		}
		if(event.bot.getBoolean(event.channel, BotProfile.KEY_AUTOPLAYERS, false)) {
			Utils.sendPlayerList(event.user);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUserLeave(IRCUserLeaveEvent event) {
		if(event.bot.isMuted(event.channel)) {
			return;
		}
		if(event.bot.getBoolean(event.channel, BotProfile.KEY_RELAYIRCJOINLEAVE, true)) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.partMsg", event.channel.getName(), event.user.getName());
			Utils.addMessageToChat(mcMessage);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUserQuit(IRCUserQuitEvent event) {
		if(event.bot.isMuted(null)) {
			return;
		}
		if(event.bot.getBoolean(null, BotProfile.KEY_RELAYIRCJOINLEAVE, true)) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.quitMsg", event.connection.getHost(), event.user.getName(), event.message);
			Utils.addMessageToChat(mcMessage);
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
		if(event.bot.isMuted(event.sender)) {
			return;
		}
		if(!event.bot.getBoolean(event.sender, "allowPrivateMessages", true)) {
			if(!event.isNotice) {
				event.sender.notice(Utils.getLocalizedMessage("irc.msg.disabled"));
			}
			return;
		}
		String message = event.message;
		if(event.bot.getBoolean(event.sender, BotProfile.KEY_LINKFILTER, false)) {
			message = MessageFormat.filterLinks(message);
		}
		String format;
		if(event.isNotice) {
			format = ConfigHelper.getDisplayFormat(event.bot.getDisplayFormat(event.sender)).mcPrivateNotice;
		} else if(event.isEmote) {
			format = ConfigHelper.getDisplayFormat(event.bot.getDisplayFormat(event.sender)).mcPrivateEmote;
		} else {
			format = ConfigHelper.getDisplayFormat(event.bot.getDisplayFormat(event.sender)).mcPrivateMessage;
		}
		IChatComponent chatComponent = MessageFormat.formatChatComponent(format, event.connection, null, event.sender, message, MessageFormat.Target.Minecraft, (event.isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message));
		if(event.isNotice && TempPlaceholder.hideNotices) {
			System.out.println(chatComponent.getUnformattedText());
			return;
		}
		String notifyMsg = chatComponent.getUnformattedText();
		if(notifyMsg.length() > 42) {
			notifyMsg = notifyMsg.substring(0, 42) + "...";
		}
		EiraIRC.proxy.publishNotification(NotificationType.PrivateMessage, notifyMsg);
		EiraIRC.instance.getChatSessionHandler().addTargetUser(event.sender);
		EnumChatFormatting emoteColor = Utils.getColorFormatting(ConfigHelper.getEmoteColor(event.sender));
		EnumChatFormatting noticeColor = Utils.getColorFormatting(ConfigHelper.getNoticeColor(event.sender));
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
		if(event.bot.isMuted(event.channel)) {
			return;
		}
		String message = event.message;
		if(ClientGlobalConfig.clientBridge) {
			if(!ClientGlobalConfig.clientBridgeMessageToken.isEmpty()) {
				if (message.endsWith(ClientGlobalConfig.clientBridgeMessageToken) || message.endsWith(ClientGlobalConfig.clientBridgeMessageToken + IRCConnectionImpl.EMOTE_END)) {
					return;
				}
			}
			if(!ClientGlobalConfig.clientBridgeNickToken.isEmpty()) {
				if (event.sender.getName().endsWith(ClientGlobalConfig.clientBridgeNickToken)) {
					return;
				}
			}
		}
		if(event.bot.getBoolean(event.sender, BotProfile.KEY_LINKFILTER, false)) {
			message = MessageFormat.filterLinks(message);
		}
		String format;
		if(event.isNotice) {
			format = ConfigHelper.getDisplayFormat(event.bot.getDisplayFormat(event.channel)).mcChannelNotice;
		} else if(event.isEmote) {
			format = ConfigHelper.getDisplayFormat(event.bot.getDisplayFormat(event.channel)).mcChannelEmote;
		} else {
			format = ConfigHelper.getDisplayFormat(event.bot.getDisplayFormat(event.channel)).mcChannelMessage;
		}
		IChatComponent chatComponent = MessageFormat.formatChatComponent(format, event.connection, event.channel, event.sender, message, MessageFormat.Target.Minecraft, event.isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message);
		if(event.isNotice && TempPlaceholder.hideNotices) {
			System.out.println(chatComponent.getUnformattedText());
			return;
		}
		EnumChatFormatting emoteColor = Utils.getColorFormatting(ConfigHelper.getEmoteColor(event.channel));
		EnumChatFormatting noticeColor = Utils.getColorFormatting(ConfigHelper.getNoticeColor(event.channel));
		if(event.isEmote && emoteColor != null) {
			chatComponent.getChatStyle().setColor(emoteColor);
		} else if(event.isNotice && noticeColor != null) {
			chatComponent.getChatStyle().setColor(noticeColor);
		}
		Utils.addMessageToChat(chatComponent);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onTopicChange(IRCChannelTopicEvent event) {
		if(event.bot.isMuted(event.channel)) {
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
