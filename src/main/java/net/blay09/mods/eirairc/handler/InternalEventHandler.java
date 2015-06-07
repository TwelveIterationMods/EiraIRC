// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.handler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.addon.Compatibility;
import net.blay09.mods.eirairc.addon.EiraMoticonsAddon;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.IRCReplyCodes;
import net.blay09.mods.eirairc.api.event.*;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.bot.IRCBotImpl;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.BotBooleanComponent;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.GeneralBooleanComponent;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.irc.IRCUserImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.IRCFormatting;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class InternalEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUserJoin(IRCUserJoinEvent event) {
        BotSettings botSettings = ConfigHelper.getBotSettings(event.channel);
        if(botSettings.getBoolean(BotBooleanComponent.SendAutoWho)) {
            Utils.sendPlayerList(event.user);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onConnecting(IRCConnectingEvent event) {
        EiraIRC.instance.getConnectionManager().addConnection(event.connection);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onConnected(IRCConnectEvent event) {
        ServerConfig serverConfig = ConfigHelper.getServerConfig(event.connection);
        // If this is a Twitch connection, tell the server that we're a JTVCLIENT so we receive name colors.
        if(event.connection.getHost().equals(Globals.TWITCH_SERVER)) {
            event.connection.irc("JTVCLIENT");
        }
        Utils.doNickServ(event.connection, serverConfig);
        for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
            GeneralSettings generalSettings = channelConfig.getGeneralSettings();
            if(generalSettings.getBoolean(GeneralBooleanComponent.AutoJoin)) {
                event.connection.join(channelConfig.getName(), channelConfig.getPassword());
            }
        }
        if(!SharedGlobalConfig.defaultChat.equals("Minecraft")) {
            IRCContext chatTarget = EiraIRCAPI.parseContext(null, SharedGlobalConfig.defaultChat, IRCContext.ContextType.IRCChannel);
            if(chatTarget.getContextType() != IRCContext.ContextType.Error) {
                EiraIRC.instance.getChatSessionHandler().setChatTarget(chatTarget);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPrivateChat(IRCPrivateChatEvent event) {
        if(!event.isNotice) {
            if(((IRCBotImpl) event.bot).processCommand(null, event.sender, event.message)) {
                event.setCanceled(true);
                return;
            } else {
                if(event.bot.isServerSide()) {
                    event.sender.notice(Utils.getLocalizedMessage("irc.bot.unknownCommand"));
                    event.setCanceled(true);
                    return;
                }
            }
        }
        if(event.sender != null && event.sender.getName().equals("tmi.twitch.tv") && event.isNotice && event.connection.getHost().equals(Globals.TWITCH_SERVER) && event.message.equals("Login unsuccessful")) {
            event.connection.disconnect("");
            EiraIRC.internalBus.post(new IRCConnectionFailedEvent(event.connection, new RuntimeException("Wrong username or invalid oauth token.")));
            MinecraftForge.EVENT_BUS.post(new IRCConnectionFailedEvent(event.connection, new RuntimeException("Wrong username or invalid oauth token.")));
            event.setCanceled(true);
            return;
        }
        // Parse Twitch user colors if this is a message from jtv on irc.twitch.tv
        if(event.sender != null && event.sender.getName().equals("jtv") && event.connection.getHost().equals(Globals.TWITCH_SERVER)) {
            if(event.message.startsWith("USERCOLOR ")) {
                int lastSpace = event.message.lastIndexOf(' ');
                String targetNick = event.message.substring(10, lastSpace);
                String targetColor = event.message.substring(lastSpace + 1);
                IRCUserImpl user = (IRCUserImpl) event.connection.getOrCreateUser(targetNick);
                user.setNameColor(IRCFormatting.getColorFromTwitch(targetColor));
            } else if(event.message.startsWith("SPECIALUSER ")) {
                int lastSpace = event.message.lastIndexOf(' ');
                String targetNick = event.message.substring(12, lastSpace);
                String targetType = event.message.substring(lastSpace + 1);
                if (targetType.equals("subscriber")) {
                    IRCUserImpl user = (IRCUserImpl) event.connection.getOrCreateUser(targetNick);
                    user.setSubscriber(true);
                }
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChannelChat(IRCChannelChatEvent event) {
        if(!event.isNotice && event.message.startsWith("!") && ((IRCBotImpl) event.bot).processCommand(event.channel, event.sender, event.message.substring(1))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onConnectionFailed(IRCConnectionFailedEvent event) {
        if(EiraIRC.instance.getConnectionManager().isLatestConnection(event.connection)) {
            for(IRCChannel channel : event.connection.getChannels()) {
                EiraIRC.instance.getChatSessionHandler().removeTargetChannel(channel);
            }
            EiraIRC.instance.getConnectionManager().removeConnection(event.connection);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDisconnected(IRCDisconnectEvent event) {
        if(EiraIRC.instance.getConnectionManager().isLatestConnection(event.connection)) {
            for(IRCChannel channel : event.connection.getChannels()) {
                EiraIRC.instance.getChatSessionHandler().removeTargetChannel(channel);
            }
            EiraIRC.instance.getConnectionManager().removeConnection(event.connection);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onIRCError(IRCErrorEvent event) {
        switch(event.numeric) {
            case IRCReplyCodes.ERR_NICKNAMEINUSE:
            case IRCReplyCodes.ERR_NICKCOLLISION:
                String failNick = event.args[1];
                String tryNick = failNick + "_";
                Utils.addMessageToChat(Utils.getLocalizedChatMessage("error.nickInUse", failNick, tryNick));
                event.connection.nick(tryNick);
                break;
            case IRCReplyCodes.ERR_ERRONEUSNICKNAME:
                Utils.addMessageToChat(Utils.getLocalizedChatMessage("error.nickInvalid", event.args[1]));
                ServerConfig serverConfig = ConfigHelper.getServerConfig(event.connection);
                if(serverConfig.getNick() != null) {
                    serverConfig.setNick(event.connection.getNick());
                }
                break;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChannelJoined(IRCChannelJoinedEvent event) {
        EiraIRC.instance.getChatSessionHandler().addTargetChannel(event.channel);
        if(ConfigHelper.getGeneralSettings(event.channel).getBoolean(GeneralBooleanComponent.AutoWho)) {
            Utils.sendUserList(null, event.connection, event.channel);
        }
        if(Compatibility.eiraMoticonsInstalled) {
            // Pre-load this channels sub badge
            EiraMoticonsAddon.getSubscriberBadge(event.channel);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChannelLeft(IRCChannelLeftEvent event) {
        EiraIRC.instance.getChatSessionHandler().removeTargetChannel(event.channel);
    }
}
