// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.irc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IRCUser {

	private final IRCConnection connection;
	private final Map<String, IRCChannel> channels = new HashMap<String, IRCChannel>();
	private String nick;
	private String authLogin;
	
	public IRCUser(IRCConnection connection, String nick) {
		this.connection = connection;
		this.nick = nick;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String getNick() {
		return nick;
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

	public String getUsername() {
		return connection.getHost() + ":" + nick;
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
