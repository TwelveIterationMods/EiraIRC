package net.blay09.mods.eirairc.addon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Blay09 on 29.03.2015.
 */
@SideOnly(Side.CLIENT)
public class AetherAddon {

	public static final Logger logger = LogManager.getLogger();

	public AetherAddon() {
		// Enable VanillaChat if Aether is installed as Aether does it's own chat GUI modifications.
		logger.info("Enabling vanillaChat config option for Aether II compatibility.");
		ClientGlobalConfig.vanillaChat = true;
	}

}
