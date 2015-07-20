// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.BotStringComponent;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;

public class ConfigHelper {
	
	public static String formatNick(String format) {
		String s = format;
		s = s.replace("%USERNAME%", Utils.getUsername());
		s = s.replace("%ANONYMOUS%", "justinfan" + String.valueOf((int) (Math.random() * 1000000)) + String.valueOf(((int) (Math.random() * 1000000))));
		return s;
	}
	
	public static String getNick(ServerConfig serverConfig) {
		if(serverConfig.getNick() != null && !serverConfig.getNick().isEmpty()) {
			return serverConfig.getNick();
		}
		return Globals.DEFAULT_NICK;
	}

	public static String getFormattedNick(ServerConfig serverConfig) {
		return formatNick(getNick(serverConfig));
	}

	public static String getQuitMessage(IRCConnection connection) {
		return ConfigurationHandler.getOrCreateServerConfig(connection.getHost()).getBotSettings().getString(BotStringComponent.QuitMessage);
	}

	public static ServerConfig getServerConfig(IRCConnection connection) {
		return ((IRCConnectionImpl) connection).getServerConfig();
	}
	
	public static ChannelConfig getChannelConfig(IRCChannel channel) {
		return getServerConfig(channel.getConnection()).getOrCreateChannelConfig(channel);
	}

	public static ThemeSettings getTheme(IRCContext context) {
		if(context instanceof IRCChannel) {
			return getChannelConfig((IRCChannel) context).getTheme();
		}
		return SharedGlobalConfig.theme;
	}

	public static BotSettings getBotSettings(IRCContext context) {
		if(context instanceof IRCChannel) {
			return getChannelConfig((IRCChannel) context).getBotSettings();
		}
		return SharedGlobalConfig.botSettings;
	}

	public static GeneralSettings getGeneralSettings(IRCContext context) {
		if(context instanceof IRCChannel) {
			return getChannelConfig((IRCChannel) context).getGeneralSettings();
		}
		return SharedGlobalConfig.generalSettings;
	}

	public static ServerConfig resolveServerConfig(String target) {
		int pathSplitIndex = target.indexOf('/');
		if(pathSplitIndex != -1) {
			target = target.substring(0, pathSplitIndex - 1);
		}
		return ConfigurationHandler.getServerConfig(target);
	}

	public static ChannelConfig resolveChannelConfig(String target) {
		int pathSplitIndex = target.indexOf('/');
		ServerConfig serverConfig = null;
		if(pathSplitIndex != -1) {
			serverConfig = ConfigurationHandler.getServerConfig(target.substring(0, pathSplitIndex - 1));
			target = target.substring(pathSplitIndex + 1);
		}
		if(serverConfig != null) {
			return serverConfig.getChannelConfig(target);
		} else {
			for(ServerConfig config : ConfigurationHandler.getServerConfigs()) {
				if(config.hasChannelConfig(target)) {
					return config.getChannelConfig(target);
				}
			}
		}
		return null;
	}
}
