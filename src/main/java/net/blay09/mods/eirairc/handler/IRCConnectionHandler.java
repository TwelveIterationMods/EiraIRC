// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.event.*;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.BotStringComponent;
import net.blay09.mods.eirairc.config.settings.GeneralBooleanComponent;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.irc.IRCReplyCodes;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
public class IRCConnectionHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onConnecting(IRCConnectingEvent event) {
		EiraIRC.instance.getConnectionManager().addConnection(event.connection);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onConnected(IRCConnectEvent event) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.connected", event.connection.getHost());
		Utils.addMessageToChat(mcMessage);
		ServerConfig serverConfig = ConfigHelper.getServerConfig(event.connection);
		if(serverConfig.getAddress().equals(Globals.TWITCH_SERVER) && serverConfig.getNick() != null) {
			serverConfig.getOrCreateChannelConfig("#" + serverConfig.getNick());
			serverConfig.getBotSettings().setString(BotStringComponent.MessageFormat, "Twitch");
		}
		Utils.doNickServ(event.connection, serverConfig);
		for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
			GeneralSettings generalSettings = channelConfig.getGeneralSettings();
			if(generalSettings.getBoolean(GeneralBooleanComponent.AutoJoin)) {
				event.connection.join(channelConfig.getName(), channelConfig.getPassword());
			}
		}
		if(!SharedGlobalConfig.defaultChat.equals("Minecraft")) {
			IRCContext chatTarget = IRCResolver.resolveTarget(SharedGlobalConfig.defaultChat, IRCResolver.FLAG_CHANNEL);
			if(chatTarget != null) {
				EiraIRC.instance.getChatSessionHandler().setChatTarget(chatTarget);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onConnectionFailed(IRCConnectionFailedEvent event) {
		String mcMessage = Utils.getLocalizedMessage("error.couldNotConnect", event.connection.getHost(), event.exception);
		Utils.addMessageToChat(mcMessage);
		if(EiraIRC.instance.getConnectionManager().isLatestConnection(event.connection)) {
			for(IRCChannel channel : event.connection.getChannels()) {
				EiraIRC.instance.getChatSessionHandler().removeTargetChannel(channel);
			}
			EiraIRC.instance.getConnectionManager().removeConnection(event.connection);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDisconnected(IRCDisconnectEvent event) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.disconnected", event.connection.getHost());
		Utils.addMessageToChat(mcMessage);
		if(EiraIRC.instance.getConnectionManager().isLatestConnection(event.connection)) {
			for(IRCChannel channel : event.connection.getChannels()) {
				EiraIRC.instance.getChatSessionHandler().removeTargetChannel(channel);
			}
			EiraIRC.instance.getConnectionManager().removeConnection(event.connection);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onIRCError(IRCErrorEvent event) {
		switch(event.numeric) {
			case IRCReplyCodes.ERR_NICKNAMEINUSE:
			case IRCReplyCodes.ERR_NICKCOLLISION:
				String failNick = event.args[1];
				String tryNick = failNick + "_";
				Utils.addMessageToChat(Utils.getLocalizedChatMessage("error.nickInUse", failNick, tryNick));
				event.connection.nick(tryNick);
				break;
			case IRCReplyCodes.ERR_ERRONEUSNICKNAME:
				Utils.addMessageToChat(Utils.getLocalizedChatMessage("error.nickInvalid", event.args[1]));
				ServerConfig serverConfig = ConfigHelper.getServerConfig(event.connection);
				if(serverConfig.getNick() != null) {
					serverConfig.setNick(event.connection.getNick());
				}
				break;
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

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChannelJoined(IRCChannelJoinedEvent event) {
		EiraIRC.instance.getChatSessionHandler().addTargetChannel(event.channel);
		if(ConfigHelper.getGeneralSettings(event.channel).getBoolean(GeneralBooleanComponent.AutoWho)) {
			Utils.sendUserList(null, event.connection, event.channel);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChannelLeft(IRCChannelLeftEvent event) {
		EiraIRC.instance.getChatSessionHandler().removeTargetChannel(event.channel);
	}

}
