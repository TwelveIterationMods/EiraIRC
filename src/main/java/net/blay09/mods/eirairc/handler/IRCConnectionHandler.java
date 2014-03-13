// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.irc.IIRCConnectionHandler;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.irc.IRCReplyCodes;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;

public class IRCConnectionHandler implements IIRCConnectionHandler {

	@Override
	public void onConnecting(IRCConnection connection) {
		EiraIRC.instance.addConnection(connection);
	}
	
	@Override
	public void onConnected(IRCConnection connection) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.connected", connection.getHost());
		Utils.addMessageToChat(mcMessage);
		ServerConfig serverConfig = Utils.getServerConfig(connection);
		if(serverConfig.getHost().equals(Globals.TWITCH_SERVER) && serverConfig.getNick() != null) {
			ChannelConfig twitchChannel = serverConfig.getChannelConfig("#" + serverConfig.getNick());
			twitchChannel.setAutoJoin(true);
		}
		Utils.doNickServ(connection, serverConfig);
		for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
			if(channelConfig.isAutoJoin()) {
				connection.join(channelConfig.getName(), channelConfig.getPassword());
			}
		}
	}

	@Override
	public void onDisconnected(IRCConnection connection) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.disconnected", connection.getHost());
		Utils.addMessageToChat(mcMessage);
		for(IRCChannel channel : connection.getChannels()) {
			EiraIRC.instance.getChatSessionHandler().removeTargetChannel(channel);
		}
		EiraIRC.instance.removeConnection(connection);
	}
	
	@Override
	public void onIRCError(IRCConnection connection, int errorCode, String line, String[] cmd) {
		switch(errorCode) {
		case IRCReplyCodes.ERR_NICKNAMEINUSE:
			String failNick = cmd[3];
			String tryNick = failNick + "_";
			Utils.addMessageToChat(Utils.getLocalizedMessage("irc.bot.nickInUse", failNick, tryNick));
			connection.nick(tryNick);
			break;
		case IRCReplyCodes.ERR_ERRONEUSNICKNAME:
			Utils.addMessageToChat(Utils.getLocalizedMessage("irc.bot.nickInvalid", cmd[3]));
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			if(serverConfig.getNick() != null) {
				serverConfig.setNick(connection.getNick());
			} else {
				GlobalConfig.nick = connection.getNick();
			}
			break;
		default:
			System.out.println("Unhandled error code: " + errorCode + " (" + line + ")");
			break;
		}
	}

	@Override
	public void onChannelJoined(IRCConnection connection, IRCChannel channel) {
		EiraIRC.instance.getChatSessionHandler().addTargetChannel(channel);
		if(connection.getChannels().size() > 1) {
			DisplayConfig.originalDisplayMode = DisplayConfig.displayMode;
			DisplayConfig.displayMode = "Classic";
		}
		ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
		ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
		EiraIRC.proxy.onChannelJoined(channel);
	}

	@Override
	public void onChannelLeft(IRCConnection connection, IRCChannel channel) {
		EiraIRC.instance.getChatSessionHandler().removeTargetChannel(channel);
		if(connection.getChannels().size() <= 1 && DisplayConfig.originalDisplayMode != null) {
			DisplayConfig.displayMode = DisplayConfig.originalDisplayMode;
			DisplayConfig.originalDisplayMode = null;
		}
	}
}
