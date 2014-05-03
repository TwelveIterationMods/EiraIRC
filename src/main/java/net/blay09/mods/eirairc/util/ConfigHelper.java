// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.api.base.IIRCChannel;
import net.blay09.mods.eirairc.api.base.IIRCConnection;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.DisplayFormatConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.minecraftforge.common.config.ConfigCategory;

public class ConfigHelper {

	public static DisplayFormatConfig getDisplayFormatConfig() {
		DisplayFormatConfig dfc = DisplayConfig.displayFormates.get(DisplayConfig.displayMode);
		if(dfc == null) {
			DisplayConfig.displayMode = "S-Light";
			dfc = DisplayConfig.displayFormates.get(DisplayConfig.displayMode);
			if(dfc == null) {
				return new DisplayFormatConfig(new ConfigCategory("unknown"));
			}
		}
		return dfc;
	}
	
	public static String getEmoteColor(IIRCChannel channel) {
		return null;
	}
	
	public static String getEmoteColor(ChannelConfig channelConfig) {
		return getEmoteColor(channelConfig.getServerConfig());
	}
	
	public static String getEmoteColor(ServerConfig serverConfig) {
		if(serverConfig.getEmoteColor() != null && !serverConfig.getEmoteColor().isEmpty()) {
			return serverConfig.getEmoteColor();
		}
		return DisplayConfig.emoteColor;
	}
	
	public static String getIRCColor(IIRCConnection connection) {
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

}
