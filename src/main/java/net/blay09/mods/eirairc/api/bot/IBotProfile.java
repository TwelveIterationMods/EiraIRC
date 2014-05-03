// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.bot;

public interface IBotProfile {

	public static final String KEY_ALLOWPRIVMSG = "allowPrivateMessages";
	public static final String KEY_AUTOWHO = "autoWho";
	public static final String KEY_RELAYIRCJOINLEAVE = "relayIRCJoinLeave";
	public static final String KEY_RELAYMCJOINLEAVE = "relayMinecraftJoinLeave";
	public static final String KEY_RELAYDEATHMESSAGES = "relayDeathMessages";
	public static final String KEY_RELAYNICKCHANGES = "relayNickChanges";
	public static final String KEY_RELAYBROADCASTS = "relayBroadcasts";

	public boolean getBoolean(String key, boolean defaultVal);
	public IBotCommand getCommand(String commandName);
	public boolean isMuted();
	public boolean isReadOnly();
	
}
