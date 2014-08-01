// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.blay09.mods.eirairc.api.bot.BotProfile;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.bot.BotCommandAlias;
import net.blay09.mods.eirairc.bot.BotCommandAuth;
import net.blay09.mods.eirairc.bot.BotCommandCustom;
import net.blay09.mods.eirairc.bot.BotCommandHelp;
import net.blay09.mods.eirairc.bot.BotCommandMessage;
import net.blay09.mods.eirairc.bot.BotCommandOp;
import net.blay09.mods.eirairc.bot.BotCommandWho;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import com.google.common.io.Files;

public class BotProfileImpl implements BotProfile {

	private static final String CATEGORY_SETTINGS = "settings";
	private static final String CATEGORY_COMMANDS = "commands";
	private static final String CATEGORY_MACROS = "macros";
	
	private static final String DEFAULT_CLIENT_FILE = "default_client";
	private static final String DEFAULT_SERVER_FILE = "default_server";
	private static final String DEFAULT_TWITCH_FILE = "default_twitch";
	
	public static final String DEFAULT_TWITCH = "Twitch";
	public static final String DEFAULT_SERVER = "Server";
	public static final String DEFAULT_CLIENT = "Client";
	
	private final Map<String, IBotCommand> commands = new HashMap<String, IBotCommand>();
	public final List<String> interOpAuthList = new ArrayList<String>();
	private final File file;
	private final Configuration config;
	
	private String name;
	private boolean muted;
	private boolean readOnly;
	private String displayFormat;
	private String[] disabledNativeCommands;
	private String[] disabledInterOpCommands;
	private boolean interOp;
	private boolean isDefaultProfile;
	
	public BotProfileImpl(File dir, String name) {
		File file = findFreeFile(dir, name);
		config = new Configuration(file);
		this.file = file;
		this.name = file.getName().substring(0, file.getName().length() - 4);
		load();
	}
	
	public BotProfileImpl(File file) {
		config = new Configuration(file);
		this.file = file;
		name = file.getName().substring(0, file.getName().length() - 4);
		load();
	}
	
