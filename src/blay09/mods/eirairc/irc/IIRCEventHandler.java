package blay09.mods.eirairc.irc;

public interface IIRCEventHandler {

	public void onConnected(IRCConnection connection);
	public void onDisconnected(IRCConnection connection);
	public void onIRCError(IRCConnection connection, int errorCode);
	public void onNickChange(IRCConnection connection, IRCUser user, String nick);
	public void onUserJoin(IRCConnection connection, IRCUser user, IRCChannel channel);
	public void onUserPart(IRCConnection connection, IRCUser user, IRCChannel channel, String quitMessage);
	public void onUserQuit(IRCConnection connection, IRCUser user, String quitMessage);
	public void onPrivateEmote(IRCConnection connection, IRCUser user, String message);
	public void onPrivateMessage(IRCConnection connection, IRCUser user, String message);
	public void onChannelEmote(IRCConnection connection, IRCChannel channel, IRCUser user, String message);
	public void onChannelMessage(IRCConnection connection, IRCChannel channel, IRCUser user, String message);
	
}
