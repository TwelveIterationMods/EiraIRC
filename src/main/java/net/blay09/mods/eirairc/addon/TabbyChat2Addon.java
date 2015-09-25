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
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.minecraft.client.Minecraft;
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
                addChatMessage(component, null);
            }

            @Override
            public void addChatMessage(ICommandSender receiver, IChatComponent component) {
                addChatMessage(component, null);
            }

            @Override
            public void addChatMessage(ICommandSender receiver, IChatComponent component, IRCContext source) {
                addChatMessage(component, source);
            }

            @Override
            public void addChatMessage(IChatComponent component, IRCContext source) {
                if(source == null) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(component);
                    return;
                }
                Channel channel = TabbyAPI.getAPI().getChat().getChannel(source.getName(), source.getContextType() == IRCContext.ContextType.IRCUser);
                channel.addMessage(component);
                channel.setStatus(ChannelStatus.UNREAD);
            }
        });
    }

    @SubscribeEvent
    public void onChannelJoined(IRCChannelJoinedEvent event) {
        Channel channel = TabbyAPI.getAPI().getChat().getChannel(event.channel.getName());
        channel.setPrefixHidden(true);
        channel.setPrefix("/irc msg " + event.channel.getIdentifier() + " ");
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
        Channel channel = TabbyAPI.getAPI().getChat().getChannel(event.sender.getName());
        channel.setPrefixHidden(true);
        channel.setPrefix("/irc msg " + event.sender.getIdentifier() + " ");
    }

}
