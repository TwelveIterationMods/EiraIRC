// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.DisplayFormatConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;

public class ConfigHelper {
	
	public static String formatNick(String nickFormate) {
		String result = nickFormate.replace("%USERNAME%", Utils.getUsername());
		return result;
	}
	
	public static String getNick(ServerConfig serverConfig) {
		if(serverConfig.getNick() != null && !serverConfig.getNick().isEmpty()) {
			return serverConfig.getNick();
		}
		return GlobalConfig.nick;
	}

	public static String getFormattedNick(ServerConfig serverConfig) {
		return formatNick(getNick(serverConfig));
	}

	public static String getQuitMessage(IIRCConnection connection) {
		ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
		if(serverConfig.getQuitMessage() != null && !serverConfig.getQuitMessage().isEmpty()) {
			return serverConfig.getQuitMessage();
		}
		return DisplayConfig.quitMessage;
	}

	public static ServerConfig getServerConfig(IIRCConnection connection) {
		return ConfigurationHandler.getServerConfig(connection.getHost());
	}
	
	public static ChannelConfig getChannelConfig(IIRCChannel channel) {
		return getServerConfig(channel.getConnection()).getChannelConfig(channel);
	}

	public static DisplayFormatConfig getDisplayFormat(String displayFormat) {
		return ConfigurationHandler.getDisplayFormat(displayFormat);
	}

	public static String getEmoteColor(IIRCContext context) {
		if(context instanceof IIRCChannel) {
			ChannelConfig channelConfig = getChannelConfig((IIRCChannel) context);
			if(channelConfig != null) {
				ServerConfig serverConfig = channelConfig.getServerConfig();
				if(!serverConfig.getEmoteColor().isEmpty()) {
					return serverConfig.getEmoteColor();
				}
			}
		}
		return DisplayConfig.emoteColor;
	}

	public static String getNoticeColor(IIRCContext context) {
		return DisplayConfig.ircNoticeColor;
	}

}
