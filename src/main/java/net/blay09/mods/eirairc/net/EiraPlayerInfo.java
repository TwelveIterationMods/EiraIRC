// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net;

public class EiraPlayerInfo {

	private String username;
	public String modVersion;
	public boolean isLive;
	public boolean isRecording;
	
	public EiraPlayerInfo(String username) {
		this.username = username;
	}
	
	public boolean hasMod() {
		return modVersion != null;
	}

	public String getUsername() {
		return username;
	}
}
