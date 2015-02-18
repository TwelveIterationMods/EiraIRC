// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config.base;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.blay09.mods.eirairc.api.bot.BotProfile;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.bot.BotCommandAlias;
import net.blay09.mods.eirairc.bot.BotCommandCustom;
import net.blay09.mods.eirairc.bot.BotCommandHelp;
import net.blay09.mods.eirairc.bot.BotCommandMessage;
import net.blay09.mods.eirairc.bot.BotCommandOp;
import net.blay09.mods.eirairc.bot.BotCommandWho;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class BotProfileImpl implements BotProfile {

	private static final String CATEGORY_SETTINGS = "settings";
	private static final String CATEGORY_COMMANDS = "commands";

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
	private String[] disabledNativeCommands;
	private String[] disabledInterOpCommands;
	private boolean isDefaultProfile;
	
	public BotProfileImpl(File file) {
		config = new Configuration(file);
		this.file = file;
		name = file.getName().substring(0, file.getName().length() - 4);
		load();
	}
	
	private void load() {
		name = Utils.unquote(config.get(CATEGORY_SETTINGS, "name", name).getString());
		isDefaultProfile = config.get(CATEGORY_SETTINGS, "isDefaultProfile", false).getBoolean(false);

		disabledNativeCommands = config.get(CATEGORY_COMMANDS, "disabledNativeCommands", new String[0]).getStringList();
		disabledInterOpCommands = config.get(CATEGORY_COMMANDS, "disabledInterOpCommands", new String[0]).getStringList();
		
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
	
	public String getName() {
		return name;
	}

	public void save() {
		config.save();
	}

	public void defaultClient() {
		config.get(CATEGORY_SETTINGS, "name", "").set("Client");
		config.get(CATEGORY_SETTINGS, "isDefaultProfile", true).set(true);

		config.get(CATEGORY_COMMANDS, "disabledNativeCommands", new String[0]).set(new String[] {
			Utils.quote("*")
		});
	}
	
	public void defaultServer() {
		config.get(CATEGORY_SETTINGS, "name", "").set("Server");
		config.get(CATEGORY_SETTINGS, "isDefaultProfile", true).set(true);
	}
	
	public void defaultTwitch() {
		config.get(CATEGORY_SETTINGS, "name", "").set("Twitch");
		config.get(CATEGORY_SETTINGS, "isDefaultProfile", true).set(true);
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
	public boolean isInterOpAuth(String authName) {
		return interOpAuthList.contains(authName);
	}

	public void setName(String name) {
		this.name = name;
		config.get(CATEGORY_SETTINGS, "name", "").set(name);
	}
	
	public String[] getInterOpBlacklist() {
		return disabledInterOpCommands;
	}
	
	public File getFile() {
		return file;
	}

	public Collection<IBotCommand> getCommands() {
		return commands.values();
	}

}
