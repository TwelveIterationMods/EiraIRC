// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import net.blay09.mods.eirairc.CommonProxy;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.upload.UploadManager;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChat;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayNotification;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayRecLive;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.client.upload.DirectUploadHoster;
import net.blay09.mods.eirairc.client.upload.ImgurHoster;
import net.blay09.mods.eirairc.config.NotificationConfig;
import net.blay09.mods.eirairc.util.NotificationType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {

	private GuiEiraChat eiraChat;
	private OverlayNotification notificationGUI;
	private OverlayRecLive recLiveGUI;
	
	@Override
	public void setupClient() {
		eiraChat = new GuiEiraChat();
		
		FMLCommonHandler.instance().bus().register(new EiraTickHandler(eiraChat));
		notificationGUI = new OverlayNotification();
		recLiveGUI= new OverlayRecLive();
		ScreenshotManager.create();
		
		UploadManager.registerUploadHoster(new DirectUploadHoster());
		UploadManager.registerUploadHoster(new ImgurHoster());
		
		EiraIRC.instance.registerCommands(ClientCommandHandler.instance, false);
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
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(NotificationConfig.notificationSound), NotificationConfig.soundVolume));
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
	
}
