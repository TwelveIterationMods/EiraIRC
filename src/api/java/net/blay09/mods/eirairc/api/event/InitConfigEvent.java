package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.config.IConfigManager;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * These event are published on the MinecraftForge.EVENTBUS bus for every IConfigManager created by EiraIRC, the event chosen depending on the origin of the config manager.
 * Mods adding custom addon properties can do so by listening to one of these events and registering them in there.
 */
public abstract class InitConfigEvent extends Event {

    /**
     * the config manager that has been set up
     */
    public final IConfigManager config;

    public InitConfigEvent(IConfigManager config) {
        this.config = config;
    }

    public static class SharedGlobalSettings extends InitConfigEvent {
        public SharedGlobalSettings(IConfigManager config) {
            super(config);
        }
    }

    public static class ClientGlobalSettings extends InitConfigEvent {
        public ClientGlobalSettings(IConfigManager config) {
            super(config);
        }
    }

    public static class GeneralSettings extends InitConfigEvent {
        public GeneralSettings(IConfigManager config) {
            super(config);
        }
    }

    public static class BotSettings extends InitConfigEvent {
        public BotSettings(IConfigManager config) {
            super(config);
        }
    }

    public static class ThemeSettings extends InitConfigEvent {
        public ThemeSettings(IConfigManager config) {
            super(config);
        }
    }
}
