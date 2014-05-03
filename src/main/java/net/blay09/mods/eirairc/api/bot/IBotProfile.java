package net.blay09.mods.eirairc.api.bot;

public interface IBotProfile {

	public static final String KEY_ALLOWPRIVMSG = "allowPrivateMessages";
	public static final String KEY_RELAYIRCJOINLEAVE = "relayIRCJoinLeave";
	public static final String KEY_AUTOWHO = "autoWho";
	public static final String KEY_RELAYNICKCHANGES = "relayNickChanges";

	public boolean getBoolean(String key, boolean defaultVal);
	public boolean isMuted();
	public boolean isReadOnly();
	
}
