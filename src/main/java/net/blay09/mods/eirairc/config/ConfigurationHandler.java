// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonWriter;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.config.base.BotProfileImpl;
import net.blay09.mods.eirairc.config.base.MessageFormatConfig;
import net.blay09.mods.eirairc.config.base.ServiceConfig;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationHandler {

	private static final Logger logger = LogManager.getLogger();

	private static final Map<String, ServerConfig> serverConfigs = new HashMap<String, ServerConfig>();
	private static final Map<String, BotProfileImpl> botProfiles = new HashMap<String, BotProfileImpl>();
	private static final List<BotProfileImpl> botProfileList = new ArrayList<BotProfileImpl>();
	private static final Map<String, MessageFormatConfig> displayFormats = new HashMap<String, MessageFormatConfig>();
	private static final List<MessageFormatConfig> displayFormatList = new ArrayList<MessageFormatConfig>();
	private static final Map<String, TrustedServer> trustedServers = new HashMap<String, TrustedServer>();

	private static File baseConfigDir;
	private static BotProfileImpl defaultBotProfile;
	private static MessageFormatConfig defaultDisplayFormat;

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

	private static void loadBotProfiles(File profileDir) {
		botProfiles.clear();
		botProfileList.clear();
		if(!profileDir.exists()) {
			if(!profileDir.mkdirs()) {
				return;
			}
		}
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

	private static void loadDisplayFormats(File formatDir) {
		displayFormats.clear();
		displayFormatList.clear();
		if(!formatDir.exists()) {
			if(!formatDir.mkdirs()) {
				return;
			}
		}
		MessageFormatConfig.setupDefaultFormats(formatDir);
		File[] files = formatDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith(".cfg");
			}
		});
		for(int i = 0; i < files.length; i++) {
			MessageFormatConfig dfc = new MessageFormatConfig(files[i]);
			dfc.loadFormats();
			displayFormats.put(dfc.getName(), dfc);
			displayFormatList.add(dfc);
		}
		defaultDisplayFormat = displayFormats.get(MessageFormatConfig.DEFAULT_FORMAT);
	}

	private static void loadTrustedServers(File configDir) {
		Gson gson = new Gson();
		try {
			Reader reader = new FileReader(new File(configDir, "trusted_servers.json"));
			JsonArray serverArray = gson.fromJson(reader, JsonArray.class);
			for(int i = 0; i < serverArray.size(); i++) {
				addTrustedServer(TrustedServer.loadFromJson(serverArray.get(i).getAsJsonObject()));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveTrustedServers() {
		Gson gson = new Gson();
		try {
			JsonArray serverArray = new JsonArray();
			for(TrustedServer trustedServer : trustedServers.values()) {
				serverArray.add(trustedServer.toJsonObject());
			}
			JsonWriter writer = new JsonWriter(new FileWriter(new File(baseConfigDir, "eirairc/trusted_servers.json")));
			writer.setIndent("  ");
			gson.toJson(serverArray, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadServices(File configDir) {
		if(!configDir.exists()) {
			if(!configDir.mkdirs()) {
				return;
			}
		}
		Configuration serviceConfig = new Configuration(new File(configDir, "services.cfg"));
		ServiceConfig.setupDefaultServices(serviceConfig);
		ServiceConfig.load(serviceConfig);
	}

	private static void loadServers(File configDir) {
		if(!configDir.exists()) {
			if(!configDir.mkdirs()) {
				return;
			}
		}
		Gson gson = new Gson();
		try {
			Reader reader = new FileReader(new File(configDir, "servers.json"));
			JsonArray serverArray = gson.fromJson(reader, JsonArray.class);
			for(int i = 0; i < serverArray.size(); i++) {
				addServerConfig(ServerConfig.loadFromJson(serverArray.get(i).getAsJsonObject()));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveServers() {
		Gson gson = new Gson();
		try {
			JsonArray serverArray = new JsonArray();
			for(ServerConfig serverConfig : serverConfigs.values()) {
				serverArray.add(serverConfig.toJsonObject());
			}
			JsonWriter writer = new JsonWriter(new FileWriter(new File(baseConfigDir, "eirairc/servers.json")));
			writer.setIndent("  ");
			gson.toJson(serverArray, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadLegacy(File configDir, Configuration legacyConfig) {
		EiraIRC.proxy.loadLegacyConfig(configDir, legacyConfig);

		ConfigCategory serversCategory = legacyConfig.getCategory("servers");
		for(ConfigCategory serverCategory : serversCategory.getChildren()) {
			ServerConfig serverConfig = new ServerConfig(Utils.unquote(legacyConfig.get(serverCategory.getQualifiedName(), "host", "").getString()));
			serverConfig.loadLegacy(legacyConfig, serverCategory);
			addServerConfig(serverConfig);
		}

		save();
	}

	public static void load(File baseConfigDir) {
		ConfigurationHandler.baseConfigDir = baseConfigDir;
		File configDir = new File(baseConfigDir, "eirairc");

		loadServices(configDir);

		File legacyConfigFile = new File(baseConfigDir, "eirairc.cfg");
		if(legacyConfigFile.exists()) {
			Configuration legacyConfig = new Configuration(legacyConfigFile);
			loadLegacy(configDir, legacyConfig);
			if(!legacyConfigFile.renameTo(new File(baseConfigDir, "eirairc.cfg.old"))) {
				logger.error("Couldn't get rid of old 'eirairc.cfg' file. Config will REGENERATE unless you delete it yourself.");
			}
		} else {
			EiraIRC.proxy.loadConfig(configDir);
		}

		loadDisplayFormats(new File(configDir, "formats"));
		loadBotProfiles(new File(configDir, "bots"));

		loadServers(configDir);
		loadTrustedServers(configDir);
	}
	
	public static void save() {
		SharedGlobalConfig.save();
		ClientGlobalConfig.save();

		saveServers();
		saveTrustedServers();
	}

	public static void reload() {
		load(baseConfigDir);
	}

	public static void lightReload() {
		File configDir = new File(baseConfigDir, "eirairc");

		SharedGlobalConfig.load(configDir);
		ClientGlobalConfig.load(configDir);
	}
	
	public static ServerConfig getOrCreateServerConfig(String host) {
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

	public static ServerConfig getServerConfig(String address) {
		return serverConfigs.get(address.toLowerCase());
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

	public static void addTrustedServer(TrustedServer server) {
		trustedServers.put(server.getAddress(), server);
	}

	public static TrustedServer getOrCreateTrustedServer(String address) {
		TrustedServer server = trustedServers.get(address.toLowerCase());
		if(server == null) {
			server = new TrustedServer(address);
		}
		return server;
	}

	public static void handleConfigCommand(ICommandSender sender, String target, String key, String value) {
		if(target.equals("global")) {
			boolean result = EiraIRC.proxy.handleConfigCommand(sender, key, value);
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
			String result = EiraIRC.proxy.handleConfigCommand(sender, key);
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

	public static void addOptionsToList(List<String> list, String option) {
		EiraIRC.proxy.addConfigOptionsToList(list, option);
	}

	public static BotProfileImpl getBotProfile(String name) {
		BotProfileImpl botProfile = botProfiles.get(name);
		if(botProfile == null) {
			return defaultBotProfile;
		}
		return botProfile;
	}

	public static MessageFormatConfig getMessageFormat(String displayMode) {
		MessageFormatConfig displayFormat = displayFormats.get(displayMode);
		if(displayFormat == null) {
			return defaultDisplayFormat;
		}
		return displayFormat;
	}

}
