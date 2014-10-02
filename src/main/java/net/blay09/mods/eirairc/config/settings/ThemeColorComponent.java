package net.blay09.mods.eirairc.config.settings;

/**
* Created by Blay09 on 02.10.2014.
*/
public enum ThemeColorComponent {
	mcNameColor("mcNameColor", "white", ""),
	mcOpNameColor("mcOpNameColor", "red", ""),
	ircNameColor("ircNameColor", "gray", ""),
	ircOpNameColor("ircOpNameColor", "gold", ""),
	ircVoiceNameColor("ircVoiceNameColor", "gray", ""),
	ircPrivateNameColor("ircPrivateNameColor", "gray" ,""),
	ircNoticeTextColor("ircNoticeTextColor", "red", ""),
	emoteTextColor("emoteTextColor", "gold", "");

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
