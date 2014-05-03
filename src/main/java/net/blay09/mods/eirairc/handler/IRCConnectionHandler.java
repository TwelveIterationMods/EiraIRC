// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.event.IRCChannelJoinedEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelLeftEvent;
import net.blay09.mods.eirairc.api.event.IRCConnectEvent;
import net.blay09.mods.eirairc.api.event.IRCConnectingEvent;
import net.blay09.mods.eirairc.api.event.IRCDisconnectEvent;
import net.blay09.mods.eirairc.api.event.IRCErrorEvent;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.irc.IRCReplyCodes;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class IRCConnectionHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onConnecting(IRCConnectingEvent event) {
		EiraIRC.instance.addConnection(event.connection);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onConnected(IRCConnectEvent event) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.connected", event.connection.getHost());
		Utils.addMessageToChat(mcMessage);
		ServerConfig serverConfig = ConfigHelper.getServerConfig(event.connection);
		if(serverConfig.getHost().equals(Globals.TWITCH_SERVER) && serverConfig.getNick() != null) {
			ChannelConfig twitchChannel = serverConfig.getChannelConfig("#" + serverConfig.getNick());
			twitchChannel.setAutoJoin(true);
		}
		Utils.doNickServ(event.connection, serverConfig);
		for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
			if(channelConfig.isAutoJoin()) {
				event.connection.join(channelConfig.getName(), channelConfig.getPassword());
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDisconnected(IRCDisconnectEvent event) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.disconnected", event.connection.getHost());
		Utils.addMessageToChat(mcMessage);
		for(IIRCChannel channel : event.connection.getChannels()) {
			EiraIRC.instance.getChatSessionHandler().removeTargetChannel(channel);
		}
		EiraIRC.instance.removeConnection(event.connection);
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
			} else {
				GlobalConfig.nick = event.connection.getNick();
			}
			break;
		default:
			System.out.println("Unhandled error code: " + event.numeric + " (" + event.args.length + " arguments)");
			break;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChannelJoined(IRCChannelJoinedEvent event) {
		EiraIRC.instance.getChatSessionHandler().addTargetChannel(event.channel);
		if(event.bot.getBoolean(event.channel, "autoWho", false)) {
			Utils.sendUserList(null, event.connection, event.channel);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChannelLeft(IRCChannelLeftEvent event) {
		EiraIRC.instance.getChatSessionHandler().removeTargetChannel(event.channel);
	}

}
