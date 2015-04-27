// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.util.IChatComponent;

public class ApplyEmoticons extends Event {

    public IChatComponent component;

    public ApplyEmoticons(IChatComponent component) {
        this.component = component;
    }

}
