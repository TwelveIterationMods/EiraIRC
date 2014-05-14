// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCContext;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;

public class IRCResolver {

	public static final short FLAGS_NONE = 0;
	public static final short FLAG_CHANNEL = 2;
	public static final short FLAG_USER = 4;
	public static final short FLAG_ONCHANNEL = 32;
	public static final short FLAG_USERONCHANNEL = 512;

	public static IIRCContext resolveTarget(String path, short flags) {
		String server = null;
		int serverIdx = path.indexOf('/');
		IIRCConnection connection = null;
		if(serverIdx != -1) {
			server = path.substring(0, serverIdx);
			path = path.substring(serverIdx + 1);
			connection = EiraIRC.instance.getConnection(server);
			if(connection == null) {
				return IRCTargetError.NotConnected;
			}
		} else {
			IIRCConnection foundConnection = null;
			for(IIRCConnection con : EiraIRC.instance.getConnections()) {
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
			IIRCChannel channel = connection.getChannel(path);
			if(channel == null) {
				return IRCTargetError.NotOnChannel;
			}
			return channel;
		} else {
			if((flags & FLAG_USER) == 0) {
				return IRCTargetError.InvalidTarget;
			}
			IIRCUser user = connection.getUser(path);
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
	
	public static IIRCConnection resolveConnection(String path, short flags) {
		String server = null;
		int serverIdx = path.indexOf('/');
		if(serverIdx != -1) {
			server = path.substring(0, serverIdx);
		} else {
			if(path.startsWith("#")) {
				for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
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
