package net.blay09.mods.eirairc.api.base;

import net.blay09.mods.eirairc.api.bot.IIRCBot;

public interface IIRCConnection {

	public String getHost();
	public IIRCBot getBot();
	public String getIdentifier();
	
}
