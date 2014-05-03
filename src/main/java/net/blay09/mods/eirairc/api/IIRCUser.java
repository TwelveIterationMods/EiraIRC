package net.blay09.mods.eirairc.api;

import java.util.Collection;

public interface IIRCUser extends IIRCContext {

	public void whois();
	public String getAuthLogin();
	public Collection<IIRCChannel> getChannels();

}
