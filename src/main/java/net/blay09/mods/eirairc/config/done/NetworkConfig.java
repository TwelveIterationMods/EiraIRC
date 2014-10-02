// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config.done;

import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

import java.util.List;

public class NetworkConfig {

	private static final String CATEGORY = ConfigurationHandler.CATEGORY_NETWORK;

	public static boolean sslTrustAllCerts = false; // shared
	public static String sslCustomTrustStore = ""; // shared
	public static boolean sslDisableDiffieHellman = true; // shared
	public static String proxyHost = ""; // shared
	public static String proxyUsername = ""; // shared
	public static String proxyPassword = ""; // shared

	public static void load(Configuration config) {
		sslTrustAllCerts = config.get(CATEGORY, "sslTrustAllCerts", sslTrustAllCerts).getBoolean(sslTrustAllCerts);
		sslCustomTrustStore = Utils.unquote(config.get(CATEGORY, "sslCustomTrustStore", "").getString());
		sslDisableDiffieHellman = config.get(CATEGORY, "sslDisableDiffieHellman", sslDisableDiffieHellman).getBoolean(sslDisableDiffieHellman);
		proxyHost = Utils.unquote(config.get(CATEGORY, "proxyHost", "").getString());
		proxyUsername = Utils.unquote(config.get(CATEGORY, "proxyUsername", "").getString());
		proxyPassword = Utils.unquote(config.get(CATEGORY, "proxyPassword", "").getString());
	}
	
	public static void save(Configuration config) {
		config.get(CATEGORY, "sslTrustAllCerts", sslTrustAllCerts).set(sslTrustAllCerts);
		config.get(CATEGORY, "sslCustomTrustStore", "").set(Utils.quote(sslCustomTrustStore));
		config.get(CATEGORY, "sslDisableDiffieHellman", sslDisableDiffieHellman).set(sslDisableDiffieHellman);
		config.get(CATEGORY, "proxyHost", "").set(Utils.quote(proxyHost));
		config.get(CATEGORY, "proxyUsername", "").set(Utils.quote(proxyUsername));
		config.get(CATEGORY, "proxyPassword", "").set(Utils.quote(proxyPassword));
	}

	public static String handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("sslTrustAllCerts")) value = String.valueOf(sslTrustAllCerts);
		else if(key.equals("sslCustomTrustStore")) value = sslCustomTrustStore;
		else if(key.equals("sslDisableDiffieHellman")) value = String.valueOf(sslDisableDiffieHellman);
		else if(key.equals("proxyHost")) value = proxyHost;
		else if(key.equals("proxyUsername")) value = proxyUsername;
		else if(key.equals("proxyPassword")) value = proxyPassword;
		return value;
	}

	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("sslTrustAllCerts")) {
		sslTrustAllCerts = Boolean.parseBoolean(value);
		} else if(key.equals("sslDisableDiffieHellman")) {
			sslDisableDiffieHellman = Boolean.parseBoolean(value);
		} else if(key.equals("sslCustomTrustStore")) {
			if(value.equals("none")) {
				value = "";
			}
			sslCustomTrustStore = value;
		} else if(key.equals("proxyHost")) {
			if(value.equals("none")) {
				value = "";
			}
			proxyHost = value;
		} else if(key.equals("proxyUsername")) {
			if(value.equals("none")) {
				value = "";
			}
			proxyUsername = value;
		} else if(key.equals("proxyPassword")) {
			if(value.equals("none")) {
				value = "";
			}
			proxyPassword = value;
		} else {
			return false;
		}
		return true;
	}
	
	public static void addOptionsToList(List<String> list) {
		list.add("sslTrustAllCerts");
		list.add("sslCustomTrustStore");
		list.add("sslDisableDiffieHellman");
		list.add("proxyHost");
		list.add("proxyUsername");
		list.add("proxyPassword");
	}

	public static void addValuesToList(List<String> list, String option) {
		if(option.equals("sslDisableDiffieHellman") || option.equals("sslTrustAllCerts")) {
			Utils.addBooleansToList(list);
		} else if(option.equals("sslCustomTrustStore") || option.startsWith("proxy")) {
			list.add("none");
		}
	}
}
