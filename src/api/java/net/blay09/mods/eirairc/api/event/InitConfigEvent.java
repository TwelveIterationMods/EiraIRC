package net.blay09.mods.eirairc.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.blay09.mods.eirairc.api.config.IConfigManager;

public abstract class InitConfigEvent extends Event {

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
