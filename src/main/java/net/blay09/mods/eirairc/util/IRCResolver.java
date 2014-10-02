// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.config2.ChannelConfig;
import net.blay09.mods.eirairc.config2.ServerConfig;

public class IRCResolver {

	public static final short FLAGS_NONE = 0;
	public static final short FLAG_CHANNEL = 2;
	public static final short FLAG_USER = 4;
	public static final short FLAG_ONCHANNEL = 32;
	public static final short FLAG_USERONCHANNEL = 512;

	public static IRCContext resolveTarget(String path, short flags) {
		String server = null;
		int serverIdx = path.indexOf('/');
		IRCConnection connection = null;
		if(serverIdx != -1) {
			server = path.substring(0, serverIdx);
			path = path.substring(serverIdx + 1);
			connection = EiraIRC.instance.getConnection(server);
			if(connection == null) {
				return IRCTargetError.NotConnected;
			}
		} else {
			IRCConnection foundConnection = null;
			for(IRCConnection con : EiraIRC.instance.getConnections()) {
				if(con.getChannel(path) != null || con.getUser(path) != null) {
					if(foundConnection != null) {
						return IRCTargetError.SpecifyServer;
					}
					foundConnection = con;
				}
			}
			if(foundConnection == null) {
				return IRCTargetError.ServerNotFound;
			}
			connection = foundConnection;
		}
		if(path.startsWith("#")) {
			if((flags & FLAG_CHANNEL) == 0) {
				return IRCTargetError.InvalidTarget;
			}
			IRCChannel channel = connection.getChannel(path);
			if(channel == null) {
				return IRCTargetError.NotOnChannel;
			}
			return channel;
		} else {
			if((flags & FLAG_USER) == 0) {
				return IRCTargetError.InvalidTarget;
			}
			IRCUser user = connection.getUser(path);
			if(user == null) {
				if((flags & FLAG_USERONCHANNEL) != 0) {
					return IRCTargetError.UserNotFound;
				} else {
					user = connection.getOrCreateUser(path);
				}
			}
			return user;
		}
	}
	
	public static String stripPath(String path) {
		int serverIdx = path.indexOf('/');
		if(serverIdx != -1) {
			return path.substring(serverIdx + 1);
		}
		return path;
	}
	
	public static IRCConnection resolveConnection(String path, short flags) {
		String server = null;
		int serverIdx = path.indexOf('/');
		if(serverIdx != -1) {
			server = path.substring(0, serverIdx);
		} else {
			if(path.startsWith("#")) {
				for(IRCConnection connection : EiraIRC.instance.getConnections()) {
					if(connection.getChannel(path) != null) {
						return connection;
					}
				}
			} else {
				server = path;
			}
		}
		return EiraIRC.instance.getConnection(server);
	}
	
	public static ServerConfig resolveServerConfig(String target, short flags) {
		return null;
	}
	
	public static ChannelConfig resolveChannelConfig(String target, short flags) {
		return null;
	}

	public static boolean hasServerPrefix(String path) {
		return path.indexOf('/') != -1;
	}
	
}
