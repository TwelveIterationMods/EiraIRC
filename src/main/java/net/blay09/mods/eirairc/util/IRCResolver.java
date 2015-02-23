// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;

public class IRCResolver {

	public static String stripPath(String path) {
		int serverIdx = path.indexOf('/');
		if(serverIdx != -1) {
			return path.substring(serverIdx + 1);
		}
		return path;
	}
	
	public static IRCConnection resolveConnection(String path) {
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
