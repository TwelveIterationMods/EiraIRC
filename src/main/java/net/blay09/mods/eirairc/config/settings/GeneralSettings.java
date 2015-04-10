package net.blay09.mods.eirairc.config.settings;

import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public class GeneralSettings {

	private final EnumMap<GeneralBooleanComponent, Boolean> booleans = new EnumMap<GeneralBooleanComponent, Boolean>(GeneralBooleanComponent.class);
	private final GeneralSettings parent;

	private Configuration dummyConfig;

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

	public void setBoolean(GeneralBooleanComponent component, boolean value) {
		if(parent.getBoolean(component) == value) {
			booleans.remove(component);
		} else {
			booleans.put(component, value);
		}
	}

	public boolean isReadOnly() {
		return getBoolean(GeneralBooleanComponent.ReadOnly);
	}

	public boolean isMuted() {
		return getBoolean(GeneralBooleanComponent.Muted);
	}

	public void pushDummyConfig() {
		if(dummyConfig != null) {
			load(dummyConfig, "settings", false);
			dummyConfig = null;
		}
	}

	public Configuration pullDummyConfig() {
		dummyConfig = new Configuration();
		for(int i = 0; i < GeneralBooleanComponent.values().length; i++) {
			Property property = dummyConfig.get("settings", GeneralBooleanComponent.values[i].name, parent.getBoolean(GeneralBooleanComponent.values[i]));
			property.setLanguageKey(GeneralBooleanComponent.values[i].langKey);
			if(booleans.containsKey(GeneralBooleanComponent.values[i])) {
				property.set(booleans.get(GeneralBooleanComponent.values[i]));
			}
		}
		return dummyConfig;
	}

	public void load(Configuration config, String category, boolean defaultValues) {
		for(int i = 0; i < GeneralBooleanComponent.values().length; i++) {
			if(defaultValues || config.hasKey(category, GeneralBooleanComponent.values[i].name)) {
				boolean value = config.getBoolean(GeneralBooleanComponent.values[i].name, category, GeneralBooleanComponent.values[i].defaultValue, I19n.format(GeneralBooleanComponent.values[i].langKey + ".tooltip"), GeneralBooleanComponent.values[i].langKey);
				if(defaultValues || value != parent.getBoolean(GeneralBooleanComponent.values[i])) {
					booleans.put(GeneralBooleanComponent.values[i], value);
				}
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
			config.get(category, entry.getKey().name, false, I19n.format(entry.getKey().langKey + ".tooltip")).set(entry.getValue());
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

	public String handleConfigCommand(ICommandSender sender, String key) {
		GeneralBooleanComponent component = GeneralBooleanComponent.fromName(key);
		if(component != null) {
			if (booleans.containsKey(component)) {
				return String.valueOf(booleans.get(component));
			} else {
				return "<inherit>";
			}
		}
		return null;
	}
	public boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		GeneralBooleanComponent component = GeneralBooleanComponent.fromName(key);
		if(component != null) {
			booleans.put(component, Boolean.parseBoolean(value));
			return true;
		}
		return false;
	}
	public static void addOptionsToList(List<String> list, String option) {
		if(option == null) {
			for(GeneralBooleanComponent component : GeneralBooleanComponent.values) {
				list.add(component.name);
			}
		} else {
			if(GeneralBooleanComponent.fromName(option) != null) {
				Utils.addMCColorsToList(list);
			}
		}
	}
}
