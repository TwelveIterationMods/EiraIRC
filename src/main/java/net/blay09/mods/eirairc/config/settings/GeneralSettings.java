// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.settings;

import net.blay09.mods.eirairc.config.property.ConfigProperty;
import net.minecraftforge.common.config.Configuration;

public class GeneralSettings extends AbstractSettings {

	private static final String SETTINGS = "settings";

	public final ConfigProperty<Boolean> autoJoin = new ConfigProperty<>(manager, SETTINGS, "autoJoin", true);
	public final ConfigProperty<Boolean> autoWho = new ConfigProperty<>(manager, SETTINGS, "autoWho", false);
	public final ConfigProperty<Boolean> showNameFlags = new ConfigProperty<>(manager, SETTINGS, "showNameFlags", false);
	public final ConfigProperty<Boolean> readOnly = new ConfigProperty<>(manager, SETTINGS, "readOnly", false);
	public final ConfigProperty<Boolean> muted = new ConfigProperty<>(manager, SETTINGS, "muted", false);

	public GeneralSettings(GeneralSettings parent) {
		super(parent, SETTINGS);
	}

	public void loadLegacy(Configuration legacyConfig, String category) {
		if(category != null) {
			if (legacyConfig.hasKey(category, "autoConnect")) {
				autoJoin.set(legacyConfig.get(category, "autoConnect", autoJoin.getDefaultValue()).getBoolean());
			} else if (legacyConfig.hasKey(category, "autoJoin")) {
				autoJoin.set(legacyConfig.get(category, "autoJoin", autoJoin.getDefaultValue()).getBoolean());
			}
			if (legacyConfig.hasKey(category, "autoWho")) {
				autoWho.set(legacyConfig.get(category, "autoWho", autoWho.getDefaultValue()).getBoolean());
			}
		}
	}

}
