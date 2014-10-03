package net.blay09.mods.eirairc.config.settings;

/**
 * Created by Blay09 on 02.10.2014.
 */
public enum BotBooleanComponent {
	RelayDeathMessages("relayDeathMessages", false, "If set to true, Minecraft death messages will be sent to this IRC context."),
	RelayMinecraftJoinLeave("relayMinecraftJoinLeave", false, "If set to true, Minecraft join/leave messages will be sent to this IRC context."),
	RelayIRCJoinLeave("relayIRCJoinLeave", true, "If set to true, IRC join/leave messages from this IRC context will be sent to Minecraft chat."),
	RelayNickChanges("relayNickChanges", true, "If set to true, IRC nick changes from this IRC context will be sent to Minecraft chat."),
	FilterLinks("filterLinks", false, "If set to true, links from this IRC context will be replaced by <removed link>."),
	HideNotices("hideNotices", false, "If set to true, NOTICE messages from this IRC context will only be printed in the console."),
	ConvertColors("convertColors", true, "If set to true, colors from this IRC context will be translated into Minecraft colors and vice-versa."),
	AllowPrivateMessages("allowPrivateMessages", true, "If set to true, private messages can be sent and received from this IRC context."),
	SendAutoWho("sendAutoWho", false, "If set to true, users who join this IRC context will be sent a list of all online Minecraft players."),
	RelayBroadcasts("relayBroadcasts", true, "If set to true, Minecraft server broadcasts (/say) will be sent to this IRC context.");

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
