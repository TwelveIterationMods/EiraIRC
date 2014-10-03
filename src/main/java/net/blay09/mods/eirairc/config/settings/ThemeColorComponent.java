package net.blay09.mods.eirairc.config.settings;

/**
* Created by Blay09 on 02.10.2014.
*/
public enum ThemeColorComponent {
	mcNameColor("mcNameColor", "f", "eirairc:config.property.mcNameColor"),
	mcOpNameColor("mcOpNameColor", "c", "eirairc:config.property.mcOpNameColor"),
	ircNameColor("ircNameColor", "7", "eirairc:config.property.ircNameColor"),
	ircOpNameColor("ircOpNameColor", "6", "eirairc:config.property.ircOpNameColor"),
	ircVoiceNameColor("ircVoiceNameColor", "7", "eirairc:config.property.ircVoiceNameColor"),
	ircPrivateNameColor("ircPrivateNameColor", "7" ,"eirairc:config.property.ircPrivateNameColor"),
	ircNoticeTextColor("ircNoticeTextColor", "c", "eirairc:config.property.ircNoticeTextColor"),
	emoteTextColor("emoteTextColor", "6", "eirairc:config.property.emoteTextColor");

	public static final ThemeColorComponent[] values = values();

	public final String name;
	public final String defaultValue;
	public final String langKey;

	ThemeColorComponent(String name, String defaultValue, String langKey) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.langKey = langKey;
	}

}
