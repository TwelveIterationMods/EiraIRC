// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import java.util.HashMap;
import java.util.Map;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.BotProfile;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.config.base.BotProfileImpl;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;

public class IRCBotImpl implements IRCBot {

	private final IRCConnectionImpl connection;
	private final Map<String, BotProfileImpl> profiles = new HashMap<String, BotProfileImpl>();
	private BotProfileImpl mainProfile;
	
	public IRCBotImpl(IRCConnectionImpl connection) {
		this.connection = connection;
		updateProfiles();
	}
	
	public BotProfileImpl getProfile(String channelName) {
		BotProfileImpl profile = profiles.get(channelName.toLowerCase());
		if(profile == null) {
			return mainProfile;
		}
		return profile;
	}
	
	@Override
	public BotProfile getProfile(IRCContext channel) {
		if(channel == null) {
			return mainProfile;
		}
		return getProfile(channel.getName());
	}
	
	@Override
	public BotProfile getMainProfile() {
		return mainProfile;
	}

	@Override
	public IRCConnection getConnection() {
		return connection;
	}

	@Override
	public boolean processCommand(IRCChannel channel, IRCUser sender, String message) {
		String[] args = message.split(" ");
		IBotCommand botCommand = null;
		BotProfile botProfile = getProfile(channel);
		if(botProfile != null) {
			botCommand = botProfile.getCommand(args[0]);
			if(botCommand == null) {
				botProfile = mainProfile;
			}
		} else {
			botProfile = mainProfile;
		}
		if(botCommand == null) {
			botCommand = botProfile.getCommand(args[0]);
			if(botCommand == null) {
				return false;
			}
		}
		if(channel != null && !botCommand.isChannelCommand()) {
			return false;
		}
		String[] shiftedArgs = Utils.shiftArgs(args, 1);
		botCommand.processCommand(this, channel, sender, shiftedArgs);
		return true;
	}

	@Override
	public boolean isServerSide() {
		return Utils.isServerSide();
	}

	public void updateProfiles() {
		ServerConfig serverConfig = ConfigHelper.getServerConfig(connection);
		mainProfile = ConfigurationHandler.getBotProfile(serverConfig.getBotProfile());
		profiles.clear();
		for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
			if(!channelConfig.getBotProfile().equals(mainProfile.getName()) && !channelConfig.getBotProfile().equals(BotProfile.INHERIT)) {
				profiles.put(channelConfig.getName().toLowerCase(), ConfigurationHandler.getBotProfile(channelConfig.getBotProfile()));
			}
		}
	}

}
