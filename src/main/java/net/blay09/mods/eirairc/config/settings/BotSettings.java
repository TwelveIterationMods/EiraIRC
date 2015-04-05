package net.blay09.mods.eirairc.config.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.base.MessageFormatConfig;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.ArrayUtils;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Blay09 on 02.10.2014.
 */
public class BotSettings {

	private final BotSettings parent;

	private final EnumMap<BotStringComponent, String> strings = new EnumMap<BotStringComponent, String>(BotStringComponent.class);
	private final EnumMap<BotStringListComponent, String[]> stringLists = new EnumMap<BotStringListComponent, String[]>(BotStringListComponent.class);
	private final EnumMap<BotBooleanComponent, Boolean> booleans = new EnumMap<BotBooleanComponent, Boolean>(BotBooleanComponent.class);

	private Configuration dummyConfig;

	public BotSettings(BotSettings parent) {
		this.parent = parent;
	}

	public boolean stringContains(BotStringListComponent component, String s) {
		String[] list;
		if(!stringLists.containsKey(component)) {
			if(parent != null) {
				return parent.stringContains(component, s);
			}
			list = component.defaultValue;
		} else {
			list = stringLists.get(component);
		}
		for(int i = 0; i < list.length; i++) {
			if(component.allowWildcard && list[i].equals("*")) {
				return true;
			}
			if(s.contains(list[i])) {
				return true;
			}
		}
		return false;
	}

	public boolean containsString(BotStringListComponent component, String s) {
		String[] list;
		if(!stringLists.containsKey(component)) {
			if(parent != null) {
				return parent.containsString(component, s);
			}
			list = component.defaultValue;
		} else {
			list = stringLists.get(component);
		}
		for(int i = 0; i < list.length; i++) {
			if(component.allowWildcard && list[i].equals("*")) {
				return true;
			}
			if(list[i].equals(s)) {
				return true;
			}
		}
		return false;
	}

	private String[] getStringList(BotStringListComponent component) {
		if(!stringLists.containsKey(component)) {
			if(parent != null) {
				return parent.getStringList(component);
			}
			return component.defaultValue;
		}
		return stringLists.get(component);
	}

	public String getString(BotStringComponent component) {
		if(!strings.containsKey(component)) {
			if(parent != null) {
				return parent.getString(component);
			}
			return component.defaultValue;
		}
		return strings.get(component);
	}

	public void setString(BotStringComponent component, String value) {
		strings.put(component, value);
	}

	public boolean getBoolean(BotBooleanComponent component) {
		if(!booleans.containsKey(component)) {
			if(parent != null) {
				return parent.getBoolean(component);
			}
			return component.defaultValue;
		}
		return booleans.get(component);
	}

	public MessageFormatConfig getMessageFormat() {
		return ConfigurationHandler.getMessageFormat(getString(BotStringComponent.MessageFormat));
	}

	public void load(Configuration config, String category, boolean defaultValues) {
		strings.clear();
		for(int i = 0; i < BotStringComponent.values().length; i++) {
			if(defaultValues || config.hasKey(category, BotStringComponent.values[i].name)) {
				String value = config.getString(BotStringComponent.values[i].name, category, BotStringComponent.values[i].defaultValue, I19n.format(BotStringComponent.values[i].langKey + ".tooltip"), BotStringComponent.values[i].langKey);
				if(defaultValues || !value.equals(parent.getString(BotStringComponent.values[i]))) {
					strings.put(BotStringComponent.values[i], value);
				}
			}
		}
		stringLists.clear();
		for(int i = 0; i < BotStringListComponent.values().length; i++) {
			if(defaultValues || config.hasKey(category, BotStringListComponent.values[i].name)) {
				String[] value = config.getStringList(BotStringListComponent.values[i].name, category, BotStringListComponent.values[i].defaultValue, I19n.format(BotStringListComponent.values[i].langKey + ".tooltip"), null, BotStringListComponent.values[i].langKey);
				if(defaultValues || value.length > 0) {
					stringLists.put(BotStringListComponent.values[i], value);
				}
			}
		}
		booleans.clear();
		for(int i = 0; i < BotBooleanComponent.values().length; i++) {
			if(defaultValues || config.hasKey(category, BotBooleanComponent.values[i].name)) {
				boolean value = config.getBoolean(BotBooleanComponent.values[i].name, category, BotBooleanComponent.values[i].defaultValue, I19n.format(BotBooleanComponent.values[i].langKey + ".tooltip"), BotBooleanComponent.values[i].langKey);
				if(defaultValues || value != parent.getBoolean(BotBooleanComponent.values[i])) {
					booleans.put(BotBooleanComponent.values[i], value);
				}
			}
		}
	}

