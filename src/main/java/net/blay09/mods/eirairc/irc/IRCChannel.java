// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.irc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;

public class IRCChannel implements IIRCChannel {

	private IRCConnection connection;
	private String name;
	private String topic;
	private Map<String, IIRCUser> users = new HashMap<String, IIRCUser>();
	
	public IRCChannel(IRCConnection connection, String name) {
		this.connection = connection;
		this.name = name;
	}
	
	public Collection<IIRCUser> getUserList() {
		return users.values();
	}
	
	public IIRCUser getUser(String nick) {
		return users.get(nick.toLowerCase());
	}
	
	public void addUser(IRCUser user) {
		users.put(user.getName().toLowerCase(), user);
	}

	public void removeUser(IIRCUser user) {
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

	public boolean hasTopic() {
		return topic != null;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	public IRCConnection getConnection() {
		return connection;
	}

	public boolean hasUser(String nick) {
		return users.containsKey(nick.toLowerCase());
	}

	public String getIdentifier() {
		return connection.getIdentifier() + "/" + name;
	}

}
