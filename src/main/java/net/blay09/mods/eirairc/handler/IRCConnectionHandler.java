// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.event.IRCChannelJoinedEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelLeftEvent;
import net.blay09.mods.eirairc.api.event.IRCConnectEvent;
import net.blay09.mods.eirairc.api.event.IRCConnectingEvent;
import net.blay09.mods.eirairc.api.event.IRCDisconnectEvent;
import net.blay09.mods.eirairc.api.event.IRCErrorEvent;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.BotStringComponent;
import net.blay09.mods.eirairc.config.settings.GeneralBooleanComponent;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.irc.IRCReplyCodes;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
	public void onDisconnected(IRCDisconnectEvent event) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.disconnected", event.connection.getHost());
		Utils.addMessageToChat(mcMessage);
		for(IRCChannel channel : event.connection.getChannels()) {
			EiraIRC.instance.getChatSessionHandler().removeTargetChannel(channel);
		}
		EiraIRC.instance.getConnectionManager().removeConnection(event.connection);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onIRCError(IRCErrorEvent event) {
		switch(event.numeric) {
		case IRCReplyCodes.ERR_NICKNAMEINUSE:
			String failNick = event.args[1];
			String tryNick = failNick + "_";
			Utils.addMessageToChat(Utils.getLocalizedMessage("irc.bot.nickInUse", failNick, tryNick));
			event.connection.nick(tryNick);
			break;
		case IRCReplyCodes.ERR_ERRONEUSNICKNAME:
			Utils.addMessageToChat(Utils.getLocalizedMessage("irc.bot.nickInvalid", event.args[1]));
			ServerConfig serverConfig = ConfigHelper.getServerConfig(event.connection);
			if(serverConfig.getNick() != null) {
				serverConfig.setNick(event.connection.getNick());
			}
			break;
		case IRCReplyCodes.ERR_PASSWDMISMATCH:
			Utils.addMessageToChat(event.args[0]);
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
