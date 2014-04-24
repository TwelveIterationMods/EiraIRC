// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.irc;

public interface IIRCEventHandler {

	public void onNickChange(IRCConnection connection, IRCUser user, String nick);
	public void onUserJoin(IRCConnection connection, IRCUser user, IRCChannel channel);
	public void onUserPart(IRCConnection connection, IRCUser user, IRCChannel channel, String quitMessage);
	public void onUserQuit(IRCConnection connection, IRCUser user, String quitMessage);
	public void onPrivateEmote(IRCConnection connection, IRCUser user, String message);
	public void onPrivateMessage(IRCConnection connection, IRCUser user, String message);
	public void onChannelEmote(IRCConnection connection, IRCChannel channel, IRCUser user, String message);
	public void onChannelMessage(IRCConnection connection, IRCChannel channel, IRCUser user, String message);
	public void onTopicChange(IRCUser user, IRCChannel channel, String topic);
	
}
