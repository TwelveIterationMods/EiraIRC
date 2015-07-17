// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;

import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus for every text component in a message EiraIRC has received.
 * It will not be posted for components handled by EiraIRC, such as links or @Mentions.
 * Mods adding emoticons can use this to make their magic happen and adjust the chat component as needed.
 */
public class ApplyEmoticons extends Event {

    /**
     * The chat component containing the text message that should be checked for emoticons.
     * This component can be changed by anyone handling this event and EiraIRC will continue working with whatever is set here.
     */
    public IChatComponent component;

    /**
     * @param component the chat component containing the text message that should be checked for emoticons
     */
    public ApplyEmoticons(IChatComponent component) {
        this.component = component;
    }

}
