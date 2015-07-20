// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.base;

public class ServiceSettings {
	
	private String identifyCMD;
	private String ghostCMD;
	
	public ServiceSettings(String identifyCMD, String ghostCMD) {
		this.identifyCMD = identifyCMD;
		this.ghostCMD = ghostCMD;
	}
	
	public String getIdentifyCommand(String username, String password) {
		return identifyCMD.replace("{USER}", username).replace("{PASS}", password);
	}
	
	public String getGhostCommand(String nick, String password) {
		return ghostCMD.replace("{NICK}", nick).replace("{PASS}", password);
	}

	public boolean hasGhostCommand() {
		return ghostCMD != null && !ghostCMD.isEmpty();
	}

}
