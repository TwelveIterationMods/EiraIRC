// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.irc;

import java.util.ArrayList;
import java.util.List;

public class IRCUser {

	private IRCConnection connection;
	private String nick;
	private final List<IRCChannel> channels = new ArrayList<IRCChannel>();
	
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

	public List<IRCChannel> getChannels() {
		return channels;
	}

	public String getUsername() {
		return connection.getHost() + ":" + nick;
	}
}
