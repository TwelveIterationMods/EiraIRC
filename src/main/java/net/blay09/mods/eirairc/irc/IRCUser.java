// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.irc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IRCUser implements IRCTarget {

	private final IRCConnection connection;
	private final Map<String, IRCChannel> channels = new HashMap<String, IRCChannel>();
	private String name;
	private String authLogin;
	
	public IRCUser(IRCConnection connection, String name) {
		this.connection = connection;
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void addChannel(IRCChannel channel) {
		channels.put(channel.getName(), channel);
	}
	
	public void removeChannel(IRCChannel channel) {
		channels.remove(channel.getName());
	}
	
	public Collection<IRCChannel> getChannels() {
		return channels.values();
	}

	public String getIdentifier() {
		return connection.getHost() + "/" + name;
	}
	
	public String getUsername() {
		// TODO return nick!username@hostname instead
		return name;
	}

	public IRCConnection getConnection() {
		return connection;
	}

	public void setAuthLogin(String authLogin) {
		this.authLogin = authLogin;
	}
	
	public String getAuthLogin() {
		return authLogin;
	}

}
