// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCReplyCodes;
import net.blay09.mods.eirairc.api.event.*;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
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
			Utils.addMessageToChat(MessageFormat.formatChatComponent(format, event.connection, null, event.user, event.message, MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPrivateChat(IRCPrivateChatEvent event) {
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
		if(event.connection.getHost().equals(Globals.TWITCH_SERVER) && event.sender != null && event.sender.getName().equals("twitchnotify")) {
			format = "{MESSAGE}";
		} else if(event.isNotice) {
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
		EnumChatFormatting twitchNameColor = (event.sender != null && SharedGlobalConfig.twitchNameColors) ? ((IRCUserImpl) event.sender).getNameColor() : null;
		EnumChatFormatting noticeColor = theme.getColor(ThemeColorComponent.ircNoticeTextColor);
		if(event.isEmote && (emoteColor != null || twitchNameColor != null)) {
			chatComponent.getChatStyle().setColor(twitchNameColor != null ? twitchNameColor : emoteColor);
		} else if(event.isNotice && noticeColor != null) {
			chatComponent.getChatStyle().setColor(noticeColor);
		}
		Utils.addMessageToChat(chatComponent);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChannelChat(IRCChannelChatEvent event) {
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
		EnumChatFormatting twitchNameColor = (event.sender != null && SharedGlobalConfig.twitchNameColors) ? ((IRCUserImpl) event.sender).getNameColor() : null;
		EnumChatFormatting noticeColor = theme.getColor(ThemeColorComponent.ircNoticeTextColor);
		if(event.isEmote && (emoteColor != null || twitchNameColor != null)) {
			chatComponent.getChatStyle().setColor(twitchNameColor != null ? twitchNameColor : emoteColor);
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

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onConnected(IRCConnectEvent event) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.connected", event.connection.getHost());
		Utils.addMessageToChat(mcMessage);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onConnectionFailed(IRCConnectionFailedEvent event) {
		String mcMessage = Utils.getLocalizedMessage("error.couldNotConnect", event.connection.getHost(), event.exception);
		Utils.addMessageToChat(mcMessage);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onConnectionFailed(IRCReconnectEvent event) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.reconnecting", event.connection.getHost(), event.waitingTime / 1000);
		Utils.addMessageToChat(mcMessage);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDisconnected(IRCDisconnectEvent event) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.disconnected", event.connection.getHost());
		Utils.addMessageToChat(mcMessage);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onIRCError(IRCErrorEvent event) {
		switch(event.numeric) {
			case IRCReplyCodes.ERR_NONICKCHANGE:
				Utils.addMessageToChat(Utils.getLocalizedChatMessage("error.noNickChange")); break;
			case IRCReplyCodes.ERR_SERVICESDOWN:
				Utils.addMessageToChat(Utils.getLocalizedChatMessage("error.servicesDown")); break;
			case IRCReplyCodes.ERR_TARGETTOOFAST:
				Utils.addMessageToChat(Utils.getLocalizedChatMessage("error.targetTooFast")); break;
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
				Utils.addMessageToChat(Utils.getLocalizedChatMessage("error.genericTarget", event.args[1], event.args[2])); break;
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
				Utils.addMessageToChat(Utils.getLocalizedChatMessage("error.generic", event.args[1])); break;
			default:
				System.out.println("Unhandled error code: " + event.numeric + " (" + event.args.length + " arguments)");
				break;
		}
	}

}
