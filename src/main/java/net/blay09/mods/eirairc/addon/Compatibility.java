package net.blay09.mods.eirairc.addon;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class Compatibility {

    private static boolean isTabbyChat2Installed;

    public static boolean isTabbyChat2Installed() {
        return isTabbyChat2Installed;
    }

    public static void postInit(FMLPostInitializationEvent event) {
        event.buildSoftDependProxy("Dynmap", "net.blay09.mods.eirairc.addon.DynmapWebChatAddon");

        if (event.getSide() == Side.CLIENT) {
            event.buildSoftDependProxy("TabbyChat2", "net.blay09.mods.eirairc.addon.TabbyChat2Addon");
            event.buildSoftDependProxy("eiramoticons", "net.blay09.mods.eirairc.addon.EiraMoticonsAddon");
            new FancyOverlay();
        }

        isTabbyChat2Installed = Loader.isModLoaded("TabbyChat2");
    }
}
