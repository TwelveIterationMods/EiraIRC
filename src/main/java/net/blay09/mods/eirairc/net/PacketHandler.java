package net.blay09.mods.eirairc.net;

import net.blay09.mods.eirairc.net.message.MessageHello;
import net.blay09.mods.eirairc.net.message.MessageNotification;
import net.blay09.mods.eirairc.net.message.MessageRecLiveState.CMessageRecLiveState;
import net.blay09.mods.eirairc.net.message.MessageRecLiveState.SMessageRecLiveState;
import net.blay09.mods.eirairc.net.message.handler.CHandlerRecLiveState;
import net.blay09.mods.eirairc.net.message.handler.HandlerHello;
import net.blay09.mods.eirairc.net.message.handler.HandlerNotification;
import net.blay09.mods.eirairc.net.message.handler.SHandlerRecLiveState;
import net.blay09.mods.eirairc.util.Globals;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Globals.MOD_ID);
	
	public static void init() {
		INSTANCE.registerMessage(HandlerHello.class, MessageHello.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(HandlerNotification.class, MessageNotification.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(SHandlerRecLiveState.class, SMessageRecLiveState.class, 2, Side.CLIENT);
		INSTANCE.registerMessage(CHandlerRecLiveState.class, CMessageRecLiveState.class, 3, Side.SERVER);
	}
}
