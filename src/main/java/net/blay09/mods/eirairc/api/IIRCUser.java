package net.blay09.mods.eirairc.api;

import java.util.Collection;

public interface IIRCUser extends IIRCContext {

	public void whois();
	public String getAuthLogin();
	public String getIdentifier();
	public IIRCConnection getConnection();
	public Collection<IIRCChannel> getChannels();

}
