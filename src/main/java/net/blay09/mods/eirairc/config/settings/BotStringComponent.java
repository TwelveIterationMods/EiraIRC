// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.settings;

public enum BotStringComponent {
	Ident("ident", "EiraIRC", "eirairc:config.property.ident"),
	Description("description", "EiraIRC Bot", "eirairc:config.property.description"),
	QuitMessage("quitMessage", "Lycopene~", "eirairc:config.property.quitMessage"),
	MessageFormat("messageFormat", "S-Light", "eirairc:config.property.messageFormat"),
	NickFormat("mcNickFormat", "%s", "eirairc:config.property.mcNickFormat");

	public static final BotStringComponent[] values = values();

	public final String name;
	public final String defaultValue;
	public final String langKey;

	BotStringComponent(String name, String defaultValue, String langKey) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.langKey = langKey;
	}

	public static BotStringComponent fromName(String name) {
		for(BotStringComponent value : values) {
			if (value.name.equals(name)) {
				return value;
			}
		}
		return null;
	}
}
