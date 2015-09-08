package net.blay09.mods.eirairc.addon;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.blay09.mods.eirairc.api.config.IConfigProperty;
import net.blay09.mods.eirairc.api.event.*;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayJoinLeave;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

public class FancyOverlay {

    private final OverlayJoinLeave overlay;
    private IConfigProperty<Boolean> enabled;
    private IConfigProperty<Integer> visibleTime;
    private IConfigProperty<Float> scale;

    public FancyOverlay() {
        overlay = new OverlayJoinLeave(Minecraft.getMinecraft(), Minecraft.getMinecraft().fontRenderer);

        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onInitConfig(InitConfigEvent.ClientGlobalSettings event) {
        enabled = event.config.registerProperty("eirairc", "enableFancyOverlay", "eirairc:config.property.enableFancyOverlay", true);
        visibleTime = event.config.registerProperty("eirairc", "fancyOverlayLifetime", "eirairc:config.property.fancyOverlayLifetime", 240);
        visibleTime.setMinMax(120, 2400);
        scale = event.config.registerProperty("eirairc", "fancyOverlayScale", "eirairc:config.property.fancyOverlayScale", 0.5f);
        scale.setMinMax(0.5f, 1f);
        overlay.setVisibleTime(visibleTime);
        overlay.setScale(scale);
    }

    @SubscribeEvent
    public void onIRCUserJoin(IRCUserJoinEvent event) {
        if(enabled.get() && SharedGlobalConfig.botSettings.relayIRCJoinLeave.get()) {
            String format = ConfigHelper.getBotSettings(event.channel).getMessageFormat().mcUserJoin;
            overlay.addMessage(MessageFormat.formatChatComponent(format, event.connection, event.channel, event.user, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onIRCNickChange(IRCUserNickChangeEvent event) {
        if(enabled.get() && SharedGlobalConfig.botSettings.relayNickChanges.get()) {
            String format = ConfigHelper.getBotSettings(event.user).getMessageFormat().mcUserNickChange;
            format = format.replace("{OLDNICK}", event.oldNick);
            overlay.addMessage(MessageFormat.formatChatComponent(format, event.connection, null, event.user, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onIRCUserLeave(IRCUserLeaveEvent event) {
        if(enabled.get() && SharedGlobalConfig.botSettings.relayIRCJoinLeave.get()) {
            String format = ConfigHelper.getBotSettings(event.channel).getMessageFormat().mcUserLeave;
            overlay.addMessage(MessageFormat.formatChatComponent(format, event.connection, event.channel, event.user, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onConnectedEvent(IRCConnectEvent event) {
        if(enabled.get()) {
            overlay.addMessage(new ChatComponentTranslation("eirairc:general.connected", event.connection.getHost()));
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onConnectionFailed(IRCConnectionFailedEvent event) {
        if(enabled.get()) {
            overlay.addMessage(new ChatComponentTranslation("eirairc:error.couldNotConnect", event.connection.getHost(), event.exception));
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onReconnecting(IRCReconnectEvent event) {
        if(enabled.get()) {
            overlay.addMessage(new ChatComponentTranslation("eirairc:general.reconnecting", event.connection.getHost(), event.waitingTime / 1000));
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onDisconnectedEvent(IRCDisconnectEvent event) {
        if(enabled.get()) {
            overlay.addMessage(new ChatComponentTranslation("eirairc:general.disonnected", event.connection.getHost()));
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onIRCUserQuit(IRCUserQuitEvent event) {
        if(enabled.get() && SharedGlobalConfig.botSettings.relayIRCJoinLeave.get()) {
            String format = ConfigHelper.getBotSettings(event.user).getMessageFormat().mcUserQuit;
            overlay.addMessage(MessageFormat.formatChatComponent(format, event.connection, null, event.user, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote));
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if(event.type == RenderGameOverlayEvent.ElementType.CHAT) {
            overlay.updateAndRender(event.partialTicks);
        }
    }

}
