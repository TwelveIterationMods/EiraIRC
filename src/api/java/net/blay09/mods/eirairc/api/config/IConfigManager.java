package net.blay09.mods.eirairc.api.config;

public interface IConfigManager {

    <T> IConfigProperty<T> registerProperty(String modid, String name, String langKey, T defaultValue);
    <T> IConfigProperty<T> getProperty(String modid, String name);

}