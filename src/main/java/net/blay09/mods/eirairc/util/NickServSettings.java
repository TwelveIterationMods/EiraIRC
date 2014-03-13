// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

import java.util.HashMap;
import java.util.Map;

public class NickServSettings {

	private static final Map<String, NickServSettings> settingsMap = new HashMap<String, NickServSettings>();
	private static final NickServSettings defaultSettings = new NickServSettings("NickServ", "IDENTIFY", "GHOST");
	
	public static final NickServSettings getSettings(String host) {
		NickServSettings settings = settingsMap.get(host);
		if(settings == null) {
			return defaultSettings;
		}
		return settings;
	}
	
	static {
		settingsMap.put("irc.quakenet.org", new NickServSettings("Q@CServe.quakenet.org", "AUTH", null));
		settingsMap.put("irc.esper.net", new NickServSettings("NickServ@services.esper.net", defaultSettings));
	}
	
	private String botNick;
	private String identifyCMD;
	private String ghostCMD;
	
	public NickServSettings(String botNick, String identifyCMD, String ghostCMD) {
		this.botNick = botNick;
		this.identifyCMD = identifyCMD;
		this.ghostCMD = ghostCMD;
	}
	
	public NickServSettings(String botNick, NickServSettings copy) {
		this(botNick, copy.identifyCMD, copy.ghostCMD);
	}

	public String getBotName() {
		return botNick;
	}
	
	public String getIdentifyCommand() {
		return identifyCMD;
	}
	
	public String getGhostCommand() {
		return ghostCMD;
	}
}
