// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.property;

import net.blay09.mods.eirairc.api.config.IConfigProperty;
import net.blay09.mods.eirairc.config.ConfigManager;

public class ConfigProperty<T> implements IConfigProperty<T> {

    private final ConfigManager manager;
    private final String name;
    private final String category;
    private final String langKey;
    private final T defaultValue;
    private T value;
    private boolean hasValue;
    private ConfigProperty<T> parentProperty;

    private String[] validValues;
    private T min;
    private T max;

    public ConfigProperty(ConfigManager manager, String category, String name, T defaultValue) {
        this(manager, category, name, "eirairc:config.property." + name, defaultValue);
    }

    public ConfigProperty(ConfigManager manager, String category, String name, String langKey, T defaultValue) {
        this.manager = manager;
        this.category = category;
        this.name = name;
        this.langKey = langKey;
        this.defaultValue = defaultValue;

        manager.registerProperty(this);
    }

    public ConfigProperty<T> getParentProperty() {
        return parentProperty;
    }

    public void setParentProperty(ConfigProperty<T> parentProperty) {
        this.parentProperty = parentProperty;
    }

    @Override
    public T get() {
        if(!hasValue && parentProperty != null) {
            return parentProperty.get();
        }
        return value != null ? value : defaultValue;
    }

    @Override
    public void set(T value) {
        this.value = value;
        hasValue = value != defaultValue;
    }

    @Override
    public void setValidValues(String[] validValues) {
        this.validValues = validValues;
        manager.updateProperty(this);
    }

    public String[] getValidValues() {
        return validValues;
    }

    @Override
    public void setMinMax(T min, T max) {
        this.min = min;
        this.max = max;
        manager.updateProperty(this);
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public T getDefaultValue() {
        if(parentProperty != null) {
            return parentProperty.get();
        }
        return defaultValue;
    }

    public void reset() {
        value = defaultValue;
        hasValue = false;
    }

    public boolean hasValue() {
        return hasValue;
    }

    public String getAsString() {
        return String.valueOf(value);
    }

    public String getLangKey() {
        return langKey;
    }
}
