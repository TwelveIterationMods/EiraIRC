// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.irc;

import java.util.ArrayList;
import java.util.List;

import blay09.mods.eirairc.config.ChannelConfig;

public class IRCChannel {

	private IRCConnection connection;
	private String name;
	private String topic;
	private List<IRCUser> users = new ArrayList<IRCUser>();
	
	public IRCChannel(IRCConnection connection, String name) {
		this.connection = connection;
		this.name = name;
	}
	
	public List<IRCUser> getUserList() {
		return users;
	}
	
	public IRCUser getUserByNick(String nick) {
		for(int i = 0; i < users.size(); i++) {
			if(users.get(i).getNick().equals(nick)) {
				return users.get(i);
			}
		}
		return null;
	}
	
	public void addUser(IRCUser user) {
		users.add(user);
	}

	public void removeUser(IRCUser user) {
		users.remove(user);
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
}
