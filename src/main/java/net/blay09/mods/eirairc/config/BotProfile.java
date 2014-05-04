// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IBotProfile;
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

public class BotProfile implements IBotProfile {

	private static final String CATEGORY_SETTINGS = "settings";
	private static final String CATEGORY_COMMANDS = "commands";
	private static final String CATEGORY_MACROS = "macros";
	
	public static final String DEFAULT_CLIENT = "default_client";
	public static final String DEFAULT_SERVER = "default_server";
	public static final String DEFAULT_TWITCH = "default_twitch";
	
	private final Map<String, IBotCommand> commands = new HashMap<String, IBotCommand>();
	private final Configuration config;
	
	private String name;
	private boolean muted;
	private boolean readOnly;
	private String displayFormat;
	private String[] disabledNativeCommands;
	
	
	public BotProfile(File file) {
		config = new Configuration(file);
		name = Utils.unquote(config.get(CATEGORY_SETTINGS, "name", file.getName().substring(0, file.getName().length() - 4)).getString());
		muted = config.get(CATEGORY_SETTINGS, "muted", false).getBoolean(false);
		readOnly = config.get(CATEGORY_SETTINGS, "readOnly", false).getBoolean(false);
		displayFormat = Utils.unquote(config.get(CATEGORY_SETTINGS, "displayFormat", "S-Light").getString());
		
		disabledNativeCommands = config.get(CATEGORY_COMMANDS, "disabledNativeCommands", new String[0]).getStringList();
	}

	public void registerCommand(IBotCommand command) {
		commands.put(command.getCommandName(), command);
	}
	
	public void loadCommands() {
		registerCommand(new BotCommandAlias());
		registerCommand(new BotCommandAuth());
		registerCommand(new BotCommandHelp());
		registerCommand(new BotCommandOp());
		registerCommand(new BotCommandHelp());
		registerCommand(new BotCommandMessage());
		registerCommand(new BotCommandWho("who"));
		registerCommand(new BotCommandWho("players"));
		
		for(int i = 0; i < disabledNativeCommands.length; i++) {
			if(Utils.unquote(disabledNativeCommands[i]).equals("*")) {
				commands.clear();
				break;
			}
			commands.remove(Utils.unquote(disabledNativeCommands[i]));
		}
		
		ConfigCategory configCategory = config.getCategory(CATEGORY_COMMANDS);
		for(ConfigCategory subCategory : configCategory.getChildren()) {
			String commandName = Utils.unquote(config.get(subCategory.getQualifiedName(), "name", "").getString());
			String command = Utils.unquote(config.get(subCategory.getQualifiedName(), "command", "").getString());
			boolean allowArgs = config.get(subCategory.getQualifiedName(), "allowArgs", false).getBoolean(false);
			boolean runAsOp = config.get(subCategory.getQualifiedName(), "runAsOp", false).getBoolean(false);
			boolean broadcastResult = config.get(subCategory.getQualifiedName(), "broadcastResult", false).getBoolean(false);
			registerCommand(new BotCommandCustom(commandName, command, allowArgs, broadcastResult, runAsOp));
		}
	}
	
	@Override
	public boolean getBoolean(String key, boolean defaultVal) {
		return config.get(CATEGORY_SETTINGS, key, defaultVal).getBoolean(defaultVal);
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
		config.get(CATEGORY_SETTINGS, KEY_ALLOWPRIVMSG, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_AUTOWHO, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_RELAYIRCJOINLEAVE, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYNICKCHANGES, true).set(true);
		
		config.get(CATEGORY_COMMANDS, "disabledNativeCommands", new String[0]).set(new String[] {
			"*"
		});
	}
	
	public void defaultServer() {
		config.get(CATEGORY_SETTINGS, KEY_ALLOWPRIVMSG, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_AUTOWHO, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYIRCJOINLEAVE, true).set(true);
		config.get(CATEGORY_SETTINGS, KEY_RELAYNICKCHANGES, true).set(true);
	}
	
	public void defaultTwitch() {
		config.get(CATEGORY_SETTINGS, KEY_ALLOWPRIVMSG, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_AUTOWHO, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_RELAYIRCJOINLEAVE, false).set(false);
		config.get(CATEGORY_SETTINGS, KEY_RELAYNICKCHANGES, false).set(false);
		
		config.get(CATEGORY_COMMANDS, "disabledNativeCommands", new String[0]).set(new String[] {
				"*"
			});
	}

	public IBotCommand getCommand(String commandName) {
		return commands.get(commandName);
	}

	public static void setupDefaultProfiles(File profileDir) {
		File file = new File(profileDir, BotProfile.DEFAULT_CLIENT + ".cfg");
		if(!file.exists()) {
			BotProfile botProfile = new BotProfile(file);
			botProfile.defaultClient();
			botProfile.save();
			botProfile.loadCommands();
		}
		file = new File(profileDir, BotProfile.DEFAULT_SERVER + ".cfg");
		if(!file.exists()) {
			BotProfile botProfile = new BotProfile(file);
			botProfile.defaultServer();
			botProfile.save();
			botProfile.loadCommands();
		}
		file = new File(profileDir, BotProfile.DEFAULT_TWITCH + ".cfg");
		if(!file.exists()) {
			BotProfile botProfile = new BotProfile(file);
			botProfile.defaultTwitch();
			botProfile.save();
			botProfile.loadCommands();
		}
	}

	@Override
	public String getDisplayFormat() {
		return displayFormat;
	}
}
