// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerConfig {

	public static final char CMODE_WRITE = 'w';
	public static final char CMODE_READ = 'r';
	public static final char CMODE_IRCJOINS = 'j';
	public static final char CMODE_MCJOINS = 'J';
	public static final char CMODE_MCDEATHS = 'D';
	public static final char CMODE_MCEMOTES = 'E';
	public static final char CMODE_IRCEMOTES = 'e';
	
	public static final String defaultServerMode = "wrjJDEe";
	public static final String defaultClientMode = "wrjEe";
	
	public final String host;
	public String nick;
	public String serverPassword = "";
	public String nickServName = "";
	public String nickServPassword = "";
	public final List<String> channels = new ArrayList<String>();
	public final Map<String, String> channelFlags = new HashMap<String, String>();
	public boolean clientSide = false;
	public boolean allowPrivateMessages = true;
	public boolean autoConnect = true;
	
	public ServerConfig(String host, String nick) {
		this.host = host;
		this.nick = nick;
	}

	public boolean hasChannelFlag(String channel, char c) {
		String channelMode = channelFlags.get(channel);
		if(channelMode == null) {
			channelMode = clientSide ? defaultClientMode : defaultServerMode;
			channelFlags.put(channel, channelMode);
		}
		if(channelMode.indexOf(c) != -1) {
			return true;
		}
		return false;
	}
	
	public boolean alterChannelFlags(String channel, String action) {
		int mode = 0;
		String channelMode = new String(channelFlags.containsKey(channel) ? channelFlags.get(channel) : (clientSide ? defaultClientMode : defaultServerMode));
		for(int i = 0; i < action.length(); i++) {
			char c = action.charAt(i);
			if(c == '+') {
				mode = 1;
			} else if(c == '-') {
				mode = -1;
			} else if(mode == 1) {
				if(channelMode.indexOf(c) == -1) {
					channelMode += c;
				}
			} else if(mode == -1) {
				int j = channelMode.indexOf(c);
				if(j != -1) {
					channelMode = channelMode.substring(0, j - 1) + channelMode.substring(j + 1);
				}
			} else {
				return false;
			}
		}
		channelFlags.put(channel, channelMode);
		return true;
	}

	public boolean hasChannelFlags(String channel, String flags) {
		String channelMode = channelFlags.get(channel);
		if(channelMode == null) {
			channelMode = clientSide ? defaultClientMode : defaultServerMode;
			channelFlags.put(channel, channelMode);
		}
		for(int i = 0; i < flags.length(); i++) {
			if(channelMode.indexOf(flags.charAt(i)) == -1) {
				return false;
			}
		}
		return true;
	}
}
