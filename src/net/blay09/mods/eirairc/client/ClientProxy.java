// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import net.blay09.mods.eirairc.CommonProxy;
import net.blay09.mods.eirairc.client.gui.OverlayNotification;
import net.blay09.mods.eirairc.client.gui.OverlayRecLive;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.NotificationConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

	private OverlayNotification notificationGUI;
	private OverlayRecLive recLiveGUI;
	
	@Override
	public void setupClient() {
		TickRegistry.registerTickHandler(new EiraTickHandler(), Side.CLIENT);
		notificationGUI = new OverlayNotification();
		recLiveGUI= new OverlayRecLive();
		ScreenshotManager.create();
	}

	@Override
	public void renderTick(float delta) {
		notificationGUI.updateAndRender(delta);
		recLiveGUI.updateAndRender(delta);
	}
	
	@Override
	public void publishNotification(NotificationType type, String text) {
		int config = 0;
		switch(type) {
		case FriendJoined: config = NotificationConfig.friendJoined; break;
		case PlayerMentioned: config = NotificationConfig.nameMentioned; break;
		case UserRecording: config = NotificationConfig.userRecording; break;
		case PrivateMessage: config = NotificationConfig.privateMessage; break;
		default:
		}
		if(config != NotificationConfig.VALUE_NONE && config != NotificationConfig.VALUE_SOUNDONLY) {
			notificationGUI.showNotification(type, text);
		}
		if(config == NotificationConfig.VALUE_TEXTANDSOUND || config == NotificationConfig.VALUE_SOUNDONLY) {
			Minecraft.getMinecraft().sndManager.playSoundFX(NotificationConfig.notificationSound, NotificationConfig.soundVolume, NotificationConfig.soundPitch);
		}
	}
	
	@Override
	public String getUsername() {
		return Minecraft.getMinecraft().getSession().getUsername();
	}
	
	@Override
	public boolean isIngame() {
		return Minecraft.getMinecraft().theWorld != null;
	}
	
	@Override
	public void onChannelJoined(IRCChannel channel) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(channel.hasTopic()) {
			Utils.sendLocalizedMessage(player, "irc.display.irc.topic", channel.getName(), channel.getTopic());
		}
		ServerConfig serverConfig = ConfigurationHandler.getServerConfig(channel.getConnection().getHost());
		ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
		if(channelConfig.isAutoWho()) {
			Utils.sendUserList(player, channel.getConnection(), channel);
		}
	}
}
