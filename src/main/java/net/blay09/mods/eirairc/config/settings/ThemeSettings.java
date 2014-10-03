package net.blay09.mods.eirairc.config.settings;

import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.resources.I18n;
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

	private static final String[] VALID_COLOR_CODES = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

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
				colors.put(ThemeColorComponent.values[i], config.getString(ThemeColorComponent.values[i].name, category, ThemeColorComponent.values[i].defaultValue, "", VALID_COLOR_CODES, ThemeColorComponent.values[i].langKey));
			}
		}
	}

	public void load(JsonObject object) {
		for(int i = 0; i < ThemeColorComponent.values().length; i++) {
			if(object.has(ThemeColorComponent.values[i].name)) {
				colors.put(ThemeColorComponent.values[i], object.get(ThemeColorComponent.values[i].name).getAsString());
			}
		}
	}

	public JsonObject toJsonObject() {
		if(colors.isEmpty()) {
			return null;
		}
		JsonObject object = new JsonObject();
		for(Map.Entry<ThemeColorComponent, String> entry : colors.entrySet()) {
			object.addProperty(entry.getKey().name, entry.getValue());
		}
		return object;
	}

	public void save(Configuration config, String category) {
		for(Map.Entry<ThemeColorComponent, String> entry : colors.entrySet()) {
			config.get(category, entry.getKey().name, "", I18n.format(entry.getKey().langKey + ".tooltip")).set(entry.getValue());
		}
	}

	public void loadLegacy(Configuration legacyConfig, String categoryName) {
		if(categoryName != null) {
			String emoteColor = Utils.unquote(legacyConfig.get(categoryName, "emoteColor", "").getString());
			if(!emoteColor.isEmpty()) {
				colors.put(ThemeColorComponent.emoteTextColor, emoteColor);
			}
			String ircColor = Utils.unquote(legacyConfig.get(categoryName, "ircColor", "").getString());
			if(!ircColor.isEmpty()) {
				colors.put(ThemeColorComponent.ircNameColor, ircColor);
			}
		} else {
			colors.put(ThemeColorComponent.emoteTextColor, Utils.unquote(legacyConfig.get("display", "emoteColor", ThemeColorComponent.emoteTextColor.defaultValue).getString()));
			colors.put(ThemeColorComponent.mcNameColor, Utils.unquote(legacyConfig.get("display", "defaultColor", ThemeColorComponent.mcNameColor.defaultValue).getString()));
			colors.put(ThemeColorComponent.mcOpNameColor, Utils.unquote(legacyConfig.get("display", "opColor", ThemeColorComponent.mcOpNameColor.defaultValue).getString()));
			colors.put(ThemeColorComponent.ircNameColor, Utils.unquote(legacyConfig.get("display", "ircColor", ThemeColorComponent.ircNameColor.defaultValue).getString()));
			colors.put(ThemeColorComponent.ircPrivateNameColor, Utils.unquote(legacyConfig.get("display", "ircPrivateColor", ThemeColorComponent.ircPrivateNameColor.defaultValue).getString()));
			colors.put(ThemeColorComponent.ircVoiceNameColor, Utils.unquote(legacyConfig.get("display", "ircVoiceColor", ThemeColorComponent.ircVoiceNameColor.defaultValue).getString()));
			colors.put(ThemeColorComponent.ircOpNameColor, Utils.unquote(legacyConfig.get("display", "ircOpColor", ThemeColorComponent.ircOpNameColor.defaultValue).getString()));
			colors.put(ThemeColorComponent.ircNoticeTextColor, Utils.unquote(legacyConfig.get("display", "ircNoticeColor", ThemeColorComponent.ircNoticeTextColor.defaultValue).getString()));
		}
	}
}
