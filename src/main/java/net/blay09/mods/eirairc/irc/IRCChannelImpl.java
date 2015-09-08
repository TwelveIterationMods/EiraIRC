// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.irc;

import net.blay09.mods.eirairc.api.config.IConfigManager;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.util.ConfigHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IRCChannelImpl implements IRCChannel {

	private IRCConnectionImpl connection;
	private String name;
	private String topic;
	private Map<String, IRCUser> users = new HashMap<String, IRCUser>();

	public IRCChannelImpl(IRCConnectionImpl connection, String name) {
		this.connection = connection;
		this.name = name;
	}
	
	public Collection<IRCUser> getUserList() {
		return users.values();
	}

	public IRCUser getUser(String nick) {
		return users.get(nick.toLowerCase());
	}
	
	public void addUser(IRCUserImpl user) {
		users.put(user.getName().toLowerCase(), user);
	}

	public void removeUser(IRCUser user) {
		users.remove(user.getName().toLowerCase());
	}

	@Override
	public void message(String message) {
		connection.message(name, message);
	}
	
	@Override
	public void notice(String message) {
		connection.notice(name, message);
	}
	
	public String getName() {
		return name;
	}

	@Override
	public ContextType getContextType() {
		return ContextType.IRCChannel;
	}

	@Override
	public boolean hasTopic() {
		return topic != null;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public String getTopic() {
		return topic;
	}

	public IRCConnectionImpl getConnection() {
		return connection;
	}

	public boolean hasUser(String nick) {
		return users.containsKey(nick.toLowerCase());
	}

	public String getIdentifier() {
		return connection.getIdentifier() + "/" + name.toLowerCase();
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
}
