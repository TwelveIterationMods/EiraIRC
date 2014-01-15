// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.irc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IRCChannel implements IRCTarget {

	private IRCConnection connection;
	private String name;
	private String topic;
	private Map<String, IRCUser> users = new HashMap<String, IRCUser>();
	
	public IRCChannel(IRCConnection connection, String name) {
		this.connection = connection;
		this.name = name;
	}
	
	public Collection<IRCUser> getUserList() {
		return users.values();
	}
	
	public IRCUser getUser(String nick) {
		return users.get(nick);
	}
	
	public void addUser(IRCUser user) {
		users.put(user.getName(), user);
	}

	public void removeUser(String nick) {
		users.remove(nick);
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
		return users.containsKey(nick);
	}

	public String getIdentifier() {
		return connection.getHost() + "/" + name;
	}
}