	public void pushDummyConfig() {
		if(dummyConfig != null) {
			load(dummyConfig, "bot", false);
			dummyConfig = null;
		}
	}

	public Configuration pullDummyConfig() {
		dummyConfig = new Configuration();
		for(int i = 0; i < BotStringComponent.values().length; i++) {
			Property property = dummyConfig.get("bot", BotStringComponent.values[i].name, parent.getString(BotStringComponent.values[i]));
			property.setLanguageKey(BotStringComponent.values[i].langKey);
			if(strings.containsKey(BotStringComponent.values[i])) {
				property.set(strings.get(BotStringComponent.values[i]));
			}
		}
		for(int i = 0; i < BotStringListComponent.values().length; i++) {
			Property property = dummyConfig.get("bot", BotStringListComponent.values[i].name, parent.getStringList(BotStringListComponent.values[i]));
			property.setLanguageKey(BotStringListComponent.values[i].langKey);
			if(stringLists.containsKey(BotStringListComponent.values[i])) {
				property.set(stringLists.get(BotStringListComponent.values[i]));
			}
		}
		for(int i = 0; i < BotBooleanComponent.values().length; i++) {
			Property property = dummyConfig.get("bot", BotBooleanComponent.values[i].name, parent.getBoolean(BotBooleanComponent.values[i]));
			property.setLanguageKey(BotBooleanComponent.values[i].langKey);
			if(booleans.containsKey(BotBooleanComponent.values[i])) {
				property.set(booleans.get(BotBooleanComponent.values[i]));
			}
		}
		return dummyConfig;
	}

	public void load(JsonObject object) {
		for(int i = 0; i < BotStringComponent.values().length; i++) {
			if(object.has(BotStringComponent.values[i].name)) {
				strings.put(BotStringComponent.values[i], object.get(BotStringComponent.values[i].name).getAsString());
			}
		}
		for(int i = 0; i < BotStringListComponent.values().length; i++) {
			if(object.has(BotStringListComponent.values[i].name)) {
				JsonArray array = object.getAsJsonArray(BotStringListComponent.values[i].name);
				String[] values = new String[array.size()];
				for(int j = 0; j < values.length; j++) {
					values[j] = array.get(j).getAsString();
				}
				stringLists.put(BotStringListComponent.values[i], values);
			}
		}
		for(int i = 0; i < BotBooleanComponent.values().length; i++) {
			if(object.has(BotBooleanComponent.values[i].name)) {
				booleans.put(BotBooleanComponent.values[i], object.get(BotBooleanComponent.values[i].name).getAsBoolean());
			}
		}
	}

	public JsonObject toJsonObject() {
		if(strings.isEmpty() && booleans.isEmpty()) {
			return null;
		}
		JsonObject object = new JsonObject();
		for(Map.Entry<BotStringComponent, String> entry : strings.entrySet()) {
			object.addProperty(entry.getKey().name, entry.getValue());
		}
		for(Map.Entry<BotStringListComponent, String[]> entry : stringLists.entrySet()) {
			JsonArray array = new JsonArray();
			for(int i = 0; i < entry.getValue().length; i++) {
				array.add(new JsonPrimitive(entry.getValue()[i]));
			}
			object.add(entry.getKey().name, array);
		}
		for(Map.Entry<BotBooleanComponent, Boolean> entry : booleans.entrySet()) {
			object.addProperty(entry.getKey().name, entry.getValue());
		}
		return object;
	}

