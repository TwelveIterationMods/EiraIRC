// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.settings;

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

	GeneralBooleanComponent(String name, boolean defaultValue, String langKey) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.langKey = langKey;
	}

	public static GeneralBooleanComponent fromName(String name) {
		for(GeneralBooleanComponent component : values) {
			if(component.name.equals(name)) {
				return component;
			}
		}
		return null;
	}
}
