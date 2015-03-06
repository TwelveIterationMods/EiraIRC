package net.blay09.mods.eirairc.config.settings;

/**
 * Created by Blay09 on 02.10.2014.
 */
public enum GeneralBooleanComponent {
	AutoJoin("autoJoin", true, "eirairc:config.property.autoJoin"),
	AutoWho("autoWho", false, "eirairc:config.property.autoWho"),
	ShowNameFlags("showNameFlags", false, "eirairc:config.property.showNameFlags"),
	ReadOnly("readOnly", false, "eirairc:config.property.readOnly"),
	Muted("muted", false, "eirairc:config.property.muted");

	public static final GeneralBooleanComponent[] values = values();

	public final String name;
	public final boolean defaultValue;
	public final String langKey;

	private GeneralBooleanComponent(String name, boolean defaultValue, String langKey) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.langKey = langKey;
	}

}
