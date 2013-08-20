// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import blay09.mods.irc.CommonProxy;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerKeybindings() {
		KeyBindingRegistry.registerKeyBinding(new KeyBindingHandler());
	}
	
}
