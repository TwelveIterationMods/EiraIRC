// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.settings;

import net.blay09.mods.eirairc.config.property.ConfigProperty;

public class GeneralSettings extends AbstractSettings {

	private static final String SETTINGS = "settings";

	public final ConfigProperty<Boolean> autoJoin = new ConfigProperty<>(manager, SETTINGS, "autoJoin", true);
	public final ConfigProperty<Boolean> autoWho = new ConfigProperty<>(manager, SETTINGS, "autoWho", false);
	public final ConfigProperty<Boolean> showNameFlags = new ConfigProperty<>(manager, SETTINGS, "showNameFlags", false);
	public final ConfigProperty<Boolean> readOnly = new ConfigProperty<>(manager, SETTINGS, "readOnly", false);
	public final ConfigProperty<Boolean> muted = new ConfigProperty<>(manager, SETTINGS, "muted", false);
	public final ConfigProperty<Boolean> subOnly = new ConfigProperty<>(manager, SETTINGS, "subOnly", false);

	public GeneralSettings(GeneralSettings parent) {
		super(parent, SETTINGS);
	}

}
