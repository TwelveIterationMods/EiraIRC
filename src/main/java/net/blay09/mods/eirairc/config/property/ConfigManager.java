// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.property;

import com.google.common.collect.Maps;
import net.blay09.mods.eirairc.util.I19n;
import net.minecraftforge.common.config.Configuration;

import java.util.Collection;
import java.util.Map;

public class ConfigManager {

    private Map<String, ConfigProperty> properties = Maps.newHashMap();

    public void registerProperty(ConfigProperty property) {
        properties.put(property.getName(), property);
    }

    @SuppressWarnings("unchecked")
    public void load(Configuration config) {
        for(ConfigProperty property : properties.values()) {
            Object type = property.getDefaultValue();
            if(type.getClass() == String.class) {
                property.set(config.getString(property.getName(), property.getCategory(), (String) property.getDefaultValue(), I19n.format("eirairc:config.property." + property.getName() + ".tooltip"), "eirairc:config.property."+ property.getName()));
            } else if(type.getClass() == Boolean.class) {
                property.set(config.getBoolean(property.getName(), property.getCategory(), (Boolean) property.getDefaultValue(), I19n.format("eirairc:config.property." + property.getName() + ".tooltip"), "eirairc:config.property." + property.getName()));
            } else if(type.getClass() == Integer.class) {
                property.set(config.getInt(property.getName(), property.getCategory(), (Integer) property.getDefaultValue(), Integer.MIN_VALUE, Integer.MAX_VALUE, I19n.format("eirairc:config.property." + property.getName() + ".tooltip"), "eirairc:config.property." + property.getName()));
            } else if(type.getClass() == Float.class) {
                property.set(config.getFloat(property.getName(), property.getCategory(), (Float) property.getDefaultValue(), Float.MIN_VALUE, Float.MAX_VALUE, I19n.format("eirairc:config.property." + property.getName() + ".tooltip"), "eirairc:config.property." + property.getName()));
            } else if(type instanceof Enum) {
                property.set(Enum.valueOf((Class<? extends Enum>) type.getClass(), config.getString(property.getName(), property.getCategory(), ((Enum) property.getDefaultValue()).name(), I19n.format("eirairc:config.property." + property.getName() + ".tooltip"), "eirairc:config.property."+ property.getName())));
            }
        }
    }

    public void save(Configuration config) {
        for(ConfigProperty property : properties.values()) {
            Object value = property.get();
            if(value.getClass() == String.class) {
                config.get(property.getCategory(), property.getName(), "", I19n.format("eirairc:config.property." + property.getName() + ".tooltip")).set((String) value);
            } else if(value.getClass() == Boolean.class) {
                config.get(property.getCategory(), property.getName(), false, I19n.format("eirairc:config.property." + property.getName() + ".tooltip")).set((Boolean) value);
            } else if(value.getClass() == Integer.class) {
                config.get(property.getCategory(), property.getName(), 0, I19n.format("eirairc:config.property." + property.getName() + ".tooltip")).set((Integer) value);
            } else if(value.getClass() == Float.class) {
                config.get(property.getCategory(), property.getName(), 0f, I19n.format("eirairc:config.property." + property.getName() + ".tooltip")).set((Float) value);
            } else if(value instanceof Enum) {
                config.get(property.getCategory(), property.getName(), 0, I19n.format("eirairc:config.property." + property.getName() + ".tooltip")).set(((Enum) value).name());
            }
        }
    }

    public Collection<ConfigProperty> getProperties() {
        return properties.values();
    }

    public ConfigProperty getProperty(String name) {
        return properties.get(name);
    }
}
