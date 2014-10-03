package net.blay09.mods.eirairc.config.settings;

/**
* Created by Blay09 on 02.10.2014.
*/
public enum ThemeColorComponent {
	mcNameColor("mcNameColor", "white", "eirairc:config.property.mcNameColor"),
	mcOpNameColor("mcOpNameColor", "red", "eirairc:config.property.mcOpNameColor"),
	ircNameColor("ircNameColor", "gray", "eirairc:config.property.ircNameColor"),
	ircOpNameColor("ircOpNameColor", "gold", "eirairc:config.property.ircOpNameColor"),
	ircVoiceNameColor("ircVoiceNameColor", "gray", "eirairc:config.property.ircVoiceNameColor"),
	ircPrivateNameColor("ircPrivateNameColor", "gray" ,"eirairc:config.property.ircPrivateNameColor"),
	ircNoticeTextColor("ircNoticeTextColor", "red", "eirairc:config.property.ircNoticeTextColor"),
	emoteTextColor("emoteTextColor", "gold", "eirairc:config.property.emoteTextColor");

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
