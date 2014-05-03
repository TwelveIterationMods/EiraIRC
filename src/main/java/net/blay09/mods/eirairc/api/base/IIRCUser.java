package net.blay09.mods.eirairc.api.base;

public interface IIRCUser extends IIRCContext {

	public void whois();
	public void notice(String message);
	public String getAuthLogin();
	public String getIdentifier();
	public IIRCConnection getConnection();

}
