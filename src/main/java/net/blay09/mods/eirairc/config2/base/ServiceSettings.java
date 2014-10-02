// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config2.base;


public class ServiceSettings {
	
	private String identifyCMD;
	private String ghostCMD;
	
	public ServiceSettings(String identifyCMD, String ghostCMD) {
		this.identifyCMD = identifyCMD;
		this.ghostCMD = ghostCMD;
	}
	
	public String getIdentifyCommand(String username, String password) {
		return identifyCMD.replaceAll("\\{USER\\}", username).replaceAll("\\{PASS\\}", password);
	}
	
	public String getGhostCommand(String nick, String password) {
		return ghostCMD.replaceAll("\\{NICK\\}", nick).replaceAll("\\{PASS\\}", password);
	}

	public boolean hasGhostCommand() {
		return ghostCMD != null && !ghostCMD.isEmpty();
	}

}
