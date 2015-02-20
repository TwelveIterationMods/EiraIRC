// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.irc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.settings.BotStringListComponent;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;

public class IRCUserImpl implements IRCUser {

	private static class QueuedAuthCommand {
		public final IRCBot bot;
		public final IRCChannel channel;
		public final IBotCommand command;
		public final String[] args;

		public QueuedAuthCommand(IRCBot bot, IRCChannel channel, IBotCommand command, String[] args) {
			this.bot = bot;
			this.channel = channel;
			this.command = command;
			this.args = args;
		}
	}

	private final IRCConnectionImpl connection;
	private final Map<String, IRCChannel> channels = new HashMap<String, IRCChannel>();
	private final Map<String, IRCChannelUserMode> channelModes = new HashMap<String, IRCChannelUserMode>();
	private final List<QueuedAuthCommand> authCommandQueue = new ArrayList<QueuedAuthCommand>();
	private String name;
	private String authLogin;
	
	public IRCUserImpl(IRCConnectionImpl connection, String name) {
		this.connection = connection;
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public boolean isOperator(IRCChannel channel) {
		IRCChannelUserMode mode = channelModes.get(channel.getName().toLowerCase());
		return mode != null && mode != IRCChannelUserMode.VOICE;
	}
	
	@Override
	public boolean hasVoice(IRCChannel channel) {
		IRCChannelUserMode mode = channelModes.get(channel.getName().toLowerCase());
		return mode == IRCChannelUserMode.VOICE;
	}

	@Override
	public String getChannelModePrefix(IRCChannel channel) {
		IRCChannelUserMode mode = channelModes.get(channel.getName().toLowerCase());
		if(mode != null) {
			int idx = channel.getConnection().getChannelUserModes().indexOf(mode.modeChar);
			if(idx != -1) {
				return String.valueOf(channel.getConnection().getChannelUserModePrefixes().charAt(idx));
			}
			return "";
		}
		return "";
	}

	public void setChannelUserMode(IRCChannel channel, IRCChannelUserMode mode) {
		if(mode == null) {
			channelModes.remove(channel.getName().toLowerCase());
		} else {
			channelModes.put(channel.getName().toLowerCase(), mode);
		}
	}

	public IRCChannelUserMode getChannelUserMode(IRCChannel channel) {
		return channelModes.get(channel.getName().toLowerCase());
	}
	
	public void addChannel(IRCChannelImpl channel) {
		channels.put(channel.getName(), channel);
	}
	
	public void removeChannel(IRCChannelImpl channel) {
		channels.remove(channel.getName());
	}
	
	public Collection<IRCChannel> getChannels() {
		return channels.values();
	}

	public String getIdentifier() {
		return connection.getIdentifier() + "/" + name;
	}
	
	public String getUsername() {
		// TODO return nick!username@hostname instead
		return name;
	}

	public IRCConnectionImpl getConnection() {
		return connection;
	}

	public void setAuthLogin(String authLogin) {
		this.authLogin = authLogin;
		if(authLogin == null || authLogin.isEmpty()) {
			notice(Utils.getLocalizedMessage("irc.bot.notAuthed"));
		} else {
			for (QueuedAuthCommand cmd : authCommandQueue) {
				if (ConfigHelper.getBotSettings(cmd.channel).containsString(BotStringListComponent.InterOpAuthList, authLogin)) {
					cmd.command.processCommand(cmd.bot, cmd.channel, this, cmd.args, cmd.command);
				} else {
					notice(Utils.getLocalizedMessage("irc.bot.noPermission"));
				}
			}
		}
		authCommandQueue.clear();
	}
	
	public String getAuthLogin() {
		return authLogin;
	}

	@Override
	public void whois() {
		connection.whois(name);
	}

	@Override
	public void notice(String message) {
		connection.notice(name, message);
	}

	@Override
	public void message(String message) {
		connection.message(name, message);
	}

	public void queueAuthCommand(IRCBotImpl bot, IRCChannel channel, IBotCommand botCommand, String[] args) {
		if(authLogin == null) {
			whois();
			authCommandQueue.add(new QueuedAuthCommand(bot, channel, botCommand, args));
		} else {
			if(ConfigHelper.getBotSettings(channel).containsString(BotStringListComponent.InterOpAuthList, authLogin)) {
				botCommand.processCommand(bot, channel, this, args, botCommand);
			} else {
				notice(Utils.getLocalizedMessage("irc.bot.noPermission"));
			}
		}
	}
}
