// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.bot;

import java.util.Collection;

public interface IBotProfile {

	public static final String KEY_ALLOWPRIVMSG = "allowPrivateMessages";
	public static final String KEY_AUTOPLAYERS = "autoWho";
	public static final String KEY_RELAYIRCJOINLEAVE = "relayIRCJoinLeave";
	public static final String KEY_RELAYMCJOINLEAVE = "relayMinecraftJoinLeave";
	public static final String KEY_RELAYDEATHMESSAGES = "relayDeathMessages";
	public static final String KEY_RELAYNICKCHANGES = "relayNickChanges";
	public static final String KEY_RELAYBROADCASTS = "relayBroadcasts";
	public static final String KEY_LINKFILTER = "enableLinkFilter";
	public static final String INHERIT = "Inherit";

	public boolean getBoolean(String key, boolean defaultVal);
	public IBotCommand getCommand(String commandName);
	public boolean isMuted();
	public boolean isReadOnly();
	public String getDisplayFormat();
	public boolean isInterOp();
	public boolean isInterOpAuth(String authName);
	public String[] getInterOpBlacklist();
	public Collection<IBotCommand> getCommands();
	
}
