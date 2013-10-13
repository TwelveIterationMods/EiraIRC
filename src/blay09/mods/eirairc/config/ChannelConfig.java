// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;

public class ChannelConfig {

	private String name;
	private String password;
	private boolean observer;
	private boolean muted;
	private boolean autoJoin;
	
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
	
	public void load(Configuration config, ConfigCategory category) {
		password = ConfigurationHandler.unquote(config.get(category.getQualifiedName(), "password", "").getString());
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
	
}
