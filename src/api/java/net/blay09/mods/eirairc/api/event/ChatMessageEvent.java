// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ChatMessageEvent extends Event {

    public final ICommandSender target;
    public final IChatComponent component;

    /**
     * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
     * @param component the chat component to display
     */
    public ChatMessageEvent(IChatComponent component) {
        this.target = null;
        this.component = component;
    }

    /**
     * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
     * @param target  the sender that issued the message
     * @param component the chat component to display
     */
    public ChatMessageEvent(ICommandSender target, IChatComponent component) {
        this.target = target;
        this.component = component;
    }

}
