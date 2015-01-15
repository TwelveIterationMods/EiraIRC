// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;

public class IRCResolver {

	public static final short FLAGS_NONE = 0;
	public static final short FLAG_CHANNEL = 2;
	public static final short FLAG_USER = 4;
	public static final short FLAG_ONCHANNEL = 32;
	public static final short FLAG_USERONCHANNEL = 512;

	public static IRCContext resolveTarget(String path, short flags) {
		String server;
		int serverIdx = path.indexOf('/');
		IRCConnection connection;
		if(serverIdx != -1) {
			server = path.substring(0, serverIdx);
			path = path.substring(serverIdx + 1);
			connection = EiraIRC.instance.getConnectionManager().getConnection(server);
			if(connection == null) {
				return IRCTargetError.NotConnected;
			}
		} else {
			IRCConnection foundConnection = null;
			for(IRCConnection con : EiraIRC.instance.getConnectionManager().getConnections()) {
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
		if(connection.getChannelTypes().indexOf(path.charAt(0)) != -1) {
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
			if(!Character.isAlphabetic(path.charAt(0))) {
				for (IRCConnection connection : EiraIRC.instance.getConnectionManager().getConnections()) {
					if (connection.getChannel(path) != null) {
						return connection;
					}
				}
			} else {
				server = path;
			}
		}
		return EiraIRC.instance.getConnectionManager().getConnection(server);
	}
	
	public static ServerConfig resolveServerConfig(String target, short flags) {
		int pathSplitIndex = target.indexOf('/');
		if(pathSplitIndex != -1) {
			target = target.substring(0, pathSplitIndex - 1);
		}
		return ConfigurationHandler.getServerConfig(target);
	}
	
	public static ChannelConfig resolveChannelConfig(String target, short flags) {
		int pathSplitIndex = target.indexOf('/');
		ServerConfig serverConfig = null;
		if(pathSplitIndex != -1) {
			serverConfig = ConfigurationHandler.getServerConfig(target.substring(0, pathSplitIndex - 1));
			target = target.substring(pathSplitIndex + 1);
		}
		if(serverConfig != null) {
			return serverConfig.getChannelConfig(target);
		} else {
			for(ServerConfig config : ConfigurationHandler.getServerConfigs()) {
				if(config.hasChannelConfig(target)) {
					return config.getChannelConfig(target);
				}
			}
		}
		return null;
	}

	public static boolean hasServerPrefix(String path) {
		return path.indexOf('/') != -1;
	}

	public static boolean isChannel(String path) {
		path = stripPath(path);
		if(!Character.isAlphabetic(path.charAt(0))) {
			return true;
		}
		return false;
	}
}
