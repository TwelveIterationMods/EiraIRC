// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.property;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.blay09.mods.eirairc.api.config.IConfigManager;
import net.blay09.mods.eirairc.api.config.IConfigProperty;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.IRCFormatting;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Map;

public class ConfigManager implements IConfigManager {

    private final Logger logger = LogManager.getLogger();
    private final Map<String, ConfigProperty> properties = Maps.newHashMap();

    private ConfigManager parentManager;

    @Override
    @SuppressWarnings("unchecked")
    public <T> IConfigProperty<T> getProperty(String modid, String name) {
        return getProperty((modid == null || modid.equals("eirairc")) ? name : (modid + "_" + name));
    }

    @Override
    public <T> IConfigProperty<T> registerProperty(String modid, String name, String langKey, T defaultValue) {
        return new ConfigProperty<>(this, "addons", modid + "_" + name, langKey, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public void registerProperty(ConfigProperty property) {
        properties.put(property.getCategory() + ":" + property.getName(), property);
        if(parentManager != null) {
            property.setParentProperty(parentManager.getProperty(property.getName()));
        }
    }

    public void load(Configuration config) {
        load(config, false);
    }

    @SuppressWarnings("unchecked")
    public void load(Configuration config, boolean ignoreDefaultValues) {
        for(ConfigProperty property : properties.values()) {
            if(ignoreDefaultValues && !config.hasKey(property.getCategory(), property.getName())) {
               continue;
            }
            Object type = property.getDefaultValue();
            if(type.getClass() == String.class) {
                String value = config.getString(property.getName(), property.getCategory(), (String) property.getDefaultValue(), I19n.format(property.getLangKey() + ".tooltip"), property.getLangKey());
                if(!ignoreDefaultValues || !value.equals(property.getDefaultValue())) {
                    property.set(value);
                }
            } else if(type.getClass() == Boolean.class) {
                boolean value = config.getBoolean(property.getName(), property.getCategory(), (Boolean) property.getDefaultValue(), I19n.format(property.getLangKey() + ".tooltip"), property.getLangKey());
                if(!ignoreDefaultValues || value != (Boolean) property.getDefaultValue()) {
                    property.set(value);
                }
            } else if(type.getClass() == Integer.class) {
                int value = config.getInt(property.getName(), property.getCategory(), (Integer) property.getDefaultValue(), Integer.MIN_VALUE, Integer.MAX_VALUE, I19n.format(property.getLangKey() + ".tooltip"), property.getLangKey());
                if(!ignoreDefaultValues || value != (Integer) property.getDefaultValue()) {
                    property.set(value);
                }
            } else if(type.getClass() == Float.class) {
                float value = config.getFloat(property.getName(), property.getCategory(), (Float) property.getDefaultValue(), Float.MIN_VALUE, Float.MAX_VALUE, I19n.format(property.getLangKey() + ".tooltip"), property.getLangKey());
                if (!ignoreDefaultValues || value != (Float) property.getDefaultValue()) {
                    property.set(value);
                }
            } else if(type.getClass() == StringList.class) {
                String[] value = config.getStringList(property.getName(), property.getCategory(), ((StringList) property.getDefaultValue()).getAsArray(), I19n.format(property.getLangKey() + ".tooltip"), null, property.getLangKey());
                if (value.length > 0) {
                    property.set(value);
                }
            } else if(type.getClass() == EnumChatFormatting.class) {
                String stringValue = config.getString(property.getName(), property.getCategory(), IRCFormatting.getNameFromColor((EnumChatFormatting) property.getDefaultValue()), I19n.format(property.getLangKey() + ".tooltip"), IRCFormatting.mcColorNames, property.getLangKey());
                if(stringValue.isEmpty()) {
                    continue;
                }
                EnumChatFormatting color = IRCFormatting.getColorFromName(stringValue);
                if(color != null) {
                    if (!ignoreDefaultValues || !color.equals(property.getDefaultValue())) {
                        property.set(color);
                    }
                } else {
                    StringBuilder validValues = new StringBuilder();
                    for(String colorName : IRCFormatting.mcColorNames) {
                        if(validValues.length() > 0) {
                            validValues.append(", ");
                        }
                        validValues.append(colorName);
                    }
                    logger.error("Invalid config value {} for option {} - valid values are: {}", stringValue.toLowerCase(), property.getName(), validValues.toString());
                }
            } else if(type instanceof Enum) {
                Enum[] enums = ((Enum) type).getClass().getEnumConstants();
                String[] validValues = new String[enums.length];
                for(int i = 0; i < validValues.length; i++) {
                    validValues[i] = enums[i].name().toLowerCase();
                }
                String stringValue = config.getString(property.getName(), property.getCategory(), ((Enum) property.getDefaultValue()).name(), I19n.format("eirairc:config.property." + property.getName() + ".tooltip"), validValues, "eirairc:config.property." + property.getName());
                if(stringValue.isEmpty()) {
                    continue;
                }
                try {
                    Enum value = Enum.valueOf((Class<? extends Enum>) type.getClass(), stringValue.toUpperCase());
                    if (!ignoreDefaultValues || !value.equals(property.getDefaultValue())) {
                        property.set(value);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid config value {} for option {} - valid values are: {}", stringValue.toLowerCase(), property.getName(), StringUtils.join(validValues, ", "));
                }
            }
        }
    }

    public void save(Configuration config) {
        for(ConfigProperty property : properties.values()) {
            Object value = property.get();
            if(value.getClass() == String.class) {
                config.get(property.getCategory(), property.getName(), (String) property.getDefaultValue(), I19n.format(property.getLangKey() + ".tooltip")).set((String) value);
            } else if(value.getClass() == Boolean.class) {
                config.get(property.getCategory(), property.getName(), (Boolean) property.getDefaultValue(), I19n.format(property.getLangKey() + ".tooltip")).set((Boolean) value);
            } else if(value.getClass() == Integer.class) {
                config.get(property.getCategory(), property.getName(), (Integer) property.getDefaultValue(), I19n.format(property.getLangKey() + ".tooltip")).set((Integer) value);
            } else if(value.getClass() == Float.class) {
                config.get(property.getCategory(), property.getName(), (Float) property.getDefaultValue(), I19n.format(property.getLangKey() + ".tooltip")).set((Float) value);
            } else if(value.getClass() == StringList.class) {
                config.get(property.getCategory(), property.getName(), ((StringList) property.getDefaultValue()).getAsArray(), I19n.format(property.getLangKey() + ".tooltip")).set(((StringList) value).getAsArray());
            } else if(value.getClass() == EnumChatFormatting.class) {
                config.get(property.getCategory(), property.getName(), IRCFormatting.getNameFromColor((EnumChatFormatting) property.getDefaultValue()), I19n.format(property.getLangKey() + ".tooltip"), IRCFormatting.mcColorNames).set(IRCFormatting.getNameFromColor((EnumChatFormatting) value));
            } else if(value instanceof Enum) {
                Enum[] enums = ((Enum) value).getClass().getEnumConstants();
                String[] validValues = new String[enums.length];
                for(int i = 0; i < validValues.length; i++) {
                    validValues[i] = enums[i].name().toLowerCase();
                }
                config.get(property.getCategory(), property.getName(), ((Enum) property.getDefaultValue()).name(), I19n.format(property.getLangKey() + ".tooltip"), validValues).set(((Enum) value).name());
            }
        }
    }

    public Collection<ConfigProperty> getProperties() {
        return properties.values();
    }

    public ConfigProperty getProperty(String name) {
        return properties.get(name);
    }

    public String getAsString(String name) {
        ConfigProperty property = getProperty(name);
        if(property == null) {
            return null;
        }
        return String.valueOf(property.get());
    }

    @SuppressWarnings("unchecked")
    public boolean setFromString(String name, String value) {
        ConfigProperty property = getProperty(name);
        if(property == null) {
            return false;
        }
        Object type = property.getDefaultValue();
        if (type.getClass() == String.class) {
            property.set(value);
        } else if (type.getClass() == Boolean.class) {
            property.set(Boolean.parseBoolean(value));
        } else if (type.getClass() == Integer.class) {
            property.set(Integer.parseInt(value));
        } else if(type.getClass() == Float.class) {
            property.set(Float.parseFloat(value));
        } else if(type.getClass() == StringList.class) {
            StringList list = new StringList(((StringList) property.get()).getAsArray());
            if (value.startsWith("add ")) {
                list.add(value.substring(4));
            } else if (value.startsWith("remove ")) {
                list.remove(value.substring(7));
            }
            property.set(list);
        } else if(type.getClass() == EnumChatFormatting.class) {
            EnumChatFormatting color = IRCFormatting.getColorFromName(value);
            if(color != null) {
                property.set(color);
            } else {
                // TODO print error message
            }
        } else if(type instanceof Enum) {
            try {
                property.set(Enum.valueOf((Class<? extends Enum>) type.getClass(), value));
            } catch (IllegalArgumentException e) {
                // TODO print error message
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public void setParentManager(ConfigManager manager) {
        this.parentManager = manager;
        for(ConfigProperty property : properties.values()) {
            property.setParentProperty(manager.getProperty(property.getName()));
        }
    }

    public void resetProperties() {
        for(ConfigProperty property : properties.values()) {
            property.reset();
        }
    }

    public Configuration pullDummyConfig() {
        Configuration dummyConfig = new Configuration();
        for(ConfigProperty property : properties.values()) {
            Object type = property.getDefaultValue();
            Property dummyProperty;
            if(type.getClass() == String.class) {
                dummyProperty = dummyConfig.get(property.getCategory(), property.getName(), (String) property.getDefaultValue(), I19n.format(property.getLangKey() + ".tooltip"));
            } else if(type.getClass() == Boolean.class) {
                dummyProperty = dummyConfig.get(property.getCategory(), property.getName(), (Boolean) property.getDefaultValue(), I19n.format(property.getLangKey() + ".tooltip"));
            } else if(type.getClass() == Integer.class) {
                dummyProperty = dummyConfig.get(property.getCategory(), property.getName(), (Integer) property.getDefaultValue(), I19n.format(property.getLangKey() + ".tooltip"));
            } else if(type.getClass() == Float.class) {
                dummyProperty = dummyConfig.get(property.getCategory(), property.getName(), (Float) property.getDefaultValue(), I19n.format(property.getLangKey() + ".tooltip"));
            } else if(type.getClass() == StringList.class) {
                dummyProperty = dummyConfig.get(property.getCategory(), property.getName(), ((StringList) property.getDefaultValue()).getAsArray(), I19n.format(property.getLangKey() + ".tooltip"));
            }else if(type.getClass() == EnumChatFormatting.class) {
                dummyProperty = dummyConfig.get(property.getCategory(), property.getName(), IRCFormatting.getNameFromColor((EnumChatFormatting) property.getDefaultValue()), I19n.format(property.getLangKey() + ".tooltip"));
            } else if(type instanceof Enum) {
                dummyProperty = dummyConfig.get(property.getCategory(), property.getName(), ((Enum) property.getDefaultValue()).name(), I19n.format(property.getLangKey() + ".tooltip"));
            } else {
                continue;
            }
            dummyProperty.setLanguageKey(property.getLangKey());
            if(property.hasValue()) {
                if(type.getClass() == String.class) {
                    dummyProperty.set((String) property.get());
                } else if(type.getClass() == Boolean.class) {
                    dummyProperty.set((Boolean) property.get());
                } else if(type.getClass() == Integer.class) {
                    dummyProperty.set((Integer) property.get());
                } else if(type.getClass() == Float.class) {
                    dummyProperty.set((Float) property.get());
                } else if(type.getClass() == StringList.class) {
                    dummyProperty.set(((StringList) property.get()).getAsArray());
                } else if(type.getClass() == EnumChatFormatting.class) {
                    dummyProperty.set(IRCFormatting.getNameFromColor((EnumChatFormatting) property.get()));
                } else if(type.getClass() == Enum.class) {
                    dummyProperty.set(((Enum) property.get()).name());
                }
            }
        }
        return dummyConfig;
    }

    @SuppressWarnings("unchecked")
    public void load(JsonObject object) {
        for(ConfigProperty property : properties.values()) {
            if(!object.has(property.getName())) {
                continue;
            }
            Object type = property.getDefaultValue();
            if(type.getClass() == String.class) {
                property.set(object.get(property.getName()).getAsString());
            } else if(type.getClass() == Boolean.class) {
                property.set(object.get(property.getName()).getAsBoolean());
            } else if(type.getClass() == Integer.class) {
                property.set(object.get(property.getName()).getAsInt());
            } else if(type.getClass() == Float.class) {
                property.set(object.get(property.getName()).getAsFloat());
            } else if(type.getClass() == StringList.class) {
                StringList stringList = new StringList();
                JsonArray stringArray = object.get(property.getName()).getAsJsonArray();
                for (int i = 0; i < stringArray.size(); i++) {
                    stringList.add(stringArray.get(i).getAsString());
                }
                property.set(stringList);
            } else if(type.getClass() == EnumChatFormatting.class) {
                String stringValue = object.get(property.getName()).getAsString();
                if(stringValue.isEmpty()) {
                    continue;
                }
                EnumChatFormatting color = IRCFormatting.getColorFromName(stringValue);
                if(color != null) {
                    property.set(color);
                } else {
                    StringBuilder validValues = new StringBuilder();
                    for(String colorName : IRCFormatting.mcColorNames) {
                        if(validValues.length() > 0) {
                            validValues.append(", ");
                        }
                        validValues.append(colorName);
                    }
                    logger.error("Invalid config value {} for option {} - valid values are: {}", stringValue.toLowerCase(), property.getName(), validValues.toString());
                }
            } else if(type instanceof Enum) {
                String stringValue = object.get(property.getName()).getAsString();
                if(stringValue.isEmpty()) {
                    continue;
                }
                try {
                    Enum value = Enum.valueOf((Class<? extends Enum>) type.getClass(), stringValue.toUpperCase());
                    property.set(value);
                } catch (IllegalArgumentException e) {
                    StringBuilder validValues = new StringBuilder();
                    for(Enum enumValue : ((Enum) type).getClass().getEnumConstants()) {
                        if(validValues.length() > 0) {
                            validValues.append(", ");
                        }
                        validValues.append(enumValue.name().toLowerCase());
                    }
                    logger.error("Invalid config value {} for option {} - valid values are: {}", stringValue.toLowerCase(), property.getName(), validValues.toString());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void save(JsonObject object) {
        for(ConfigProperty property : properties.values()) {
            if(!property.hasValue()) {
                continue;
            }
            Object type = property.getDefaultValue();
            if(type.getClass() == String.class) {
                object.addProperty(property.getName(), (String) property.get());
            } else if(type.getClass() == Boolean.class) {
                object.addProperty(property.getName(), (Boolean) property.get());
            } else if(type.getClass() == Integer.class) {
                object.addProperty(property.getName(), (Integer) property.get());
            } else if(type.getClass() == Float.class) {
                object.addProperty(property.getName(), (Float) property.get());
            } else if(type.getClass() == StringList.class) {
                JsonArray stringArray = new JsonArray();
                for(String s : (StringList) property.get()) {
                    stringArray.add(new JsonPrimitive(s));
                }
                object.add(property.getName(), stringArray);
            } else if(type instanceof Enum) {
                object.addProperty(property.getName(), ((Enum) property.get()).name());
            }
        }
    }
}
