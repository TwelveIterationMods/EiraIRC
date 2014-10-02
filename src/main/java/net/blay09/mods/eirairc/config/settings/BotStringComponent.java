package net.blay09.mods.eirairc.config.settings;

/**
 * Created by Blay09 on 02.10.2014.
 */
public enum BotStringComponent {
	Ident("ident", "EiraIRC", ""),
	Description("description", "EiraIRC Bot", ""),
	QuitMessage("quitMessage", "Lycopene~", ""),
	MessageFormat("messageFormat", "S-Light", ""),
	NickFormat("mcNickFormat", "%s", "");

	public static final BotStringComponent[] values = values();

	public final String name;
	public final String defaultValue;
	public final String comment;

	private BotStringComponent(String name, String defaultValue, String comment) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.comment = comment;
	}

}
