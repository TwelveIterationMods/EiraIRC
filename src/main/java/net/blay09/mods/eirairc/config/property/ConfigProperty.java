// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.property;

import net.blay09.mods.eirairc.api.config.IConfigProperty;

public class ConfigProperty<T> implements IConfigProperty<T> {

    private final ConfigManager manager;
    private final String name;
    private final String category;
    private final T defaultValue;
    private T value;
    private boolean hasValue;
    private ConfigProperty<T> parentProperty;

    public ConfigProperty(ConfigManager manager, String category, String name, T defaultValue) {
        this.manager = manager;
        this.category = category;
        this.name = name;
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

}
