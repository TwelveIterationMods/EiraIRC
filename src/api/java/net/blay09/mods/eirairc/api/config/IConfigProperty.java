package net.blay09.mods.eirairc.api.config;

public interface IConfigProperty<T> {

    String getName();
    String getCategory();
    T getDefaultValue();
    T get();
    void set(T value);

}
