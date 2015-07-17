// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.net;

public class EiraPlayerInfo {

	private final String username;
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
