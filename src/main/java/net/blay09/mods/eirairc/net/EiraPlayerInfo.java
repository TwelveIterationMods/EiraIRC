// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net;

public class EiraPlayerInfo {

	private String username;
	public String modVersion;
	public boolean modInstalled;
	
	public EiraPlayerInfo(String username) {
		this.username = username;
	}
	
	public boolean hasMod() {
		return modInstalled;
	}

	public String getUsername() {
		return username;
	}
}
