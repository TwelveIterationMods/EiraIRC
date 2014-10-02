package net.blay09.mods.eirairc.config.settings;

/**
 * Created by Blay09 on 02.10.2014.
 */
public enum GeneralBooleanComponent {
	AutoJoin("autoJoin", true, ""),
	AutoWho("autoWho", false, ""),
	ReadOnly("readOnly", false, ""),
	Muted("muted", false, "");

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
