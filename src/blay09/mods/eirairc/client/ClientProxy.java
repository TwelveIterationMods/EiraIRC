// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import blay09.mods.eirairc.CommonProxy;
import blay09.mods.eirairc.config.GlobalConfig;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerKeybindings() {
		KeyBindingRegistry.registerKeyBinding(new KeyBindingHandler());
	}

}
