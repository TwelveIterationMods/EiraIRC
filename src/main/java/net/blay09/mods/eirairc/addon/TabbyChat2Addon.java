// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.addon;

import mnm.mods.tabbychat.api.Channel;
import mnm.mods.tabbychat.api.TabbyAPI;
import net.blay09.mods.eirairc.api.event.ChatMessageEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelJoinedEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelLeftEvent;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.BotStringComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TabbyChat2Addon {

    public TabbyChat2Addon() {
        MinecraftForge.EVENT_BUS.register(this);
        SharedGlobalConfig.botSettings.setString(BotStringComponent.MessageFormat, "TabbyChat2");
        SharedGlobalConfig.save();
        ClientGlobalConfig.chatNoOverride = true;
        ClientGlobalConfig.disableChatToggle = true;
        ClientGlobalConfig.save();
    }

    @SubscribeEvent
    public void onChannelJoined(IRCChannelJoinedEvent event) {
        Channel channel = TabbyAPI.getAPI().getChat().getChannel(event.channel.getName());
        channel.setPrefixHidden(true);
        channel.setPrefix("/irc msg " + event.channel.getName() + " ");
        channel.setActive(true);
    }

    @SubscribeEvent
    public void onChannelLeft(IRCChannelLeftEvent event) {
        Channel channel = TabbyAPI.getAPI().getChat().getChannel(event.channel.getName());
        channel.setActive(false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessage(ChatMessageEvent event) {
        String message = event.component.getUnformattedText();
        if(message.length() > 0 && message.charAt(0) == '[') {
            int idx = message.indexOf(']');
            if(idx != -1) {
                TabbyAPI.getAPI().getChat().getChannel(message.substring(1, idx)).addMessage(event.component);
                event.setCanceled(true);
            }
        }
    }

}
