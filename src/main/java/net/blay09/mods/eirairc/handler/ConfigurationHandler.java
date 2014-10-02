// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.blay09.mods.eirairc.config.done.*;
import net.blay09.mods.eirairc.config2.ChannelConfig;
import net.blay09.mods.eirairc.config2.ServerConfig;
import net.blay09.mods.eirairc.config2.base.BotProfileImpl;
import net.blay09.mods.eirairc.config2.base.DisplayFormatConfig;
import net.blay09.mods.eirairc.config2.base.ServiceConfig;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {

	private static String CONFIG_VERSION = "2";
	public static final String CATEGORY_GLOBAL = "global";

	public static final String CATEGORY_DISPLAY = "display";
	public static final String CATEGORY_FORMATS = "formats";
	public static final String CATEGORY_SERVERONLY = "serveronly";
	public static final String CATEGORY_CLIENTONLY = "clientonly";
	public static final String CATEGORY_KEYBINDS = "keybinds";
	public static final String CATEGORY_SERVERS = "servers";
	public static final String CATEGORY_CHANNELS = "channels";
	public static final String CATEGORY_COMPAT = "compatibility";
	public static final String CATEGORY_NOTIFICATIONS = "notifications";
	public static final String CATEGORY_NETWORK = "network";
	public static final String PREFIX_SERVER = "server";
	public static final String PREFIX_CHANNEL = "channel";
	
	private static final Map<String, ServerConfig> serverConfigs = new HashMap<String, ServerConfig>();

	private static File configFile;
	private static Configuration config;
	private static Map<String, BotProfileImpl> botProfiles = new HashMap<String, BotProfileImpl>();
	private static List<BotProfileImpl> botProfileList = new ArrayList<BotProfileImpl>();
	private static BotProfileImpl defaultBotProfile;
	private static File botProfileDir;
	private static final Map<String, DisplayFormatConfig> displayFormats = new HashMap<String, DisplayFormatConfig>();
	private static List<DisplayFormatConfig> displayFormatList = new ArrayList<DisplayFormatConfig>();
	private static DisplayFormatConfig defaultDisplayFormat;
	
	public static void loadBotProfiles(File profileDir) {
		botProfiles.clear();
		botProfileList.clear();
		if(!profileDir.exists()) {
			profileDir.mkdirs();
		}
		botProfileDir = profileDir;
		BotProfileImpl.setupDefaultProfiles(profileDir);
		File[] files = profileDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith(".cfg");
			}
		});
		for(int i = 0; i < files.length; i++) {
			BotProfileImpl botProfile = new BotProfileImpl(files[i]);
			botProfile.loadCommands();
			botProfiles.put(botProfile.getName(), botProfile);
			botProfileList.add(botProfile);
		}
		findDefaultBotProfile();
	}
	
	public static void findDefaultBotProfile() {
		defaultBotProfile = botProfiles.get("Client");
		if(defaultBotProfile == null) {
			for(BotProfileImpl botProfile : botProfiles.values()) {
				if(botProfile.isDefaultProfile()) {
					defaultBotProfile = botProfile;
					return;
				}
			}
			if(defaultBotProfile == null) {
				Iterator<BotProfileImpl> it = botProfiles.values().iterator();
				defaultBotProfile = it.next();
			}
		}
	}
	
	public static void loadDisplayFormats(File formatDir) {
		displayFormats.clear();
		displayFormatList.clear();
		if(!formatDir.exists()) {
			formatDir.mkdirs();
		}
		DisplayFormatConfig.setupDefaultFormats(formatDir);
		File[] files = formatDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith(".cfg");
			}
		});
		for(int i = 0; i < files.length; i++) {
			DisplayFormatConfig dfc = new DisplayFormatConfig(files[i]);
			dfc.loadFormats();
			displayFormats.put(dfc.getName(), dfc);
			displayFormatList.add(dfc);
		}
		defaultDisplayFormat = displayFormats.get(DisplayFormatConfig.DEFAULT_FORMAT);
	}
	
	public static void loadServices(File configDir) {
		if(!configDir.exists()) {
			configDir.mkdirs();
		}
		Configuration serviceConfig = new Configuration(new File(configDir, "services.cfg"));
		ServiceConfig.setupDefaultServices(serviceConfig);
		ServiceConfig.load(serviceConfig);
	}
	
	public static void load(File configFile) {
		ConfigurationHandler.configFile = configFile;
		config = new Configuration(configFile);
		
		GlobalConfig.load(config);
		NotificationConfig.load(config);
		ScreenshotConfig.load(config);

		config.save();
	}
	
	public static void save() {
		GlobalConfig.save(config);
		NotificationConfig.save(config);
		ScreenshotConfig.save(config);
	}
	
	public static ServerConfig getServerConfig(String host) {
		ServerConfig serverConfig = serverConfigs.get(host.toLowerCase());
		if(serverConfig == null) {
			serverConfig = new ServerConfig(host);
			serverConfig.useDefaults(Utils.isServerSide());
		}
		return serverConfig;
	}

	public static Collection<ServerConfig> getServerConfigs() {
		return serverConfigs.values();
	}

	public static void addServerConfig(ServerConfig serverConfig) {
		serverConfigs.put(serverConfig.getAddress().toLowerCase(), serverConfig);
	}
	
	public static void removeServerConfig(String host) {
		serverConfigs.remove(host.toLowerCase());
	}

	public static boolean hasServerConfig(String host) {
		return serverConfigs.containsKey(host.toLowerCase());
	}

	public static void handleConfigCommand(ICommandSender sender, String target, String key, String value) {
		if(target.equals("global")) {
			boolean result = false;
			result = GlobalConfig.handleConfigCommand(sender, key, value);
			if(!result) result = NotificationConfig.handleConfigCommand(sender, key, value);
			if(!result) result = ScreenshotConfig.handleConfigCommand(sender, key, value);
			if(result) {
				Utils.sendLocalizedMessage(sender, "irc.config.change", "Global", key, value);
				ConfigurationHandler.save();
			} else {
				Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", "Global", key);
			}
		} else {
			ChannelConfig channelConfig = IRCResolver.resolveChannelConfig(target, IRCResolver.FLAGS_NONE);
			if(channelConfig != null) {
				channelConfig.handleConfigCommand(sender, key, value);
			} else {
				ServerConfig serverConfig = IRCResolver.resolveServerConfig(target, IRCResolver.FLAGS_NONE);
				if(serverConfig != null) {
					serverConfig.handleConfigCommand(sender, key, value);
				} else {
					Utils.sendLocalizedMessage(sender, "irc.target.targetNotFound", target);
				}
			}
		}
	}
	
	public static void handleConfigCommand(ICommandSender sender, String target, String key) {
		if(target.equals("global")) {
			String result = null;
			result = GlobalConfig.handleConfigCommand(sender, key);
			if(result == null) result = NotificationConfig.handleConfigCommand(sender, key);
			if(result == null) result = ScreenshotConfig.handleConfigCommand(sender, key);
			if(result != null) {
				Utils.sendLocalizedMessage(sender, "irc.config.lookup", "Global", key, result);
			} else {
				Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", "Global", key);
			}
		} else {
			ChannelConfig channelConfig = IRCResolver.resolveChannelConfig(target, IRCResolver.FLAGS_NONE);
			if(channelConfig != null) {
				channelConfig.handleConfigCommand(sender, key);
			} else {
				ServerConfig serverConfig = IRCResolver.resolveServerConfig(target, IRCResolver.FLAGS_NONE);
				if(serverConfig != null) {
					serverConfig.handleConfigCommand(sender, key);
				} else {
					Utils.sendLocalizedMessage(sender, "irc.target.targetNotFound", target);
				}
			}
		}
	}

	public static ServerConfig getDefaultServerConfig() {
		Iterator<ServerConfig> it = serverConfigs.values().iterator();
		if(it.hasNext()) {
			return it.next();
		}
		return null;
	}

	public static void addOptionsToList(List<String> list) {
		GlobalConfig.addOptionsToList(list);
		NotificationConfig.addOptionsToList(list);
		ScreenshotConfig.addOptionsToList(list);
	}

	public static void addValuesToList(List<String> list, String option) {
		GlobalConfig.addValuesToList(list, option);
		NotificationConfig.addValuesToList(list, option);
		ScreenshotConfig.addValuesToList(list, option);
	}

	public static BotProfileImpl getBotProfile(String name) {
		BotProfileImpl botProfile = botProfiles.get(name);
		if(botProfile == null) {
			return defaultBotProfile;
		}
		return botProfile;
	}
	
	public static List<BotProfileImpl> getBotProfiles() {
		return botProfileList;
	}

	public static DisplayFormatConfig getDisplayFormat(String displayMode) {
		DisplayFormatConfig displayFormat = displayFormats.get(displayMode);
		if(displayFormat == null) {
			return defaultDisplayFormat;
		}
		return displayFormat;
	}

	public static File getBotProfileDir() {
		return botProfileDir;
	}
	
	public static List<DisplayFormatConfig> getDisplayFormats() {
		return displayFormatList;
	}

	public static void addBotProfile(BotProfileImpl botProfile) {
		botProfiles.put(botProfile.getName(), botProfile);
		botProfileList.add(botProfile);
	}
	
	public static void renameBotProfile(BotProfileImpl botProfile, String newName) {
		botProfiles.remove(botProfile.getName());
		botProfile.setName(newName);
		botProfiles.put(botProfile.getName(), botProfile);
	}

	public static void removeBotProfile(BotProfileImpl botProfile) {
		botProfiles.remove(botProfile.getName());
		botProfileList.remove(botProfile);
		botProfile.getFile().delete();
	}

	public static BotProfileImpl getDefaultBotProfile() {
		return defaultBotProfile;
	}

	public static void reload() {
		load(configFile);
	}

}
