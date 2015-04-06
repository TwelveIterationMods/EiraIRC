package net.blay09.mods.eirairc.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * This event is published on the FMLCommonHandler.instance().bus() EventBus before chat is sent to the ClientCommandHandler and server.
 * Mods taking over the chat GUI should use this to make EiraIRC compatible.
 * If this event was cancelled (.post() returns true), the message was consumed by EiraIRC and should NOT be sent to the server.
 */
@Cancelable
public class ClientChatEvent extends Event {

    public final String message;

    public ClientChatEvent(String message) {
        this.message = message;
    }
}
