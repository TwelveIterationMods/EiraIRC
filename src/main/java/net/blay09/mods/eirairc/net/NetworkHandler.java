// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.net;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.blay09.mods.eirairc.net.message.MessageHello;
import net.blay09.mods.eirairc.net.message.MessageNotification;
import net.blay09.mods.eirairc.net.message.MessageRedirect;
import net.blay09.mods.eirairc.net.message.handler.HandlerHello;
import net.blay09.mods.eirairc.net.message.handler.HandlerNotification;
import net.blay09.mods.eirairc.net.message.handler.HandlerRedirect;
import net.blay09.mods.eirairc.util.Globals;

public class NetworkHandler {

	public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(Globals.MOD_ID);
	
	public static void init() {
		instance.registerMessage(HandlerHello.class, MessageHello.class, 0, Side.SERVER);
		instance.registerMessage(HandlerNotification.class, MessageNotification.class, 1, Side.CLIENT);
		instance.registerMessage(HandlerRedirect.class, MessageRedirect.class, 4, Side.CLIENT);
	}
}
