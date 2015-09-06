// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.irc;

import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.config.IConfigManager;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.api.irc.TwitchUser;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.I19n;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

public class IRCUserImpl implements IRCUser, TwitchUser {

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
	private final Map<String, IRCChannel> channels = new HashMap<>();
	private final Map<String, IRCChannelUserMode> channelModes = new HashMap<>();
	private final List<QueuedAuthCommand> authCommandQueue = new ArrayList<>();
	private String name;
	private String ident;
	private String hostname;
	private String accountName;
	private EnumChatFormatting nameColor;
	private boolean isTwitchSubscriber;
	private boolean isTwitchTurbo;
	private String displayName;

	public IRCUserImpl(IRCConnectionImpl connection, String name) {
		this.connection = connection;
		this.name = name;
	}

	public void setUsername(String ident) {
		this.ident = ident;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public ContextType getContextType() {
		return ContextType.IRCUser;
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

	@Override
	public Collection<IRCChannel> getChannels() {
		return channels.values();
	}

	@Override
	public String getIdentifier() {
		return connection.getIdentifier() + "/" + name;
	}
	
	public IRCConnectionImpl getConnection() {
		return connection;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
		if(accountName == null || accountName.isEmpty()) {
			notice(I19n.format("eirairc:bot.notAuthed"));
		} else {
			for (QueuedAuthCommand cmd : authCommandQueue) {
				if (ConfigHelper.getBotSettings(cmd.channel).interOpAuthList.get().containsString(accountName, false)) {
					cmd.command.processCommand(cmd.bot, cmd.channel, this, cmd.args, cmd.command);
				} else {
					notice(I19n.format("eirairc:bot.noPermission"));
				}
			}
		}
		authCommandQueue.clear();
	}

	@Override
	public String getAccountName() {
		if(connection.isTwitch()) {
			return name;
		}
		return accountName;
	}

	@Override
	public void notice(String message) {
		connection.notice(name, message);
	}

	@Override
	public void message(String message) {
		connection.message(name, message);
	}

	@Override
	public void ctcpMessage(String message) {
		message(IRCConnectionImpl.CTCP_START + message + IRCConnectionImpl.CTCP_END);
	}

	@Override
	public void ctcpNotice(String message) {
		notice(IRCConnectionImpl.CTCP_START + message + IRCConnectionImpl.CTCP_END);
	}

	@Override
	public IConfigManager getGeneralSettings() {
		return ConfigHelper.getGeneralSettings(this).manager;
	}

	@Override
	public IConfigManager getBotSettings() {
		return ConfigHelper.getBotSettings(this).manager;
	}

	@Override
	public IConfigManager getThemeSettings() {
		return ConfigHelper.getTheme(this).manager;
	}

	public void queueAuthCommand(IRCBotImpl bot, IRCChannel channel, IBotCommand botCommand, String[] args) {
		if(accountName == null) {
			connection.whois(name);
			authCommandQueue.add(new QueuedAuthCommand(bot, channel, botCommand, args));
		} else {
			if(ConfigHelper.getBotSettings(channel).interOpAuthList.get().containsString(accountName, false)) {
				botCommand.processCommand(bot, channel, this, args, botCommand);
			} else {
				notice(I19n.format("eirairc:bot.noPermission"));
			}
		}
	}

	@Override
	public boolean isTwitchSubscriber(IRCChannel channel) {
		return isTwitchSubscriber;
	}

	public void setTwitchSubscriber(boolean isSubscriber) {
		this.isTwitchSubscriber = isSubscriber;
	}

	@Override
	public boolean isTwitchTurbo() {
		return isTwitchTurbo;
	}

	public void setTwitchTurbo(boolean twitchTurbo) {
		this.isTwitchTurbo = twitchTurbo;
	}

	public void setNameColor(EnumChatFormatting nameColor) {
		this.nameColor = nameColor;
	}

	public EnumChatFormatting getNameColor() {
		return nameColor;
	}

	@Override
	public String getUsername() {
		return ident;
	}

	@Override
	public String getHostname() {
		return hostname;
	}

	@Override
	public TwitchUser getTwitchUser() {
		return connection.isTwitch() ? this : null;
	}

	public String getDisplayName() {
		return displayName != null ? displayName : getName();
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
