package net.blay09.mods.eirairc.config.settings;

/**
 * Created by Blay09 on 02.10.2014.
 */
public enum BotStringComponent {
	Ident("ident", "EiraIRC", "The ident for your IRC user. No effect in channels."),
	Description("description", "EiraIRC Bot", "The description for your IRC user. No effect in channels."),
	QuitMessage("quitMessage", "Lycopene~", "The message shown to others when you leave IRC."),
	MessageFormat("messageFormat", "S-Light", "The name to the message format used to display chat messages. See /eirairc/formats/."),
	NickFormat("mcNickFormat", "%s", "The format to put the nick in (previously nickPrefix and nickSuffix). %s will be replaced by the nick. No effect in channels. Example: %s-IG"),
	BotProfile("botProfile", "Inherit", "[Deprecated] The bot profile to use for this connection. Only used for InterOP and custom commands. No effect in channels.");

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
