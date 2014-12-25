// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.blay09.mods.eirairc.CommonProxy;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCRedirect;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChat;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayNotification;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.*;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.List;

public class ClientProxy extends CommonProxy {

	private GuiEiraChat eiraChat;
	private OverlayNotification notificationGUI;

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

	@Override
	public void loadLegacyConfig(File configDir, Configuration legacyConfig) {
		super.loadLegacyConfig(configDir, legacyConfig);
		ClientGlobalConfig.loadLegacy(configDir, legacyConfig);
	}

	@Override
	public void loadConfig(File configDir) {
		super.loadConfig(configDir);
		ClientGlobalConfig.load(configDir);
	}

	@Override
	public void handleRedirect(ServerConfig serverConfig) {
		TrustedServer server = ConfigurationHandler.getOrCreateTrustedServer(Utils.getServerAddress());
		if(server.isAllowRedirect()) {
			Utils.redirectTo(serverConfig, server.isRedirectSolo());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCRedirect(serverConfig));
		}
	}

	@Override
	public boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		if(!super.handleConfigCommand(sender, key, value)) {
			return ClientGlobalConfig.handleConfigCommand(sender, key, value);
		} else {
			return true;
		}
	}

	@Override
	public String handleConfigCommand(ICommandSender sender, String key) {
		if(super.handleConfigCommand(sender, key) == null) {
			return ClientGlobalConfig.handleConfigCommand(sender, key);
		} else {
			return null;
		}
	}

	@Override
	public void addConfigOptionsToList(List<String> list, String option) {
		super.addConfigOptionsToList(list, option);
		ClientGlobalConfig.addOptionsToList(list, option);
	}
}
