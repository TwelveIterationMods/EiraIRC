// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.blay09.mods.eirairc.CommonProxy;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.upload.UploadManager;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChat;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayNotification;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayRecLive;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.client.upload.DirectUploadHoster;
import net.blay09.mods.eirairc.client.upload.ImgurHoster;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.NotificationStyle;
import net.blay09.mods.eirairc.util.NotificationType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import org.lwjgl.input.Keyboard;
import org.omg.PortableInterceptor.ClientRequestInfo;

public class ClientProxy extends CommonProxy {

	private GuiEiraChat eiraChat;
	private OverlayNotification notificationGUI;
	private OverlayRecLive recLiveGUI;

	private static final KeyBinding[] keyBindings = new KeyBinding[] {
		ClientGlobalConfig.keyScreenshotShare,
		ClientGlobalConfig.keyOpenScreenshots,
		ClientGlobalConfig.keyToggleRecording,
		ClientGlobalConfig.keyToggleLive,
		ClientGlobalConfig.keyToggleTarget,
		ClientGlobalConfig.keyOpenMenu
	};

	@Override
	public void setupClient() {
		eiraChat = new GuiEiraChat();
		
		notificationGUI = new OverlayNotification();
		recLiveGUI= new OverlayRecLive();
		ScreenshotManager.create();
		FMLCommonHandler.instance().bus().register(new EiraTickHandler(eiraChat));

		for(int i = 0; i < keyBindings.length; i++) {
			ClientRegistry.registerKeyBinding(keyBindings[i]);
		}
		
		EiraIRC.instance.registerCommands(ClientCommandHandler.instance, false);
	}
	
	@Override
	public void renderTick(float delta) {
		notificationGUI.updateAndRender(delta);
		recLiveGUI.updateAndRender(delta);
	}
	
	@Override
	public void publishNotification(NotificationType type, String text) {
		NotificationStyle config = NotificationStyle.None;
		switch(type) {
		case FriendJoined: config = ClientGlobalConfig.ntfyFriendJoined; break;
		case PlayerMentioned: config = ClientGlobalConfig.ntfyNameMentioned; break;
		case UserRecording: config = ClientGlobalConfig.ntfyUserRecording; break;
		case PrivateMessage: config = ClientGlobalConfig.ntfyPrivateMessage; break;
		default:
		}
		if(config != NotificationStyle.None && config != NotificationStyle.SoundOnly) {
			notificationGUI.showNotification(type, text);
		}
		if(config == NotificationStyle.TextAndSound || config == NotificationStyle.SoundOnly) {
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(ClientGlobalConfig.notificationSound), ClientGlobalConfig.notificationSoundVolume));
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
