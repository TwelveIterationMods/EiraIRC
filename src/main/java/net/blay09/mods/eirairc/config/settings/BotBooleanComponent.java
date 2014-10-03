package net.blay09.mods.eirairc.config.settings;

/**
 * Created by Blay09 on 02.10.2014.
 */
public enum BotBooleanComponent {
	RelayDeathMessages("relayDeathMessages", false, "eirairc:config.property.relayDeathMessages"),
	RelayMinecraftJoinLeave("relayMinecraftJoinLeave", false, "eirairc:config.property.relayMinecraftJoinLeave"),
	RelayIRCJoinLeave("relayIRCJoinLeave", true, "eirairc:config.property.relayIRCJoinLeave"),
	RelayNickChanges("relayNickChanges", true, "eirairc:config.property.relayNickChanges"),
	FilterLinks("filterLinks", false, "eirairc:config.property.filterLinks"),
	HideNotices("hideNotices", false, "eirairc:config.property.hideNotices"),
	ConvertColors("convertColors", true, "eirairc:config.property.convertColors"),
	AllowPrivateMessages("allowPrivateMessages", true, "eirairc:config.property.allowPrivateMessages"),
	SendAutoWho("sendAutoWho", false, "eirairc:config.property.sendAutoWho"),
	RelayBroadcasts("relayBroadcasts", true, "eirairc:config.property.relayBroadcasts");

	public static final BotBooleanComponent[] values = values();

	public final String name;
	public final boolean defaultValue;
	public final String langKey;

	private BotBooleanComponent(String name, boolean defaultValue, String langKey) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.langKey = langKey;
	}

}
