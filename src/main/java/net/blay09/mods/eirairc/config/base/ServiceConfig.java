// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config.base;

import java.util.HashMap;
import java.util.Map;

import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ServiceConfig {

	private static final String CATEGORY = "services";
	private static final Map<String, ServiceSettings> serverMap = new HashMap<String, ServiceSettings>();
	
	private static final ServiceSettings defaultSettings = new ServiceSettings("PRIVMSG NickServ IDENTIFY {USER} {PASS}", "PRIVMSG NickServ GHOST {NICK} {PASS}");
	
	public static ServiceSettings getSettings(String host, String type) {
		ServiceSettings settings = serverMap.get(host);
		if(settings == null) {
			settings = serverMap.get(type);
			if(settings == null) {
				return defaultSettings;
			}
			return settings;
		}
		return settings;
	}
	
	public static void setupDefaultServices(Configuration config) {
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "atheme", "cmdIdentify", "PRIVMSG NickServ :IDENTIFY {USER} {PASS}");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "atheme", "cmdGhost", "PRIVMSG NickServ :GHOST {NICK} {PASS}");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "atheme", "serverList", new String[] { Utils.quote("irc.esper.net") });
		
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "anope", "cmdIdentify", "PRIVMSG NickServ :IDENTIFY {PASS}");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "anope", "cmdGhost", "PRIVMSG NickServ :GHOST {NICK} {PASS}");
		
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "qbot", "cmdIdentify", "AUTH {USER} {PASS}");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "qbot", "serverList", new String[] { Utils.quote("irc.quakenet.org") });
		config.save();
	}
	
	public static void load(Configuration config) {
		serverMap.clear();
		for(ConfigCategory category : config.getCategory(CATEGORY).getChildren()) {
			String qname = category.getQualifiedName();
			String cmdIdentify = Utils.unquote(config.get(qname, "cmdIdentify", "").getString());
			String cmdGhost = Utils.unquote(config.get(qname, "cmdGhost", "").getString());
			ServiceSettings nss = new ServiceSettings(cmdIdentify, cmdGhost);
			String[] serverList = config.get(qname, "serverList", new String[0]).getStringList();
			for(int i = 0; i < serverList.length; i++) {
				serverMap.put(Utils.unquote(serverList[i]), nss);
			}
		}
	}

}
