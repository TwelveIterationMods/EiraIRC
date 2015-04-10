// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.irc;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;

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

	public boolean hasTopic() {
		return topic != null;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}

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
		return connection.getIdentifier() + "/" + name;
	}

}
