package net.blay09.mods.eirairc.addon;

import net.blay09.mods.eiramoticons.api.IEmoticon;
import net.blay09.mods.eiramoticons.api.IEmoticonLoader;
import net.blay09.mods.eiramoticons.api.ReloadEmoticons;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Optional.Interface(modid = "eiramoticons", iface = "net.blay09.mods.eiramoticons.api.IEmoticonLoader")
public class EiraMoticonsAddon implements IEmoticonLoader {

	public static boolean isLoaded;

	public EiraMoticonsAddon() {
		isLoaded = true;

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	@Optional.Method(modid = "eiramoticons")
	public void reloadEmoticons(ReloadEmoticons event) {

	}

	@Override
	public void loadEmoticonImage(IEmoticon iEmoticon) {

	}

}
