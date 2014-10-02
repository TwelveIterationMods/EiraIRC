package net.blay09.mods.eirairc.config.settings;

import net.minecraftforge.common.config.Configuration;

import java.util.EnumMap;

/**
 * Created by Blay09 on 02.10.2014.
 */
public class GeneralSettings {

	private final GeneralSettings parent;

	private final EnumMap<GeneralBooleanComponent, String> booleans = new EnumMap<GeneralBooleanComponent, String>(GeneralBooleanComponent.class);

	public GeneralSettings(GeneralSettings parent) {
		this.parent = parent;
	}

	public void load(Configuration config, String category, boolean defaultValues) {

	}

	public void save(Configuration config, String category) {

	}

	public void loadLegacy(Configuration legacyConfig) {

	}
}
