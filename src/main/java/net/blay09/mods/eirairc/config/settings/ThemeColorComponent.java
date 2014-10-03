package net.blay09.mods.eirairc.config.settings;

/**
* Created by Blay09 on 02.10.2014.
*/
public enum ThemeColorComponent {
	mcNameColor("mcNameColor", "white", "Color for default Minecraft names."),
	mcOpNameColor("mcOpNameColor", "red", "Color for operator Minecraft names."),
	ircNameColor("ircNameColor", "gray", "Color for default IRC names."),
	ircOpNameColor("ircOpNameColor", "gold", "Color for operator IRC names."),
	ircVoiceNameColor("ircVoiceNameColor", "gray", "Color for voiced IRC names."),
	ircPrivateNameColor("ircPrivateNameColor", "gray" ,"Color for IRC names in private chat."),
	ircNoticeTextColor("ircNoticeTextColor", "red", "Color for IRC NOTICE messages."),
	emoteTextColor("emoteTextColor", "gold", "Color for emote messages.");

	public static final ThemeColorComponent[] values = values();

	public final String name;
	public final String defaultValue;
	public final String comment;

	ThemeColorComponent(String name, String defaultValue, String comment) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.comment = comment;
	}

}
