package net.blay09.mods.eirairc.config.settings;

import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.config.property.ConfigManager;
import net.blay09.mods.eirairc.config.property.ConfigProperty;
import net.blay09.mods.eirairc.util.IRCFormatting;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

import java.util.List;

public abstract class AbstractSettings {

    protected final String category;
    protected final ConfigManager manager = new ConfigManager();
    private final AbstractSettings parent;
    private Configuration dummyConfig;

    public AbstractSettings(AbstractSettings parent, String category) {
        this.parent = parent;
        this.category = category;
        if(parent != null) {
            manager.setParentManager(parent.manager);
        }
    }

    public void load(Configuration config, boolean ignoreDefaultValues) {
        manager.resetProperties();
        manager.load(config, ignoreDefaultValues);
    }

    public void save(Configuration config) {
        manager.save(config);
    }

    public void pushDummyConfig() {
        if(dummyConfig != null) {
            load(dummyConfig, true);
            dummyConfig = null;
        }
    }

    public Configuration pullDummyConfig() {
        dummyConfig = manager.pullDummyConfig();
        return dummyConfig;
    }

    public void load(JsonObject object) {
        manager.load(object);
    }

    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        manager.save(object);
        return object;
    }

    public String handleConfigCommand(ICommandSender sender, String key) {
        ConfigProperty property = manager.getProperty(key);
        if(property != null) {
            if(property.hasValue()) {
                return property.getAsString();
            } else {
                return "<inherit: " + property.getAsString() + ">";
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public boolean handleConfigCommand(ICommandSender sender, String key, String value) {
        return manager.setFromString(key, value);
    }

    public void addOptionsToList(List<String> list, String option, boolean autoCompleteOption) {
        if (autoCompleteOption) {
            for(ConfigProperty property : manager.getProperties()) {
                if(property.getName().startsWith(option)) {
                    list.add(property.getName());
                }
            }
        } else {
            ConfigProperty property = manager.getProperty(option);
            if(property != null) {
                if (property.get().getClass() == Boolean.class) {
                    list.add("true");
                    list.add("false");
                } else if (property.get().getClass() == EnumChatFormatting.class) {
                    IRCFormatting.addValidColorsToList(list);
                }
            }
        }
    }

}
