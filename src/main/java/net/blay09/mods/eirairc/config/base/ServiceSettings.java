// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config.base;


public class ServiceSettings {
	
	private final String identifyCMD;
	private final String ghostCMD;
	
	public ServiceSettings(String identifyCMD, String ghostCMD) {
		this.identifyCMD = identifyCMD;
		this.ghostCMD = ghostCMD;
	}
	
	public String getIdentifyCommand(String username, String password) {
		return identifyCMD.replace("\\{USER\\}", username).replaceAll("\\{PASS\\}", password);
	}
	
	public String getGhostCommand(String nick, String password) {
		return ghostCMD.replace("\\{NICK\\}", nick).replaceAll("\\{PASS\\}", password);
	}

	public boolean hasGhostCommand() {
		return ghostCMD != null && !ghostCMD.isEmpty();
	}

}
