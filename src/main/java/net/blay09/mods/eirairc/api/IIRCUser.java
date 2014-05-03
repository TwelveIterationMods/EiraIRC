package net.blay09.mods.eirairc.api;

public interface IIRCUser {

	public void whois();
	public void notice(String message);
	public String getAuthLogin();
	public String getIdentifier();
	public String getName();
	public IIRCConnection getConnection();

}
