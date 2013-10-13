// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import blay09.mods.eirairc.irc.IRCChannel;

public class ServerConfig {
	
	private final String host;
	private String nick;
	private String serverPassword;
	private String nickServName;
	private String nickServPassword;
	private final Map<String, ChannelConfig> channels = new HashMap<String, ChannelConfig>();
	private boolean serverSide;
	private boolean allowPrivateMessages = true;
	private boolean autoConnect = true;
	
	public ServerConfig(String host) {
		this.host = host;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getServerPassword() {
		return serverPassword;
	}
	
	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}
	
	public String getNickServName() {
		return nickServName;
	}
	
	public String getNickServPassword() {
		return nickServPassword;
	}
	
	public boolean isClientSide() {
		return !serverSide;
	}
	
	public boolean isAutoConnect() {
		return autoConnect;
	}
	
	public boolean allowsPrivateMessages() {
		return allowPrivateMessages;
	}
	
	public ChannelConfig getChannelConfig(IRCChannel channel) {
		ChannelConfig channelConfig = channels.get(channel.getName());
		if(channelConfig == null) {
			channelConfig = new ChannelConfig(channel.getName());
			if(host.equals(Globals.TWITCH_SERVER)) {
				channelConfig.defaultTwitch();
			} else if(serverSide) {
				channelConfig.defaultServer();
			} else {
				channelConfig.defaultClient();
			}
		}
		return channelConfig;
	}

	public void setNickServ(String nickServName, String nickServPassword) {
		this.nickServName = nickServName;
		this.nickServPassword = nickServPassword;
	}

	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
	}

	public void setAllowPrivateMessages(boolean allowPrivateMessages) {
		this.allowPrivateMessages = allowPrivateMessages;
	}

	public void addChannelConfig(ChannelConfig channelConfig) {
		channels.put(channelConfig.getName(), channelConfig);
	}

	public boolean hasChannelConfig(String channelName) {
		return channels.containsKey(channelName);
	}

	public void setServerSide(boolean serverSide) {
		this.serverSide = serverSide;
	}

	public Collection<ChannelConfig> getChannelConfigs() {
		return channels.values();
	}

}
