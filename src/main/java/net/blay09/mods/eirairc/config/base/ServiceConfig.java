// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.base;

import net.blay09.mods.eirairc.config.ServiceSettings;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;
import java.util.Map;

public class ServiceConfig {

	private static final String CATEGORY = "services";
	private static final Map<String, ServiceSettings> serverMap = new HashMap<>();

	private static final ServiceSettings defaultSettings = new ServiceSettings("NickServ", "PRIVMSG NickServ IDENTIFY {USER} {PASS}", "PRIVMSG NickServ GHOST {NICK} {PASS}");
	
	public static ServiceSettings getSettings(String host, String type) {
		ServiceSettings settings = serverMap.get(host);
		if(settings == null) {
			if(type != null) {
				type = type.replaceAll("[0-9]", "*");
				settings = serverMap.get(type);
			}
			if(settings == null) {
				return defaultSettings;
			}
			return settings;
		}
		return settings;
	}
	
	public static void setupDefaultServices(Configuration config) {
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "synirc", "serviceName", "NickServ");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "synirc", "cmdIdentify", "NickServ IDENTIFY {PASS}");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "synirc", "cmdGhost", "NickServ GHOST {NICK} {PASS}");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "synirc", "serverList", new String[] { Utils.quote("irc.synirc.net") });
		
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "atheme", "serviceName", "NickServ");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "atheme", "cmdIdentify", "PRIVMSG NickServ :IDENTIFY {USER} {PASS}");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "atheme", "cmdGhost", "PRIVMSG NickServ :GHOST {NICK} {PASS}");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "atheme", "serverList", new String[] {
				"\"charybdis-*.*.*-dev\"",
				"\"irc.esper.net\""
		});
		
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "anope", "serviceName", "NickServ");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "anope", "cmdIdentify", "PRIVMSG NickServ :IDENTIFY {PASS}");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "anope", "cmdGhost", "PRIVMSG NickServ :GHOST {NICK} {PASS}");
		
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "qbot", "serviceName", "Q");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "qbot", "cmdIdentify", "AUTH {USER} {PASS}");
		config.get(CATEGORY + Configuration.CATEGORY_SPLITTER + "qbot", "serverList", new String[] { "\"irc.quakenet.org\"" });
		config.save();
	}
	
	public static void load(Configuration config) {
		serverMap.clear();
		for(ConfigCategory category : config.getCategory(CATEGORY).getChildren()) {
			String qname = category.getQualifiedName();
			String serviceName = Utils.unquote(config.get(qname, "serviceName", "").getString());
			String cmdIdentify = Utils.unquote(config.get(qname, "cmdIdentify", "").getString());
			String cmdGhost = Utils.unquote(config.get(qname, "cmdGhost", "").getString());
			ServiceSettings nss = new ServiceSettings(serviceName, cmdIdentify, cmdGhost);
			String[] serverList = config.get(qname, "serverList", new String[0]).getStringList();
			for(String entry : serverList) {
				serverMap.put(Utils.unquote(entry), nss);
			}
		}
	}

}
