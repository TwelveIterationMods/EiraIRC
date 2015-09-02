// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.addon;

import mnm.mods.tabbychat.api.Channel;
import mnm.mods.tabbychat.api.ChannelStatus;
import mnm.mods.tabbychat.api.TabbyAPI;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.IChatHandler;
import net.blay09.mods.eirairc.api.event.IRCChannelJoinedEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelLeftEvent;
import net.blay09.mods.eirairc.api.event.IRCPrivateChatEvent;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
public class TabbyChat2Addon {

    public TabbyChat2Addon() {
        MinecraftForge.EVENT_BUS.register(this);

        EiraIRCAPI.setChatHandler(new IChatHandler() {
            @Override
            public void addChatMessage(IChatComponent component) {
                String message = component.getUnformattedText();
                if(message.length() > 0 && message.charAt(0) == '[') {
                    int idx = message.indexOf(']');
                    if(idx != -1) {
                        TabbyAPI.getAPI().getChat().getChannel(message.substring(1, idx)).addMessage(component);
                    }
                }
            }

            @Override
            public void addChatMessage(ICommandSender receiver, IChatComponent component) {
                addChatMessage(component);
            }
        });
    }

    @SubscribeEvent
    public void onChannelJoined(IRCChannelJoinedEvent event) {
        Channel channel = TabbyAPI.getAPI().getChat().getChannel(event.channel.getName());
        channel.setPrefixHidden(true);
        channel.setPrefix("/irc msg " + event.channel.getIdentifier() + " ");
        channel.setStatus(ChannelStatus.ACTIVE);
    }

    @SubscribeEvent
    public void onChannelLeft(IRCChannelLeftEvent event) {
        Channel channel = TabbyAPI.getAPI().getChat().getChannel(event.channel.getName());
        TabbyAPI.getAPI().getChat().removeChannel(channel);
    }

    @SubscribeEvent
    public void onPrivateMessage(IRCPrivateChatEvent event) {
        if(event.sender == null) {
            return;
        }
        Channel channel = TabbyAPI.getAPI().getChat().getChannel(event.sender.getName(), true);
        channel.setPrefixHidden(true);
        channel.setPrefix("/irc msg " + event.sender.getIdentifier() + " ");
        channel.setStatus(ChannelStatus.ACTIVE);
    }

}
