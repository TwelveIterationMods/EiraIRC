// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.util.List;

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
	private boolean readOnly;
	private boolean muted;
	private boolean autoWho;
	private boolean autoJoin = true;
	
	public boolean relayMinecraftJoinLeave;
	public boolean relayDeathMessages;
	public boolean relayIRCJoinLeave;
	public boolean relayNickChanges;
	public boolean relayBroadcasts;
	
	public ChannelConfig(ServerConfig serverConfig, String name) {
		this.serverConfig = serverConfig;
		if(serverConfig.getHost().equals(Globals.TWITCH_SERVER)) {
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
	
	public boolean isMuted() {
		return muted;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void setAutoJoin(boolean autoJoin) {
		this.autoJoin = autoJoin;
	}
	
	public boolean isAutoJoin() {
		return autoJoin;
	}

	public void defaultTwitch() {
		autoWho = false;
	}
	
	public void defaultServer() {
		relayMinecraftJoinLeave = true;
		relayDeathMessages = true;
		relayIRCJoinLeave = true;
		relayNickChanges = true;
		relayBroadcasts = true;
		autoWho = true;
	}
	
	public void defaultClient() {
		relayMinecraftJoinLeave = false;
		relayDeathMessages = false;
		relayIRCJoinLeave = true;
		relayNickChanges = true;
		autoWho = false;
	}

	public void load(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		password = Utils.unquote(config.get(categoryName, "password", "").getString());
		readOnly = config.get(categoryName, "readOnly", readOnly).getBoolean(readOnly);
		muted = config.get(categoryName, "muted", muted).getBoolean(muted);
		autoJoin = config.get(categoryName, "autoJoin", autoJoin).getBoolean(autoJoin);
		autoWho = config.get(categoryName, "autoWho", autoWho).getBoolean(autoWho);
		relayMinecraftJoinLeave = config.get(categoryName, "relayMinecraftJoinLeave", relayMinecraftJoinLeave).getBoolean(relayMinecraftJoinLeave);
		relayDeathMessages = config.get(categoryName, "relayDeathMessages", relayDeathMessages).getBoolean(relayDeathMessages);
		relayIRCJoinLeave = config.get(categoryName, "relayIRCJoinLeave", relayIRCJoinLeave).getBoolean(relayIRCJoinLeave);
		relayNickChanges = config.get(categoryName, "relayIRCNickChange", relayNickChanges).getBoolean(relayNickChanges);
		relayBroadcasts = config.get(categoryName, "relayBroadcasts", relayBroadcasts).getBoolean(relayBroadcasts);
	}

	public void save(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		config.get(categoryName, "name", "").set(Utils.quote(name));
		config.get(categoryName, "password", "").set(Utils.quote(GlobalConfig.saveCredentials && password != null ? password : ""));
		config.get(categoryName, "readOnly", readOnly).set(readOnly);
		config.get(categoryName, "muted", muted).set(muted);
		config.get(categoryName, "autoJoin", autoJoin).set(autoJoin);
		config.get(categoryName, "autoWho", autoWho).set(autoWho);
		config.get(categoryName, "relayMinecraftJoinLeave", relayMinecraftJoinLeave).set(relayMinecraftJoinLeave);
		config.get(categoryName, "relayDeathMessages", relayDeathMessages).set(relayDeathMessages);
		config.get(categoryName, "relayIRCJoinLeave", relayIRCJoinLeave).set(relayIRCJoinLeave);
		config.get(categoryName, "relayNickChanges", relayNickChanges).set(relayNickChanges);
		config.get(categoryName, "relayBroadcasts", relayBroadcasts).set(relayBroadcasts);
	}

	public void handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("readOnly")) value = String.valueOf(readOnly);
		else if(key.equals("muted")) value = String.valueOf(muted);
		else if(key.equals("autoJoin")) value = String.valueOf(autoJoin);
		else if(key.equals("relayMinecraftJoinLeave")) value = String.valueOf(relayMinecraftJoinLeave);
		else if(key.equals("relayDeathMessages")) value = String.valueOf(relayDeathMessages);
		else if(key.equals("relayIRCJoinLeave")) value = String.valueOf(relayIRCJoinLeave);
		else if(key.equals("relayNickChanges")) value = String.valueOf(relayNickChanges);
		else if(key.equals("relayBroadcasts")) value = String.valueOf(relayBroadcasts);
		else if(key.equals("autoWho")) value = String.valueOf(autoWho);
		if(value != null) {
			Utils.sendLocalizedMessage(sender, "irc.config.lookup", name, key, value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", name, key);
		}
	}
	
	public void handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("readOnly")) {
			readOnly = Boolean.parseBoolean(value);
		} else if(key.equals("muted")) {
			muted = Boolean.parseBoolean(value);
		} else if(key.equals("autoJoin")) {
			autoJoin = Boolean.parseBoolean(value);
		} else if(key.equals("relayMinecraftJoinLeave")) {
			relayMinecraftJoinLeave = Boolean.parseBoolean(value);
		} else if(key.equals("relayDeathMessages")) {
			relayDeathMessages = Boolean.parseBoolean(value);
		} else if(key.equals("relayIRCJoinLeave")) {
			relayIRCJoinLeave = Boolean.parseBoolean(value);
		} else if(key.equals("relayNickChanges")) {
			relayNickChanges = Boolean.parseBoolean(value);
		} else if(key.equals("relayBroadcasts")) {
			relayBroadcasts = Boolean.parseBoolean(value);
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
		list.add("readOnly");
		list.add("muted");
		list.add("autoJoin");
		list.add("relayMinecraftJoinLeave");
		list.add("relayDeathMessages");
		list.add("relayIRCJoinLeave");
		list.add("relayNickChanges");
		list.add("relayBroadcasts");
		list.add("autoWho");
	}
	
	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}
	
	public void setAutoWho(boolean autoWho) {
		this.autoWho = autoWho;
	}
	
	public boolean isAutoWho() {
		return autoWho;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public static void addValuesToList(List<String> list, String option) {
		Utils.addBooleansToList(list);
	}
	
}
