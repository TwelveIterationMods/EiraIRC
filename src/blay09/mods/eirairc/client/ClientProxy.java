// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import net.minecraft.client.Minecraft;
import blay09.mods.eirairc.CommonProxy;
import blay09.mods.eirairc.NotificationType;
import blay09.mods.eirairc.config.NotificationConfig;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void setupClient() {
		KeyBindingRegistry.registerKeyBinding(new KeyBindingHandler());
		
		GuiNotification.instance = new GuiNotification();
		ScreenshotManager.create();
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
			GuiNotification.instance.showNotification(type, text);
		}
		if(config == NotificationConfig.VALUE_TEXTANDSOUND || config == NotificationConfig.VALUE_SOUNDONLY) {
			Minecraft.getMinecraft().sndManager.playSoundFX(NotificationConfig.notificationSound, NotificationConfig.soundVolume, NotificationConfig.soundPitch);
		}
	}
	
	@Override
	public String getUsername() {
		return Minecraft.getMinecraft().thePlayer.username;
	}
	
	
}
