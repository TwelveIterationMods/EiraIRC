package net.blay09.mods.eirairc.addon;

import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
		logger.info("Enabling chatNoOverride config option for Aether II compatibility.");
		ClientGlobalConfig.chatNoOverride = true;
	}

}
