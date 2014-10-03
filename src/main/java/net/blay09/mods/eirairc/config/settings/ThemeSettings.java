package net.blay09.mods.eirairc.config.settings;

import net.minecraftforge.common.config.Configuration;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Blay09 on 29.09.2014.
 */
public class ThemeSettings {

	private static final String[] VALID_COLORS = new String[] {
			"black",
			"darkblue",
			"green",
			"cyan",
			"darkred",
			"purple",
			"gold",
			"gray",
			"darkgray",
			"blue",
			"lime",
			"lightblue",
			"red",
			"magenta",
			"yellow",
			"white"
	};


	private final ThemeSettings parent;
	private final EnumMap<ThemeColorComponent, String> colors = new EnumMap<ThemeColorComponent, String>(ThemeColorComponent.class);

	public ThemeSettings(ThemeSettings parent) {
		this.parent = parent;
	}

	public String getColor(ThemeColorComponent component) {
		String color = colors.get(component);
		if(color == null && parent != null) {
			return parent.getColor(component);
		}
		return color;
	}

	public boolean hasColor(ThemeColorComponent component) {
		return colors.containsKey(component);
	}

	public void load(Configuration config, String category, boolean defaultValues) {
		for(int i = 0; i < ThemeColorComponent.values().length; i++) {
			if(defaultValues || config.hasKey(category, ThemeColorComponent.values[i].name)) {
				colors.put(ThemeColorComponent.values[i], config.getString(ThemeColorComponent.values[i].name, category, ThemeColorComponent.values[i].defaultValue, "", VALID_COLORS));
			}
		}
	}

	public void save(Configuration config, String category) {
		for(Map.Entry<ThemeColorComponent, String> entry : colors.entrySet()) {
			config.get(category, entry.getKey().name, "", entry.getKey().comment).set(entry.getValue());
		}
	}

	public void loadLegacy(Configuration legacyConfig) {
		colors.put(ThemeColorComponent.emoteTextColor, legacyConfig.get("display", "emoteColor", ThemeColorComponent.emoteTextColor.defaultValue).getString());
		colors.put(ThemeColorComponent.mcNameColor, legacyConfig.get("display", "defaultColor", ThemeColorComponent.mcNameColor.defaultValue).getString());
		colors.put(ThemeColorComponent.mcOpNameColor, legacyConfig.get("display", "opColor", ThemeColorComponent.mcOpNameColor.defaultValue).getString());
		colors.put(ThemeColorComponent.ircNameColor, legacyConfig.get("display", "ircColor", ThemeColorComponent.ircNameColor.defaultValue).getString());
		colors.put(ThemeColorComponent.ircPrivateNameColor, legacyConfig.get("display", "ircPrivateColor", ThemeColorComponent.ircPrivateNameColor.defaultValue).getString());
		colors.put(ThemeColorComponent.ircVoiceNameColor, legacyConfig.get("display", "ircVoiceColor", ThemeColorComponent.ircVoiceNameColor.defaultValue).getString());
		colors.put(ThemeColorComponent.ircOpNameColor, legacyConfig.get("display", "ircOpColor", ThemeColorComponent.ircOpNameColor.defaultValue).getString());
		colors.put(ThemeColorComponent.ircNoticeTextColor, legacyConfig.get("display", "ircNoticeColor", ThemeColorComponent.ircNoticeTextColor.defaultValue).getString());
	}
}
