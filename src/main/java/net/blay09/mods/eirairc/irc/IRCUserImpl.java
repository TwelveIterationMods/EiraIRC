// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.irc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCUser;

public class IRCUserImpl implements IRCUser {

	private final IRCConnectionImpl connection;
	private final Map<String, IRCChannel> channels = new HashMap<String, IRCChannel>();
	private final List<IRCChannel> opChannels = new ArrayList<IRCChannel>();
	private final List<IRCChannel> voiceChannels = new ArrayList<IRCChannel>();
	private String name;
	private String authLogin;
	
	public IRCUserImpl(IRCConnectionImpl connection, String name) {
		this.connection = connection;
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public boolean isOperator(IRCChannel channel) {
		return opChannels.contains(channel);
	}
	
	@Override
	public boolean hasVoice(IRCChannel channel) {
		return opChannels.contains(channel) || voiceChannels.contains(channel);
	}
	
	public void setOperator(IRCChannelImpl channel, boolean opFlag) {
		if(opFlag && !opChannels.contains(channel)) {
			opChannels.add(channel);
		} else {
			opChannels.remove(channel);
		}
	}
	
	public void setVoice(IRCChannelImpl channel, boolean voiceFlag) {
		if(voiceFlag && !voiceChannels.contains(channel)) {
			voiceChannels.add(channel);
		} else {
			voiceChannels.remove(channel);
		}
	}
	
	public void addChannel(IRCChannelImpl channel) {
		channels.put(channel.getName(), channel);
	}
	
	public void removeChannel(IRCChannelImpl channel) {
		channels.remove(channel.getName());
	}
	
	public Collection<IRCChannel> getChannels() {
		return channels.values();
	}

	public String getIdentifier() {
		return connection.getIdentifier() + "/" + name;
	}
	
	public String getUsername() {
		// TODO return nick!username@hostname instead
		return name;
	}

	public IRCConnectionImpl getConnection() {
		return connection;
	}

	public void setAuthLogin(String authLogin) {
		this.authLogin = authLogin;
	}
	
	public String getAuthLogin() {
		return authLogin;
	}

	@Override
	public void whois() {
		connection.whois(name);
	}

	@Override
	public void notice(String message) {
		connection.notice(name, message);
	}

	@Override
	public void message(String message) {
		connection.message(name, message);
	}

}
