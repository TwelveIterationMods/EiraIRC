// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config2;

import java.util.List;

import net.blay09.mods.eirairc.config2.base.BotProfileImpl;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ChannelConfig {

	private final ServerConfig serverConfig;
	private final String name;
	private String password;
	private boolean autoWho; // server
	private boolean autoJoin = true; // server
	private String botProfile; // bot
	
	public ChannelConfig(ServerConfig serverConfig, String name) {
		this.serverConfig = serverConfig;
		if(serverConfig.getAddress().equals(Globals.TWITCH_SERVER)) {
			this.name = name.toLowerCase();
		} else {
			this.name = name;
		}
	}

	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setAutoJoin(boolean autoJoin) {
		this.autoJoin = autoJoin;
	}
	
	public boolean isAutoJoin() {
		return autoJoin;
	}

	public void load(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		password = Utils.unquote(config.get(categoryName, "password", "").getString());
		autoJoin = config.get(categoryName, "autoJoin", autoJoin).getBoolean(autoJoin);
		autoWho = config.get(categoryName, "autoWho", autoWho).getBoolean(autoWho);
		botProfile = Utils.unquote(config.get(categoryName, "botProfile", "").getString());
	}

	public void save(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		config.get(categoryName, "name", "").set(Utils.quote(name));
		config.get(categoryName, "password", "").set(Utils.quote(password != null ? password : ""));
		config.get(categoryName, "autoJoin", autoJoin).set(autoJoin);
		config.get(categoryName, "autoWho", autoWho).set(autoWho);
		config.get(categoryName, "botProfile", "").set(Utils.quote(botProfile));
	}

	public void handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("autoJoin")) value = String.valueOf(autoJoin);
		else if(key.equals("autoWho")) value = String.valueOf(autoWho);
		if(value != null) {
			Utils.sendLocalizedMessage(sender, "irc.config.lookup", name, key, value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", name, key);
		}
	}
	
	public void handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("autoJoin")) {
			autoJoin = Boolean.parseBoolean(value);
		} else if(key.equals("autoWho")) {
			autoWho = Boolean.parseBoolean(value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", name, key, value);
			return;
		}
		Utils.sendLocalizedMessage(sender, "irc.config.change", name, key, value);
		ConfigurationHandler.save();
	}

	public static void addOptionsToList(List<String> list) {
		list.add("autoJoin");
		list.add("autoWho");
	}
	
	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAutoWho(boolean autoWho) {
		this.autoWho = autoWho;
	}
	
	public boolean isAutoWho() {
		return autoWho;
	}
	
	public static void addValuesToList(List<String> list, String option) {
		if(option.equals("autoJoin") || option.equals("autoWho")) {
			Utils.addBooleansToList(list);
		}
	}

	public String getBotProfile() {
		return botProfile;
	}

	public void setBotProfile(String botProfile) {
		this.botProfile = botProfile;
	}

	public void useDefaults(boolean serverSide) {
		botProfile = BotProfileImpl.INHERIT;
	}
	
}
