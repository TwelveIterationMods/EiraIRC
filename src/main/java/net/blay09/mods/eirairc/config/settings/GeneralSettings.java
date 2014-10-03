package net.blay09.mods.eirairc.config.settings;

import com.google.gson.JsonObject;
import net.minecraftforge.common.config.Configuration;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Blay09 on 02.10.2014.
 */
public class GeneralSettings {

	private final GeneralSettings parent;

	private final EnumMap<GeneralBooleanComponent, Boolean> booleans = new EnumMap<GeneralBooleanComponent, Boolean>(GeneralBooleanComponent.class);

	public GeneralSettings(GeneralSettings parent) {
		this.parent = parent;
	}

	public boolean getBoolean(GeneralBooleanComponent component) {
		if(!booleans.containsKey(component)) {
			if(parent != null) {
				return parent.getBoolean(component);
			}
			return component.defaultValue;
		}
		return booleans.get(component);
	}

	public boolean isReadOnly() {
		return getBoolean(GeneralBooleanComponent.ReadOnly);
	}

	public boolean isMuted() {
		return isMuted();
	}

	public void load(Configuration config, String category, boolean defaultValues) {
		for(int i = 0; i < GeneralBooleanComponent.values().length; i++) {
			if(defaultValues || config.hasKey(category, GeneralBooleanComponent.values[i].name)) {
				booleans.put(GeneralBooleanComponent.values[i], config.getBoolean(GeneralBooleanComponent.values[i].name, category, GeneralBooleanComponent.values[i].defaultValue, ""));
			}
		}
	}

	public void load(JsonObject object) {
		for(int i = 0; i < GeneralBooleanComponent.values().length; i++) {
			if(object.has(GeneralBooleanComponent.values[i].name)) {
				booleans.put(GeneralBooleanComponent.values[i], object.get(GeneralBooleanComponent.values[i].name).getAsBoolean());
			}
		}
	}

	public JsonObject toJsonObject() {
		if(booleans.isEmpty()) {
			return null;
		}
		JsonObject object = new JsonObject();
		for(Map.Entry<GeneralBooleanComponent, Boolean> entry : booleans.entrySet()) {
			object.addProperty(entry.getKey().name, entry.getValue());
		}
		return object;
	}

	public void save(Configuration config, String category) {
		for(Map.Entry<GeneralBooleanComponent, Boolean> entry : booleans.entrySet()) {
			config.get(category, entry.getKey().name, false, entry.getKey().comment).set(entry.getValue());
		}
	}

	public void loadLegacy(Configuration legacyConfig, String category) {
		if(category != null) {
			if (legacyConfig.hasKey(category, "autoConnect")) {
				booleans.put(GeneralBooleanComponent.AutoJoin, legacyConfig.get(category, "autoConnect", GeneralBooleanComponent.AutoJoin.defaultValue).getBoolean());
			} else if (legacyConfig.hasKey(category, "autoJoin")) {
				booleans.put(GeneralBooleanComponent.AutoJoin, legacyConfig.get(category, "autoJoin", GeneralBooleanComponent.AutoJoin.defaultValue).getBoolean());
			}
			if (legacyConfig.hasKey(category, "autoWho")) {
				booleans.put(GeneralBooleanComponent.AutoWho, legacyConfig.get(category, "autoWho", GeneralBooleanComponent.AutoWho.defaultValue).getBoolean());
			}
		}
	}
}
