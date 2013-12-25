// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import net.minecraft.client.Minecraft;
import blay09.mods.eirairc.CommonProxy;
import blay09.mods.eirairc.client.gui.OverlayNotification;
import blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import blay09.mods.eirairc.config.NotificationConfig;
import blay09.mods.eirairc.util.NotificationType;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

	private OverlayNotification notificationGUI;
	
	@Override
	public void setupClient() {
		TickRegistry.registerTickHandler(new EiraTickHandler(), Side.CLIENT);
		notificationGUI = new OverlayNotification();
		ScreenshotManager.create();
	}

	@Override
	public void renderTick(float delta) {
		notificationGUI.updateAndRender(delta);
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
		return Minecraft.getMinecraft().thePlayer.username;
	}
	
	@Override
	public boolean isIngame() {
		return Minecraft.getMinecraft().theWorld != null;
	}
}
