package net.blay09.mods.eirairc.api.config;

public interface IConfigManager {

    /**
     * Registers a new addon property to this config manager. The resulting property name will be modid_name.
     * @param modid the modid of the mod providing the addon. This will be used as a prefix to prevent conflicts.
     * @param name the name of the option.
     * @param langKey the language key of this option.
     * @param defaultValue the default value of this option
     * @param <T> the type of this option; supported are String, Boolean, Integer, Float, StringList, EnumChatFormatting, any other Enums
     * @return the newly created config property
     */
    <T> IConfigProperty<T> registerProperty(String modid, String name, String langKey, T defaultValue);

    /**
     * Returns the property by the given name. Specifying a modid of 'eirairc' or null allows retrieval of EiraIRC's native options.
     * @param modid the modid of the mod implementing this option or null for native EiraIRC options
     * @param name the name of the option
     * @param <T> the type of this option; supported are String, Boolean, Integer, Float, StringList, EnumChatFormatting, any other Enums
     * @return the config property or null if it wasn't found
     */
    <T> IConfigProperty<T> getProperty(String modid, String name);

}
