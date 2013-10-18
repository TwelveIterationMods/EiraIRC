// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import blay09.mods.eirairc.Utils;

public class ChannelConfig {

	private final String name;
	private String password;
	private boolean observer;
	private boolean muted;
	private boolean autoJoin = true;
	
	public boolean relayMinecraftJoinLeave;
	public boolean relayDeathMessages;
	public boolean relayIRCJoinLeave;
	public boolean relayIRCNickChange;
	
	public ChannelConfig(String name) {
		this.name = name;
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
	
	public boolean isObserver() {
		return observer;
	}
	
	public void setAutoJoin(boolean autoJoin) {
		this.autoJoin = autoJoin;
	}
	
	public boolean isAutoJoin() {
		return autoJoin;
	}

	public void defaultTwitch() {
		observer = true;
	}
	
	public void defaultServer() {
		relayMinecraftJoinLeave = true;
		relayDeathMessages = true;
		relayIRCJoinLeave = true;
		relayIRCNickChange = true;
	}
	
	public void defaultClient() {
		relayMinecraftJoinLeave = false;
		relayDeathMessages = false;
		relayIRCJoinLeave = true;
		relayIRCNickChange = true;
	}

	public void load(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		password = Utils.unquote(config.get(categoryName, "password", "").getString());
		observer = config.get(categoryName, "observer", observer).getBoolean(observer);
		muted = config.get(categoryName, "muted", muted).getBoolean(muted);
		autoJoin = config.get(categoryName, "autoJoin", autoJoin).getBoolean(autoJoin);
		relayMinecraftJoinLeave = config.get(categoryName, "relayMinecraftJoinLeave", relayMinecraftJoinLeave).getBoolean(relayMinecraftJoinLeave);
		relayDeathMessages = config.get(categoryName, "relayDeathMessages", relayDeathMessages).getBoolean(relayDeathMessages);
		relayIRCJoinLeave = config.get(categoryName, "relayIRCJoinLeave", relayIRCJoinLeave).getBoolean(relayIRCJoinLeave);
		relayIRCNickChange = config.get(categoryName, "relayIRCNickChange", relayIRCNickChange).getBoolean(relayIRCNickChange);
	}

	
	public void save(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		config.get(categoryName, "name", "").set(Utils.quote(name));
		if(GlobalConfig.saveCredentials) {
			config.get(categoryName, "password", "").set(Utils.quote(password != null ? password : ""));
		}
		config.get(categoryName, "observer", observer).set(observer);
		config.get(categoryName, "muted", muted).set(muted);
		config.get(categoryName, "autoJoin", autoJoin).set(autoJoin);
		config.get(categoryName, "relayMinecraftJoinLeave", relayMinecraftJoinLeave).set(relayMinecraftJoinLeave);
		config.get(categoryName, "relayDeathMessages", relayDeathMessages).set(relayDeathMessages);
		config.get(categoryName, "relayIRCJoinLeave", relayIRCJoinLeave).set(relayIRCJoinLeave);
		config.get(categoryName, "relayIRCNickChange", relayIRCNickChange).set(relayIRCNickChange);
	}
	
}
