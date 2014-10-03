package net.blay09.mods.eirairc.config.settings;

/**
 * Created by Blay09 on 02.10.2014.
 */
public enum GeneralBooleanComponent {
	AutoJoin("autoJoin", true, "If set to true, EiraIRC will automatically join this server / channel on startup."),
	AutoWho("autoWho", false, "If set to true, EiraIRC will automatically print a list of all IRC users to the chat."),
	ReadOnly("readOnly", false, "If set to true, EiraIRC will only read messages from IRC, but never send any."),
	Muted("muted", false, "If set to true, EiraIRC will not show messages from IRC in chat.");

	public static final GeneralBooleanComponent[] values = values();

	public final String name;
	public final boolean defaultValue;
	public final String comment;

	private GeneralBooleanComponent(String name, boolean defaultValue, String comment) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.comment = comment;
	}

}
