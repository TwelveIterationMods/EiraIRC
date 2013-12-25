// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.util;

import java.util.HashMap;
import java.util.Map;

public class NickServSettings {

	private static final Map<String, NickServSettings> settingsMap = new HashMap<String, NickServSettings>();
	private static final NickServSettings defaultSettings = new NickServSettings("NickServ", "IDENTIFY");
	
	public static final NickServSettings getSettings(String host) {
		NickServSettings settings = settingsMap.get(host);
		if(settings == null) {
			return defaultSettings;
		}
		return settings;
	}
	
	static {
		settingsMap.put("irc.quakenet.org", new NickServSettings("Q@CServe.quakenet.org", "AUTH"));
	}
	
	private String bot;
	private String cmd;
	
	public NickServSettings(String bot, String cmd) {
		this.bot = bot;
		this.cmd = cmd;
	}
	
	public String getBotName() {
		return bot;
	}
	
	public String getCommand() {
		return cmd;
	}
}
