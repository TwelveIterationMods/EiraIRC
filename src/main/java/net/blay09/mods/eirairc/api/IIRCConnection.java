package net.blay09.mods.eirairc.api;

import java.util.Collection;

import net.blay09.mods.eirairc.api.bot.IIRCBot;

public interface IIRCConnection {

	public boolean irc(String irc);
	public void nick(String nick);
	public void join(String channelName, String password);
	public void part(String channelName);
	public void kick(String channelName, String nick, String reason);
	public void mode(String channelName, String mode);
	public void mode(String channelName, String nick, String mode);
	public void topic(String channelName, String topic);
	public void disconnect(String quitMessage);
	
	public String getServerType();
	public Collection<IIRCChannel> getChannels();
	public IIRCChannel getChannel(String name);
	public IIRCUser getUser(String name);
	public IIRCUser getOrCreateUser(String name);
	public String getHost();
	public int getPort();
	public IIRCBot getBot();
	public String getIdentifier();
	public String getNick();
	
}
