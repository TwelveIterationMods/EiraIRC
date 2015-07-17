// Copyright (c) 2015 Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.bot.BotCommandCustom;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.base.MessageFormatConfig;
import net.blay09.mods.eirairc.config.base.ServiceConfig;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ConfigurationHandler {

	private static final Logger logger = LogManager.getLogger();

	private static final Map<String, ServerConfig> serverConfigs = new HashMap<String, ServerConfig>();
	private static final Map<String, MessageFormatConfig> displayFormats = new HashMap<String, MessageFormatConfig>();
	private static final List<IBotCommand> customCommands = new ArrayList<IBotCommand>();
	private static final List<SuggestedChannel> suggestedChannels = new ArrayList<SuggestedChannel>();
	private static final Map<String, TrustedServer> trustedServers = new HashMap<String, TrustedServer>();
	public static final List<String> failedToLoad = new ArrayList<String>();

	private static File baseConfigDir;
	private static MessageFormatConfig defaultDisplayFormat;

	private static void loadDisplayFormats(File formatDir) {
		displayFormats.clear();
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
		for(File file : files) {
			MessageFormatConfig dfc = new MessageFormatConfig(file);
			dfc.loadFormats();
			displayFormats.put(dfc.getName(), dfc);
		}
		defaultDisplayFormat = displayFormats.get(MessageFormatConfig.DEFAULT_FORMAT);
	}

	private static void loadTrustedServers(File configDir) {
		trustedServers.clear();
		Gson gson = new Gson();
		try {
			Reader reader = new FileReader(new File(configDir, "trusted_servers.json"));
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.setLenient(true);
			JsonArray serverArray = gson.fromJson(jsonReader, JsonArray.class);
			for (int i = 0; i < serverArray.size(); i++) {
				addTrustedServer(TrustedServer.loadFromJson(serverArray.get(i).getAsJsonObject()));
			}
			reader.close();
		} catch (JsonSyntaxException e) {
			logger.error("Syntax error in trusted_servers.json: ", e);
			failedToLoad.add("trusted_servers.json");
		} catch (FileNotFoundException ignored) {
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

	public static void loadSuggestedChannels(IResourceManager resourceManager) throws IOException {
		suggestedChannels.clear();
		InputStream in;
		File overrideFile = new File(baseConfigDir, "eirairc/suggested-channels.json");
		if(overrideFile.exists()) {
			in = new FileInputStream(overrideFile);
		} else {
			URL remoteURL = new URL("https://raw.githubusercontent.com/blay09/EiraIRC/master/src/main/resources/assets/eirairc/suggested-channels.json");
			try {
				in = remoteURL.openStream();
			} catch (IOException e) {
				in = resourceManager.getResource(new ResourceLocation("eirairc", "suggested-channels.json")).getInputStream();
			}
		}
		Gson gson = new Gson();
		Reader reader = new InputStreamReader(in);
		JsonReader jsonReader = new JsonReader(reader);
		jsonReader.setLenient(true);
		try {
			JsonArray channelArray = gson.fromJson(jsonReader, JsonArray.class);
			for (int i = 0; i < channelArray.size(); i++) {
				suggestedChannels.add(SuggestedChannel.loadFromJson(channelArray.get(i).getAsJsonObject()));
			}
		} catch (JsonSyntaxException e) {
			logger.error("Syntax error in suggested-channels.json: ", e);
			failedToLoad.add("suggested-channels.json");
		}
		reader.close();
		in.close();
	}

	private static void loadCommands(File configDir) {
		customCommands.clear();
		if(!configDir.exists()) {
			if(!configDir.mkdirs()) {
				return;
			}
		}
		createExampleCommands();
		Gson gson = new Gson();
		try {
			File file = new File(configDir, "commands.json");
			if(!file.exists()) {
				JsonArray root = new JsonArray();
				JsonObject players = new JsonObject();
				players.addProperty("name", "players");
				players.addProperty("override", "who");
				players.addProperty("description", "Default alias players for the who command.");
				root.add(players);
				try {
					JsonWriter writer = new JsonWriter(new FileWriter(new File(baseConfigDir, "eirairc/commands.json")));
					writer.setIndent("  ");
					gson.toJson(root, writer);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Reader reader = new FileReader(file);
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.setLenient(true);
			JsonArray commandArray = gson.fromJson(jsonReader, JsonArray.class);
			for(int i = 0; i < commandArray.size(); i++) {
				customCommands.add(BotCommandCustom.loadFromJson(commandArray.get(i).getAsJsonObject()));
			}
			reader.close();
		} catch (FileNotFoundException ignored) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			logger.error("Syntax error in commands.json: ", e);
			failedToLoad.add("commands.json");
		}
	}

	private static void createExampleCommands() {
		JsonArray root = new JsonArray();
		JsonObject tps = new JsonObject();
		tps.addProperty("name", "tps");
		tps.addProperty("command", "cofh tps");
		tps.addProperty("broadcastResult", true);
		tps.addProperty("runAsOp", true);
		tps.addProperty("requireAuth", false);
		tps.addProperty("allowArgs", false);
		tps.addProperty("description", "Broadcasts the current TPS to the channel.");
		root.add(tps);
		JsonObject alias = new JsonObject();
		alias.addProperty("name", "players");
		alias.addProperty("override", "who");
		alias.addProperty("description", "An alias for the who command called players.");
		root.add(alias);
		JsonObject override = new JsonObject();
		override.addProperty("name", "help");
		override.addProperty("override", "help");
		override.addProperty("broadcastResult", true);
		override.addProperty("description", "Changes EiraIRCs help command to broadcast into the channel instead of a private tell.");
		root.add(override);
		JsonObject ban = new JsonObject();
		ban.addProperty("name", "ban");
		ban.addProperty("command", "ban");
		ban.addProperty("broadcastResult", false);
		ban.addProperty("runAsOp", true);
		ban.addProperty("requireAuth", true);
		ban.addProperty("allowArgs", true);
		ban.addProperty("description", "Bans the specified player with an optional reason from the server. /ban <name> [reason ...]. Authed only.");
		root.add(ban);
		JsonObject banip = new JsonObject();
		banip.addProperty("name", "ban-ip");
		banip.addProperty("command", "ban-ip");
		banip.addProperty("broadcastResult", false);
		banip.addProperty("runAsOp", true);
		banip.addProperty("requireAuth", true);
		banip.addProperty("allowArgs", true);
		banip.addProperty("description", "Bans the specified IP address with an optional reason from the server. /ban <address|name> [reason ...]. Authed only.");
		root.add(banip);
		JsonObject banlist = new JsonObject();
		banlist.addProperty("name", "banlist");
		banlist.addProperty("command", "banlist");
		banlist.addProperty("broadcastResult", false);
		banlist.addProperty("runAsOp", true);
		banlist.addProperty("requireAuth", true);
		banlist.addProperty("allowArgs", true);
		banlist.addProperty("description", "List IP addresses and names on the server. /banlist [ips|players]. Authed only.");
		root.add(banlist);
		JsonObject pardon = new JsonObject();
		pardon.addProperty("name", "pardon");
		pardon.addProperty("command", "pardon");
		pardon.addProperty("broadcastResult", false);
		pardon.addProperty("runAsOp", true);
		pardon.addProperty("requireAuth", true);
		pardon.addProperty("allowArgs", true);
		pardon.addProperty("description", "Unbans (pardons) the specified player on the server. /pardon <name>. Authed only.");
		root.add(pardon);
		JsonObject pardonip = new JsonObject();
		pardonip.addProperty("name", "pardon-ip");
		pardonip.addProperty("command", "pardon-ip");
		pardonip.addProperty("broadcastResult", false);
		pardonip.addProperty("runAsOp", true);
		pardonip.addProperty("requireAuth", true);
		pardonip.addProperty("allowArgs", true);
		pardonip.addProperty("description", "Unbans (pardons) the specified IP address on the server. /pardon-ip <address>. Authed only.");
		root.add(pardonip);
		Gson gson = new Gson();
		try {
			JsonWriter writer = new JsonWriter(new FileWriter(new File(baseConfigDir, "eirairc/commands.json.example.txt")));
			writer.setIndent("  ");
			gson.toJson(root, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadServers(File configDir) {
		serverConfigs.clear();
		if(!configDir.exists()) {
			if(!configDir.mkdirs()) {
				return;
			}
		}
		createExampleServers();
		Gson gson = new Gson();
		try {
			Reader reader = new FileReader(new File(configDir, "servers.json"));
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.setLenient(true);
			JsonArray serverArray = gson.fromJson(jsonReader, JsonArray.class);
			for (int i = 0; i < serverArray.size(); i++) {
				addServerConfig(ServerConfig.loadFromJson(serverArray.get(i).getAsJsonObject()));
			}
			reader.close();
		} catch (JsonSyntaxException e) {
			logger.error("Syntax error in servers.json: ", e);
			failedToLoad.add("servers.json");
		} catch (FileNotFoundException ignored) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createExampleServers() {
		JsonArray root = new JsonArray();
		JsonObject server = new JsonObject();
		server.addProperty("address", "irc.esper.net");
		server.addProperty("serverPassword", "");
		server.addProperty("nick", "%USERNAME%");
		server.addProperty("charset", "UTF-8");
		server.addProperty("isRedirect", false);
		server.addProperty("isSSL", false);
		JsonObject nickserv = new JsonObject();
		nickserv.addProperty("username", "");
		nickserv.addProperty("password", "");
		server.add("nickserv", nickserv);
		JsonArray channelArray = new JsonArray();
		JsonObject channel1 = new JsonObject();
		channel1.addProperty("name", "#EiraIRC");
		channelArray.add(channel1);
		JsonObject channel2 = new JsonObject();
		channel2.addProperty("name", "#minecraft");
		channel2.addProperty("password", "");
		channelArray.add(channel2);
		server.add("channels", channelArray);
		JsonObject botSettings = new JsonObject();
		botSettings.addProperty("botProfile", "Server");
		botSettings.addProperty("relayDeathMessages", false);
		botSettings.addProperty("(more)", "(see shared.cfg for more options)");
		server.add("bot", botSettings);
		JsonObject genSettings = new JsonObject();
		genSettings.addProperty("autoJoin", true);
		genSettings.addProperty("(more)", "(see shared.cfg for more options)");
		server.add("settings", genSettings);
		JsonObject themeSettings = new JsonObject();
		themeSettings.addProperty("ircNameColor", "c");
		themeSettings.addProperty("(more)", "(see shared.cfg for more options)");
		root.add(server);
		Gson gson = new Gson();
		try {
			JsonWriter writer = new JsonWriter(new FileWriter(new File(baseConfigDir, "eirairc/servers.json.example.txt")));
			writer.setIndent("  ");
			gson.toJson(root, writer);
			writer.close();
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
		loadDisplayFormats(new File(configDir, "formats"));

		File legacyConfigFile = new File(baseConfigDir, "eirairc.cfg");
		if(legacyConfigFile.exists()) {
			Configuration legacyConfig = new Configuration(legacyConfigFile);
			loadLegacy(configDir, legacyConfig);
			if(!legacyConfigFile.renameTo(new File(baseConfigDir, "eirairc.cfg.old"))) {
				logger.error("Couldn't get rid of old 'eirairc.cfg' file. Config will REGENERATE unless you delete it yourself.");
			}
		} else {
			EiraIRC.proxy.loadConfig(configDir, true);
		}

		loadCommands(configDir);
		loadServers(configDir);
		loadTrustedServers(configDir);

		AuthManager.load(new File("."));
		IgnoreList.load(configDir);
	}
	
	public static void save() {
		EiraIRC.proxy.saveConfig();

		saveServers();
		saveTrustedServers();
	}

	public static void reloadAll() {
		failedToLoad.clear();
		load(baseConfigDir);
		for(IRCConnection connection : EiraIRC.instance.getConnectionManager().getConnections()) {
			((IRCBotImpl) connection.getBot()).reloadCommands();
		}
	}

	public static void lightReload() {
		File configDir = new File(baseConfigDir, "eirairc");

		EiraIRC.proxy.loadConfig(configDir, false);
	}
	
	public static ServerConfig getOrCreateServerConfig(String host) {
		ServerConfig serverConfig = serverConfigs.get(host.toLowerCase());
		if(serverConfig == null) {
			serverConfig = new ServerConfig(host);
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

	public static ServerConfig removeServerConfig(String host) {
		return serverConfigs.remove(host.toLowerCase());
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
			ChannelConfig channelConfig = ConfigHelper.resolveChannelConfig(target);
			if(channelConfig != null) {
				channelConfig.handleConfigCommand(sender, key, value);
			} else {
				ServerConfig serverConfig = ConfigHelper.resolveServerConfig(target);
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
			ChannelConfig channelConfig = ConfigHelper.resolveChannelConfig(target);
			if(channelConfig != null) {
				channelConfig.handleConfigCommand(sender, key);
			} else {
				ServerConfig serverConfig = ConfigHelper.resolveServerConfig(target);
				if(serverConfig != null) {
					serverConfig.handleConfigCommand(sender, key);
				} else {
					Utils.sendLocalizedMessage(sender, "irc.target.targetNotFound", target);
				}
			}
		}
		save();
	}

	public static void addOptionsToList(List<String> list, String option) {
		EiraIRC.proxy.addConfigOptionsToList(list, option);
	}

	public static MessageFormatConfig getMessageFormat(String displayMode) {
		MessageFormatConfig displayFormat = displayFormats.get(displayMode);
		if(displayFormat == null) {
			return defaultDisplayFormat;
		}
		return displayFormat;
	}

	public static List<IBotCommand> getCustomCommands() {
		return customCommands;
	}

	public static List<SuggestedChannel> getSuggestedChannels() {
		return suggestedChannels;
	}

	public static String[] getAvailableMessageFormats() {
		return displayFormats.keySet().toArray(new String[displayFormats.size()]);
	}
}
