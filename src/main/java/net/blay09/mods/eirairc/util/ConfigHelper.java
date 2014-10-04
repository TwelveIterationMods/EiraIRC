// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.BotStringComponent;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;

public class ConfigHelper {
	
	public static String formatNick(String format) {
		return format.replace("%USERNAME%", Utils.getUsername());
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
		return ConfigurationHandler.getOrCreateServerConfig(connection.getHost());
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
}
