package net.blay09.mods.eirairc.config.settings;

/**
 * Created by Blay09 on 02.10.2014.
 */
public enum BotBooleanComponent {
	RelayDeathMessages("relayDeathMessages", false, ""),
	RelayMinecraftJoinLeave("relayMinecraftJoinLeave", false, ""),
	RelayIRCJoinLeave("relayIRCJoinLeave", true, ""),
	RelayNickChanges("relayNickChanges", true, ""),
	FilterLinks("filterLinks", false, ""),
	HideNotices("hideNotices", false, ""),
	ConvertColors("convertColors", true, ""),
	AllowPrivateMessages("allowPrivateMessages", true, ""),
	SendAutoWho("sendAutoWho", false, ""),
	RelayBroadcasts("relayBroadcasts", true, "");

	public static final BotBooleanComponent[] values = values();

	public final String name;
	public final boolean defaultValue;
	public final String comment;

	private BotBooleanComponent(String name, boolean defaultValue, String comment) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.comment = comment;
	}

}
