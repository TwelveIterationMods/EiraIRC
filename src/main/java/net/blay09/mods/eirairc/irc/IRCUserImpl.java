// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.irc;

import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.settings.BotStringListComponent;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

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
	private String username;
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

	public void setUsername(String username) {
		this.username = username;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
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

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getHostname() {
		return hostname;
	}

	@Override
	public IRCConnectionImpl getConnection() {
		return connection;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
		if(accountName == null || accountName.isEmpty()) {
			notice(Utils.getLocalizedMessage("irc.bot.notAuthed"));
		} else {
			for (QueuedAuthCommand cmd : authCommandQueue) {
				if (ConfigHelper.getBotSettings(cmd.channel).containsString(BotStringListComponent.InterOpAuthList, accountName)) {
					cmd.command.processCommand(cmd.bot, cmd.channel, this, cmd.args, cmd.command);
				} else {
					notice(Utils.getLocalizedMessage("irc.bot.noPermission"));
				}
			}
		}
		authCommandQueue.clear();
	}

	@Override
	public String getAccountName() {
		return accountName;
	}

	@Override
	public void notice(String message) {
		connection.notice(name, message);
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
	public void message(String message) {
		connection.message(name, message);
	}

	public void queueAuthCommand(IRCBotImpl bot, IRCChannel channel, IBotCommand botCommand, String[] args) {
		if(accountName == null) {
			connection.whois(name);
			authCommandQueue.add(new QueuedAuthCommand(bot, channel, botCommand, args));
		} else {
			if(ConfigHelper.getBotSettings(channel).containsString(BotStringListComponent.InterOpAuthList, accountName)) {
				botCommand.processCommand(bot, channel, this, args, botCommand);
			} else {
				notice(Utils.getLocalizedMessage("irc.bot.noPermission"));
			}
		}
	}

	public void setTwitchSubscriber(boolean isTwitchSubscriber) {
		this.isTwitchSubscriber = isTwitchSubscriber;
	}

	public boolean isTwitchSubscriber() {
		return isTwitchSubscriber;
	}

	public void setTwitchTurbo(boolean isTwitchTurbo) {
		this.isTwitchTurbo = isTwitchTurbo;
	}

	public boolean isTwitchTurbo() {
		return isTwitchTurbo;
	}

	public void setNameColor(EnumChatFormatting nameColor) {
		this.nameColor = nameColor;
	}

	public EnumChatFormatting getNameColor() {
		return nameColor;
	}

	public String getDisplayName() {
		return displayName != null ? displayName : getName();
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
