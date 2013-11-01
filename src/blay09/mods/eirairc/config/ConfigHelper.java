package blay09.mods.eirairc.config;

import net.minecraftforge.common.ConfigCategory;
import blay09.mods.eirairc.irc.IRCConnection;

public class ConfigHelper {

	public static DisplayFormatConfig getDisplayFormatConfig() {
		DisplayFormatConfig dfc = GlobalConfig.displayFormates.get(GlobalConfig.displayMode);
		if(dfc == null) {
			GlobalConfig.displayMode = "S-Light";
			dfc = GlobalConfig.displayFormates.get(GlobalConfig.displayMode);
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
		return GlobalConfig.emoteColor;
	}
	
	public static String getIRCColor(ChannelConfig channelConfig) {
		return getIRCColor(channelConfig.getServerConfig());
	}
	
	public static String getIRCColor(ServerConfig serverConfig) {
		if(serverConfig.getIRCColor() != null && !serverConfig.getIRCColor().isEmpty()) {
			return serverConfig.getIRCColor();
		}
		return GlobalConfig.ircColor;
	}
	
	public static String getNick(ServerConfig serverConfig) {
		if(serverConfig.getNick() != null && !serverConfig.getNick().isEmpty()) {
			return serverConfig.getNick();
		}
		return GlobalConfig.nick;
	}
	
}
