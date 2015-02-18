package net.blay09.mods.eirairc.config.settings;

/**
 * Created by Blay09 on 02.10.2014.
 */
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

	private BotStringComponent(String name, String defaultValue, String langKey) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.langKey = langKey;
	}

}
