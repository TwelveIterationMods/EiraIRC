package net.blay09.mods.eirairc.net;

import net.blay09.mods.eirairc.net.message.MessageHello;
import net.blay09.mods.eirairc.net.message.MessageNotification;
import net.blay09.mods.eirairc.net.message.MessageRecLiveState.CMessageRecLiveState;
import net.blay09.mods.eirairc.net.message.MessageRecLiveState.SMessageRecLiveState;
import net.blay09.mods.eirairc.net.message.MessageRedirect;
import net.blay09.mods.eirairc.net.message.handler.*;
import net.blay09.mods.eirairc.util.Globals;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

	public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(Globals.MOD_ID);
	
	public static void init() {
		instance.registerMessage(HandlerHello.class, MessageHello.class, 0, Side.CLIENT);
		instance.registerMessage(HandlerNotification.class, MessageNotification.class, 1, Side.CLIENT);
		instance.registerMessage(SHandlerRecLiveState.class, SMessageRecLiveState.class, 2, Side.CLIENT);
		instance.registerMessage(CHandlerRecLiveState.class, CMessageRecLiveState.class, 3, Side.SERVER);
		instance.registerMessage(HandlerRedirect.class, MessageRedirect.class, 4, Side.CLIENT);
	}
}
