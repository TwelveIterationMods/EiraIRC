// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ChatMessageEvent extends Event {

    public final ICommandSender target;
    public final IChatComponent component;

    public ChatMessageEvent(IChatComponent component) {
        this.target = null;
        this.component = component;
    }

    public ChatMessageEvent(ICommandSender target, IChatComponent component) {
        this.target = target;
        this.component = component;
    }

}