	public BotProfileImpl(BotProfileImpl copy, String name) {
		file = findFreeFile(copy.getFile().getParentFile(), name);
		try {
			Files.copy(copy.getFile(), file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		config = new Configuration(file);
		config.get(CATEGORY_SETTINGS, "isDefaultProfile", false).set(false);
		setName(file.getName().substring(0, file.getName().length() - 4));
		load();
	}
	
	private void load() {
		name = Utils.unquote(config.get(CATEGORY_SETTINGS, "name", name).getString());
		muted = config.get(CATEGORY_SETTINGS, "muted", false).getBoolean(false);
		readOnly = config.get(CATEGORY_SETTINGS, "readOnly", false).getBoolean(false);
		isDefaultProfile = config.get(CATEGORY_SETTINGS, "isDefaultProfile", false).getBoolean(false);
		displayFormat = Utils.unquote(config.get(CATEGORY_SETTINGS, "displayFormat", "S-Light").getString());
		
		disabledNativeCommands = config.get(CATEGORY_COMMANDS, "disabledNativeCommands", new String[0]).getStringList();
		disabledInterOpCommands = config.get(CATEGORY_COMMANDS, "disabledInterOpCommands", new String[0]).getStringList();
		
		interOp = config.get(CATEGORY_COMMANDS, "interOp", false).getBoolean(false);
		String[] interOpAuthListArray = config.get(CATEGORY_COMMANDS, "interOpAuthList", new String[0]).getStringList();
		for(int i = 0; i < interOpAuthListArray.length; i++) {
			interOpAuthList.add(interOpAuthListArray[i]);
		}
	}

	public boolean isDefaultProfile() {
		return isDefaultProfile;
	}
	
	public void registerCommand(IBotCommand command) {
		commands.put(command.getCommandName().toLowerCase(), command);
	}
	
	public void loadCommands() {
		commands.clear();
		registerCommand(new BotCommandAlias());
		registerCommand(new BotCommandHelp());
		registerCommand(new BotCommandHelp());
		registerCommand(new BotCommandMessage());
		registerCommand(new BotCommandWho("who"));
		registerCommand(new BotCommandWho("players"));
		registerCommand(new BotCommandOp());
		
		for(int i = 0; i < disabledNativeCommands.length; i++) {
			if(Utils.unquote(disabledNativeCommands[i]).equals("*")) {
				commands.clear();
				break;
			}
			commands.remove(Utils.unquote(disabledNativeCommands[i]));
		}
		
		registerCommand(new BotCommandAuth());
		
		ConfigCategory configCategory = config.getCategory(CATEGORY_COMMANDS);
		for(ConfigCategory subCategory : configCategory.getChildren()) {
			String commandName = Utils.unquote(config.get(subCategory.getQualifiedName(), "name", "").getString());
			String command = Utils.unquote(config.get(subCategory.getQualifiedName(), "command", "").getString());
			String description = Utils.unquote(config.get(subCategory.getQualifiedName(), "description", "[Custom Command: Missing Description]").getString());
			boolean allowArgs = config.get(subCategory.getQualifiedName(), "allowArgs", false).getBoolean(false);
			boolean runAsOp = config.get(subCategory.getQualifiedName(), "runAsOp", false).getBoolean(false);
			boolean broadcastResult = config.get(subCategory.getQualifiedName(), "broadcastResult", false).getBoolean(false);
			boolean requireAuth = config.get(subCategory.getQualifiedName(), "requireAuth", runAsOp).getBoolean(runAsOp);
			registerCommand(new BotCommandCustom(subCategory.getQualifiedName(), commandName, command, description, allowArgs, broadcastResult, runAsOp, requireAuth));
		}
	}
	
	@Override
	public boolean getBoolean(String key, boolean defaultVal) {
		return config.get(CATEGORY_SETTINGS, key, defaultVal).getBoolean(defaultVal);
	}
	
	public void setBoolean(String key, boolean value) {
		config.get(CATEGORY_SETTINGS, key, value).set(value);
	}

	@Override
	public boolean isMuted() {
		return muted;
	}
	
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	public String getName() {
		return name;
	}

	public void save() {
		config.save();
	}

	public void defaultClient() {
		config.get(CATEGORY_SETTINGS, "name", "").set("Client");
		config.get(CATEGORY_SETTINGS, "isDefaultProfile", true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_ALLOWPRIVMSG, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_AUTOPLAYERS, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_RELAYIRCJOINLEAVE, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYNICKCHANGES, true).set(true);
		
		config.get(CATEGORY_COMMANDS, "disabledNativeCommands", new String[0]).set(new String[] {
			Utils.quote("*")
		});
	}
	
	public void defaultServer() {
		config.get(CATEGORY_SETTINGS, "name", "").set("Server");
		config.get(CATEGORY_SETTINGS, "isDefaultProfile", true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_ALLOWPRIVMSG, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_AUTOPLAYERS, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYIRCJOINLEAVE, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYNICKCHANGES, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYBROADCASTS, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYDEATHMESSAGES, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYMCJOINLEAVE, true).set(true);
	}
	
	public void defaultTwitch() {
		config.get(CATEGORY_SETTINGS, "name", "").set("Twitch");
		config.get(CATEGORY_SETTINGS, "isDefaultProfile", true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_ALLOWPRIVMSG, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_AUTOPLAYERS, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_RELAYIRCJOINLEAVE, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_RELAYNICKCHANGES, false).set(false);
		config.get(CATEGORY_SETTINGS, "displayFormat", "").set("Twitch");

		config.get(CATEGORY_COMMANDS, "disabledNativeCommands", new String[0]).set(new String[] {
				Utils.quote("*")
			});
	}

	public IBotCommand getCommand(String commandName) {
		return commands.get(commandName.toLowerCase());
	}

	public static void setupDefaultProfiles(File profileDir) {
		File file = new File(profileDir, BotProfileImpl.DEFAULT_CLIENT_FILE + ".cfg");
		if(!file.exists()) {
			BotProfileImpl botProfile = new BotProfileImpl(file);
			botProfile.defaultClient();
			botProfile.save();
			botProfile.loadCommands();
		}
		file = new File(profileDir, BotProfileImpl.DEFAULT_SERVER_FILE + ".cfg");
		if(!file.exists()) {
			BotProfileImpl botProfile = new BotProfileImpl(file);
			botProfile.defaultServer();
			botProfile.save();
			botProfile.loadCommands();
		}
		file = new File(profileDir, BotProfileImpl.DEFAULT_TWITCH_FILE + ".cfg");
		if(!file.exists()) {
			BotProfileImpl botProfile = new BotProfileImpl(file);
			botProfile.defaultTwitch();
			botProfile.save();
			botProfile.loadCommands();
		}
	}

	@Override
	public String getDisplayFormat() {
		return displayFormat;
	}

	@Override
	public boolean isInterOpAuth(String authName) {
		return interOpAuthList.contains(authName);
	}

	@Override
	public boolean isInterOp() {
		return interOp;
	}

	public void setName(String name) {
		this.name = name;
		config.get(CATEGORY_SETTINGS, "name", "").set(name);
	}
	
	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
		config.get(CATEGORY_SETTINGS, "displayFormat", "").set(displayFormat);
	}
	
	public String[] getInterOpBlacklist() {
		return disabledInterOpCommands;
	}
	
	public File getFile() {
		return file;
	}

	private static File findFreeFile(File dir, String name) {
		File file = new File(dir, name + ".cfg");
		int i = 1;
		while(file.exists()) {
			i++;
			file = new File(dir, name + "_" + i + ".cfg");
		}
		return file;
	}

	public Collection<IBotCommand> getCommands() {
		return commands.values();
	}

	public void deleteCustomCommand(BotCommandCustom botCommand) {
		commands.remove(botCommand.getCommandName().toLowerCase());
		if(botCommand.getCategoryName() != null) {
			config.removeCategory(config.getCategory(botCommand.getCategoryName()));
		}
		save();
	}

	public void addCustomCommand(BotCommandCustom botCommand) {
		String categoryName = botCommand.getCategoryName();
		if(categoryName == null) {
			categoryName = CATEGORY_COMMANDS + Configuration.CATEGORY_SPLITTER + Configuration.allowedProperties.retainFrom(botCommand.getCommandName()).replace('.', '_');
		}
		config.get(categoryName, "name", "").set(botCommand.getCommandName());
		config.get(categoryName, "command", "").set(botCommand.getMinecraftCommand());
		config.get(categoryName, "allowArgs", "").set(botCommand.allowsArgs());
		config.get(categoryName, "runAsOp", "").set(botCommand.isRunAsOp());
		config.get(categoryName, "broadcastResult", "").set(botCommand.isBroadcastResult());
		config.get(categoryName, "requireAuth", "").set(botCommand.requiresAuth());
		registerCommand(botCommand);
		save();
	}

	public void setDisabledNativeCommands(String[] disabledCommands) {
		this.disabledNativeCommands = disabledCommands;
		config.get(CATEGORY_COMMANDS, "disabledNativeCommands", new String[0]).set(disabledCommands);
	}

	public String[] getDisabledNativeCommands() {
		return disabledNativeCommands;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
