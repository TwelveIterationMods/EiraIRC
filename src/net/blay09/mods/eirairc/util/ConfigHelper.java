// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.DisplayFormatConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;

public class ConfigHelper {

	public static String getEmoteColor(IRCContext context) {
		if(context instanceof IRCChannel) {
			ChannelConfig channelConfig = getChannelConfig((IRCChannel) context);
			if(channelConfig != null) {
				ServerConfig serverConfig = channelConfig.getServerConfig();
				if(!serverConfig.getEmoteColor().isEmpty()) {
					return serverConfig.getEmoteColor();
				}
			}
		}
		return DisplayConfig.emoteColor;
	}
	
	public static String getNoticeColor(IRCContext context) {
		return DisplayConfig.ircNoticeColor;
	}
	
	public static ChannelConfig getChannelConfig(IRCChannel channel) {
		return getServerConfig(channel.getConnection()).getChannelConfig(channel);
	}
	
	public static String getIRCColor(IRCConnection connection) {
		return getIRCColor(ConfigurationHandler.getServerConfig(connection.getHost()));
	}
	
	public static String getIRCColor(ChannelConfig channelConfig) {
		return getIRCColor(channelConfig.getServerConfig());
	}
	
	public static String getIRCColor(ServerConfig serverConfig) {
		if(serverConfig.getIRCColor() != null && !serverConfig.getIRCColor().isEmpty()) {
			return serverConfig.getIRCColor();
		}
		return DisplayConfig.ircColor;
	}
	
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

	public static String getQuitMessage(IRCConnection connection) {
		ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
		if(serverConfig.getQuitMessage() != null && !serverConfig.getQuitMessage().isEmpty()) {
			return serverConfig.getQuitMessage();
		}
		return DisplayConfig.quitMessage;
	}

	public static ServerConfig getServerConfig(IRCConnection connection) {
		return ConfigurationHandler.getServerConfig(connection.getHost());
	}

	public static DisplayFormatConfig getDisplayFormat(String displayFormat) {
		return ConfigurationHandler.getDisplayFormat(displayFormat);
	}

}
