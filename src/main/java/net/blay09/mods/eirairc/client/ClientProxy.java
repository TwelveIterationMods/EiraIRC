// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import cpw.mods.fml.common.FMLCommonHandler;
import net.blay09.mods.eirairc.CommonProxy;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.gui.OverlayNotification;
import net.blay09.mods.eirairc.client.gui.OverlayRecLive;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.command.CommandConnect;
import net.blay09.mods.eirairc.command.CommandDisconnect;
import net.blay09.mods.eirairc.command.CommandIRC;
import net.blay09.mods.eirairc.command.CommandJoin;
import net.blay09.mods.eirairc.command.CommandNick;
import net.blay09.mods.eirairc.command.CommandPart;
import net.blay09.mods.eirairc.command.CommandServIRC;
import net.blay09.mods.eirairc.command.CommandWho;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.NotificationConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

	private OverlayNotification notificationGUI;
	private OverlayRecLive recLiveGUI;
	
	@Override
	public void setupClient() {
		FMLCommonHandler.instance().bus().register(new EiraTickHandler());
		notificationGUI = new OverlayNotification();
		recLiveGUI= new OverlayRecLive();
		ScreenshotManager.create();
		
//		EiraIRC.instance.registerCommands(ClientCommandHandler.instance, false);
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
