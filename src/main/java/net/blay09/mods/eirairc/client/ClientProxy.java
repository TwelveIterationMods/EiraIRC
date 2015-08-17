// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.blay09.mods.eirairc.CommonProxy;
import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.addon.DirectUploadHoster;
import net.blay09.mods.eirairc.addon.ImgurHoster;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.event.IRCChannelMessageEvent;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCRedirect;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayNotification;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.config.*;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

	private OverlayNotification notificationGUI;

	private static final KeyBinding[] keyBindings = new KeyBinding[] {
		ClientGlobalConfig.keyScreenshotShare,
		ClientGlobalConfig.keyOpenScreenshots,
		ClientGlobalConfig.keyToggleTarget,
		ClientGlobalConfig.keyOpenMenu
	};

	@Override
	public void init() {
		notificationGUI = new OverlayNotification();
		ScreenshotManager.create();
		FMLCommonHandler.instance().bus().register(new EiraTickHandler());

		for(KeyBinding keyBinding : keyBindings) {
			ClientRegistry.registerKeyBinding(keyBinding);
		}

		// Dirty hack to stop toggle target overshadowing player list key when they share the same key code (they do by default)
		KeyBinding keyBindPlayerList = Minecraft.getMinecraft().gameSettings.keyBindPlayerList;
		if(ClientGlobalConfig.keyToggleTarget.getKeyCode() == keyBindPlayerList.getKeyCode()) {
			Minecraft.getMinecraft().gameSettings.keyBindPlayerList = new KeyBinding(keyBindPlayerList.getKeyDescription(), keyBindPlayerList.getKeyCodeDefault(), keyBindPlayerList.getKeyCategory());
			Minecraft.getMinecraft().gameSettings.keyBindPlayerList.setKeyCode(keyBindPlayerList.getKeyCode());
		}
		
		EiraIRC.instance.registerCommands(ClientCommandHandler.instance, false);
		if(ClientGlobalConfig.registerShortCommands.get()) {
			IRCCommandHandler.registerQuickCommands(ClientCommandHandler.instance);
		}

		EiraGui.init(Minecraft.getMinecraft().getResourceManager());
		try {
			ConfigurationHandler.loadSuggestedChannels(Minecraft.getMinecraft().getResourceManager());
		} catch (IOException ignored) {}
	}

	@Override
	public void postInit() {
		super.postInit();
		EiraIRCAPI.registerUploadHoster(new DirectUploadHoster());
		EiraIRCAPI.registerUploadHoster(new ImgurHoster());
	}

	@Override
	public void renderTick(float delta) {
		notificationGUI.updateAndRender(delta);
	}
	
	@Override
	public void publishNotification(NotificationType type, String text) {
		NotificationStyle config = NotificationStyle.None;
		switch(type) {
		case FriendJoined: config = ClientGlobalConfig.ntfyFriendJoined.get(); break;
		case PlayerMentioned: config = ClientGlobalConfig.ntfyNameMentioned.get(); break;
		case PrivateMessage: config = ClientGlobalConfig.ntfyPrivateMessage.get(); break;
		default:
		}
		if(config != NotificationStyle.None && config != NotificationStyle.SoundOnly) {
			notificationGUI.showNotification(type, text);
		}
		if(config == NotificationStyle.TextAndSound || config == NotificationStyle.SoundOnly) {
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(ClientGlobalConfig.notificationSound.get()), ClientGlobalConfig.notificationSoundPitch.get())); // createPositionedSoundRecord
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
	public void loadConfig(File configDir, boolean reloadFile) {
		super.loadConfig(configDir, reloadFile);
		ClientGlobalConfig.load(configDir, reloadFile);
	}

	@Override
	public void handleRedirect(ServerConfig serverConfig) {
		TrustedServer server = ConfigurationHandler.getOrCreateTrustedServer(Utils.getServerAddress());
		if(server.isAllowRedirect()) {
			ConnectionManager.redirectTo(serverConfig, server.isRedirectSolo());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCRedirect(serverConfig));
		}
	}

	@Override
	public boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		return super.handleConfigCommand(sender, key, value) || ClientGlobalConfig.handleConfigCommand(sender, key, value);
	}

	@Override
	public String handleConfigCommand(ICommandSender sender, String key) {
		String result = super.handleConfigCommand(sender, key);
		if(result == null) {
			return ClientGlobalConfig.handleConfigCommand(sender, key);
		} else {
			return result;
		}
	}

	@Override
	public void addConfigOptionsToList(List<String> list, String option, boolean autoCompleteOption) {
		super.addConfigOptionsToList(list, option, autoCompleteOption);
		ClientGlobalConfig.addOptionsToList(list, option, autoCompleteOption);
	}

	@Override
	public boolean checkClientBridge(IRCChannelMessageEvent event) {
		if(event.sender != null && ClientGlobalConfig.clientBridge.get()) {
			for(Object obj : FMLClientHandler.instance().getClientPlayerEntity().sendQueue.playerInfoList) {
				GuiPlayerInfo playerInfo = (GuiPlayerInfo) obj;
				if(event.sender.getName().equalsIgnoreCase(playerInfo.name)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void saveConfig() {
		super.saveConfig();
		if (ClientGlobalConfig.thisConfig.hasChanged()) {
			ClientGlobalConfig.thisConfig.save();
		}
	}
}