	public void save(Configuration config, String category) {
		for(Map.Entry<BotStringComponent, String> entry : strings.entrySet()) {
			config.get(category, entry.getKey().name, "", I19n.format(entry.getKey().langKey + ".tooltip")).set(entry.getValue());
		}
		for(Map.Entry<BotStringListComponent, String[]> entry : stringLists.entrySet()) {
			config.get(category, entry.getKey().name, "", I19n.format(entry.getKey().langKey + ".tooltip")).set(entry.getValue());
		}
		for(Map.Entry<BotBooleanComponent, Boolean> entry : booleans.entrySet()) {
			config.get(category, entry.getKey().name, false, I19n.format(entry.getKey().langKey + ".tooltip")).set(entry.getValue());
		}
	}

	public void loadLegacy(Configuration legacyConfig, String category) {
		if(category != null) {
			strings.put(BotStringComponent.Description, Utils.unquote(legacyConfig.get(category, "description", BotStringComponent.Description.defaultValue).getString()));
			strings.put(BotStringComponent.Ident, Utils.unquote(legacyConfig.get(category, "ident", BotStringComponent.Ident.defaultValue).getString()));
			String quitMessage = Utils.unquote(legacyConfig.get(category, "quitMessage", "").getString());
			if(!quitMessage.isEmpty()) {
				strings.put(BotStringComponent.QuitMessage, quitMessage);
			}
		} else {
			strings.put(BotStringComponent.NickFormat, Utils.unquote(legacyConfig.get("serveronly", "nickPrefix", "").getString()) + "%s" + Utils.unquote(legacyConfig.get("serveronly", "nickSuffix", "").getString()));
			booleans.put(BotBooleanComponent.HideNotices, legacyConfig.get("display", "hideNotices", BotBooleanComponent.HideNotices.defaultValue).getBoolean());
			booleans.put(BotBooleanComponent.ConvertColors, legacyConfig.get("display", "enableIRCColors", BotBooleanComponent.ConvertColors.defaultValue).getBoolean());
		}
	}

	public String handleConfigCommand(ICommandSender sender, String key) {
		BotBooleanComponent booleanComponent = BotBooleanComponent.fromName(key);
		if(booleanComponent != null) {
			if(booleans.containsKey(booleanComponent)) {
				return String.valueOf(booleans.get(booleanComponent));
			} else {
				return "<inherit>";
			}
		}
		BotStringComponent stringComponent = BotStringComponent.fromName(key);
		if(stringComponent != null) {
			if(strings.containsKey(stringComponent)) {
				return strings.get(stringComponent);
			} else {
				return "<inherit>";
			}
		}
		BotStringListComponent stringListComponent = BotStringListComponent.fromName(key);
		if(stringListComponent != null) {
			if (stringLists.containsKey(stringListComponent)) {
				return Utils.joinStrings(stringLists.get(stringListComponent), ", ", 0);
			} else {
				return "<inherit>";
			}
		}
		return null;
	}
	public boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		BotBooleanComponent booleanComponent = BotBooleanComponent.fromName(key);
		if(booleanComponent != null) {
			booleans.put(booleanComponent, Boolean.parseBoolean(value));
			return true;
		}
		BotStringComponent stringComponent = BotStringComponent.fromName(key);
		if(stringComponent != null) {
			strings.put(stringComponent, value);
			return true;
		}
		BotStringListComponent stringListComponent = BotStringListComponent.fromName(key);
		if(stringListComponent != null) {
			String[] list = stringLists.get(stringListComponent);
			if (value.startsWith("add ")) {
				if (list == null) {
					list = new String[]{value.substring(4)};
				} else {
					list = ArrayUtils.add(list, value.substring(4));
				}
			} else if (value.startsWith("remove ") && list != null) {
				for (int i = 0; i < list.length; i++) {
					if (list[i].equals(value.substring(7))) {
						list = ArrayUtils.remove(list, i);
						break;
					}
				}
			}
			if (list != null) {
				stringLists.put(stringListComponent, list);
			}
			return true;
		}
		return false;
	}
	public static void addOptionsToList(List<String> list, String option) {
		if(option == null) {
			for(BotBooleanComponent component : BotBooleanComponent.values) {
				list.add(component.name);
			}
			for(BotStringComponent component : BotStringComponent.values) {
				list.add(component.name);
			}
			for(BotStringListComponent component : BotStringListComponent.values) {
				list.add(component.name);
			}
		} else {
			if(BotBooleanComponent.fromName(option) != null) {
				Utils.addBooleansToList(list);
			}
		}
	}

}
