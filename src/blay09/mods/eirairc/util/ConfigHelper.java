// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.util;

import net.minecraftforge.common.ConfigCategory;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.DisplayConfig;
import blay09.mods.eirairc.config.DisplayFormatConfig;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IRCConnection;

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
	
	public static String getEmoteColor(ChannelConfig channelConfig) {
		return getEmoteColor(channelConfig.getServerConfig());
	}
	
	public static String getEmoteColor(ServerConfig serverConfig) {
		if(serverConfig.getEmoteColor() != null && !serverConfig.getEmoteColor().isEmpty()) {
			return serverConfig.getEmoteColor();
		}
		return DisplayConfig.emoteColor;
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
	
}
