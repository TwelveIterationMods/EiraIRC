// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc;

import java.util.HashMap;
import java.util.Map;

public class NickServSettings {

	public static final Map<String, NickServSettings> settings = new HashMap<String, NickServSettings>();
	
	static {
		settings.put("irc.esper.net", new NickServSettings("NickServ", "IDENTIFY"));
//		settings.put("irc.quakenet.org", new NickServSettings("Q@CServe.quakenet.org", "AUTH"));
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
