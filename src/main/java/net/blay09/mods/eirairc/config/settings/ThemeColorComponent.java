package net.blay09.mods.eirairc.config.settings;

/**
* Created by Blay09 on 02.10.2014.
*/
public enum ThemeColorComponent {
	mcNameColor("white", ""),
	mcOpNameColor("red", ""),
	ircNameColor("gray", ""),
	ircOpNameColor("gold", ""),
	ircVoiceNameColor("gray", ""),
	ircPrivateNameColor("gray" ,""),
	ircNoticeTextColor("red", ""),
	emoteTextColor("gold", "");

	public static final ThemeColorComponent[] values = values();

	public final String defaultValue;
	public final String comment;

	ThemeColorComponent(String defaultValue, String comment) {
		this.defaultValue = defaultValue;
		this.comment = comment;
	}

}
