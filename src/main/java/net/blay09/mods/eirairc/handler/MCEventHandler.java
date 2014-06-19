// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IBotProfile;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.CompatibilityConfig;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.server.CommandBroadcast;
import net.minecraft.command.server.CommandEmote;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MCEventHandler {

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		String name = Utils.getNickIRC(event.player);
		String ircMessage = Utils.getLocalizedMessage("irc.display.mc.joinMsg", name);
		for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
			IIRCBot bot = connection.getBot();
			ServerConfig serverConfig = ConfigHelper.getServerConfig(connection);
			for(IIRCChannel channel : connection.getChannels()) {
				if(!bot.isReadOnly(channel) && bot.getBoolean(channel, IBotProfile.KEY_RELAYMCJOINLEAVE, false)) {
					channel.message(ircMessage);
				}
				if(channel.getTopic() != null) {
					Utils.sendLocalizedMessage(event.player, "irc.display.irc.topic", channel.getName(), channel.getTopic());
				}
				ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
				if(channelConfig.isAutoWho()) {
					Utils.sendUserList(event.player, connection, channel);
				}
			}
		}
	}

	@SubscribeEvent
	public void onServerCommand(CommandEvent event) {
		if(event.command instanceof CommandEmote) {
			if(event.sender instanceof EntityPlayer) {
				String emote = Utils.joinStrings(event.parameters, " ").trim();
				if(emote.length() == 0) {
					return;
				}
				String mcAlias = Utils.getNickGame((EntityPlayer) event.sender);
				IChatComponent chatComponent = new ChatComponentText("* " + mcAlias + " " + emote);
				if(DisplayConfig.emoteColor != null) {
					chatComponent.getChatStyle().setColor(Utils.getColorFormatting(DisplayConfig.emoteColor));
				}
				Utils.addMessageToChat(chatComponent);
				if(!MinecraftServer.getServer().isSinglePlayer()) {
					for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
						IIRCBot bot = connection.getBot();
						for(IIRCChannel channel : connection.getChannels()) {
							String ircMessage = Utils.formatMessage(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(channel)).ircChannelEmote, event.sender, emote, false, DisplayConfig.hidePlayerTags);
							if(!bot.isReadOnly(channel)) {
								channel.message(ircMessage);
							}
						}
					}
				}
				event.setCanceled(true);
			}
		} else if(event.command instanceof CommandBroadcast) {
			for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
				IIRCBot bot = connection.getBot();
				for(IIRCChannel channel : connection.getChannels()) {
					String ircMessage = Utils.formatMessage(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(channel)).ircBroadcastMessage, event.sender, Utils.joinStrings(event.parameters, " "), false, DisplayConfig.hidePlayerTags);
					if(!bot.isReadOnly(channel) && bot.getBoolean(channel, IBotProfile.KEY_RELAYBROADCASTS, true)) {
						channel.message(ircMessage);
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onClientChat(String text) {
		if(text.startsWith("/")) {
			if(text.startsWith("/me")) {
				return onClientEmote(text.substring(4));
			}
			return false;
		}
		EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
		if(EiraIRC.instance.getConnectionCount() > 0 && IRCCommandHandler.onChatCommand(sender, text, false)) {
			return true;
		}
		if(CompatibilityConfig.clientBridge) {
			for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
				IIRCBot bot = connection.getBot();
				for(IIRCChannel channel : connection.getChannels()) {
					if(!bot.isReadOnly(channel)) {
						channel.message(text + (!CompatibilityConfig.clientBridgeMessageToken.isEmpty() ? " " + CompatibilityConfig.clientBridgeMessageToken : ""));
					}
				}
			}
			return false;
		}
		String chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return false;
		}
		String[] target = chatTarget.split("/");
		IIRCConnection connection = EiraIRC.instance.getConnection(target[0]);
		if(connection != null) {
			IIRCBot bot = connection.getBot();
			IChatComponent chatComponent = null;
			if(target[1].startsWith("#")) {
				IIRCChannel targetChannel = connection.getChannel(target[1]);
				if(targetChannel != null) {
					targetChannel.message(text);
					chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetChannel)).mcSendChannelMessage, sender, text, true, DisplayConfig.hidePlayerTags);
				}
			} else {
				IIRCUser targetUser = connection.getUser(target[1]);
				if(targetUser != null) {
					targetUser.message(text);
					chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetUser)).mcSendPrivateMessage, sender, text, true, DisplayConfig.hidePlayerTags);
				}
			}
			Utils.addMessageToChat(chatComponent);
		}
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onClientEmote(String text) {
		EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
		if(CompatibilityConfig.clientBridge) {
			for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
				IIRCBot bot = connection.getBot();
				for(IIRCChannel channel : connection.getChannels()) {
					if(!bot.isReadOnly(channel)) {
						channel.message(IRCConnection.EMOTE_START + text + (!CompatibilityConfig.clientBridgeMessageToken.isEmpty() ? " " + CompatibilityConfig.clientBridgeMessageToken : "") + IRCConnection.EMOTE_END);
					}
				}
			}
			return false;
		}
		String chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return false;
		}
		String[] target = chatTarget.split("/");
		IIRCConnection connection = EiraIRC.instance.getConnection(target[0]);
		if(connection != null) {
			IIRCBot bot = connection.getBot();
			ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
			EnumChatFormatting emoteColor = null;
			IChatComponent chatComponent = null;
			if(target[1].startsWith("#")) {
				IIRCChannel targetChannel = connection.getChannel(target[1]);
				if(targetChannel != null) {
					ChannelConfig channelConfig = serverConfig.getChannelConfig(targetChannel);
					emoteColor = Utils.getColorFormatting(ConfigHelper.getEmoteColor(targetChannel));
					targetChannel.message(IRCConnection.EMOTE_START + text + IRCConnection.EMOTE_END);
					chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetChannel)).mcSendChannelEmote, sender, text, false, DisplayConfig.hidePlayerTags);
				}
			} else {
				IIRCUser targetUser = connection.getUser(target[1]);
				if(targetUser != null) {
					emoteColor = Utils.getColorFormatting(ConfigHelper.getEmoteColor(targetUser));
					targetUser.message(IRCConnection.EMOTE_START + text + IRCConnection.EMOTE_END);
					chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetUser)).mcSendPrivateEmote, sender, text, false, DisplayConfig.hidePlayerTags);
				}
			}
			if(emoteColor != null) {
				chatComponent.getChatStyle().setColor(emoteColor);
			}
			Utils.addMessageToChat(chatComponent);
		}
		return true;
	}
	
	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		IChatComponent senderComponent = event.player.func_145748_c_();
		senderComponent.getChatStyle().setColor(Utils.getColorFormattingForPlayer(event.player));
		event.component = new ChatComponentTranslation("chat.type.text", senderComponent, event.message);
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			String text = event.message;
			if(IRCCommandHandler.onChatCommand(event.player, text, true)) {
				event.setCanceled(true);
				return;
			}
			for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
				IIRCBot bot = connection.getBot();
				for(IIRCChannel channel : connection.getChannels()) {
					String ircMessage = Utils.formatMessage(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(channel)).ircChannelMessage, event.player, event.message, false, DisplayConfig.hidePlayerTags);
					if(!bot.isReadOnly(channel)) {
						channel.message(ircMessage);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.entityLiving instanceof EntityPlayer) {
			String name = Utils.getNickIRC((EntityPlayer) event.entityLiving);
			String ircMessage = event.entityLiving.func_110142_aN().func_151521_b().getUnformattedText();
			ircMessage = ircMessage.replaceAll(event.entityLiving.getCommandSenderName(), name);
			for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
				IIRCBot bot = connection.getBot();
				for(IIRCChannel channel : connection.getChannels()) {
					if(!bot.isReadOnly(channel) && bot.getBoolean(channel, IBotProfile.KEY_RELAYDEATHMESSAGES, false)) {
						channel.message(ircMessage);
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerNameFormat(PlayerEvent.NameFormat event) {
		event.displayname = Utils.getNickGame(event.entityPlayer);
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		String name = Utils.getNickIRC(event.player);
		String ircMessage = Utils.getLocalizedMessage("irc.display.mc.partMsg", name);
		for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
			IIRCBot bot = connection.getBot();
			for(IIRCChannel channel : connection.getChannels()) {
				if(!bot.isReadOnly(channel) && bot.getBoolean(channel, IBotProfile.KEY_RELAYMCJOINLEAVE, false)) {
					channel.message(ircMessage);
				}
			}
		}
	}

	public void onPlayerNickChange(String oldNick, String newNick) {
		String message = Utils.getLocalizedMessage("irc.display.mc.nickChange", oldNick, newNick);
		Utils.addMessageToChat(message);
		for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
			IIRCBot bot = connection.getBot();
			for(IIRCChannel channel : connection.getChannels()) {
				if(!bot.isReadOnly(channel)) {
					channel.message(message);
				}
			}
		}
	}

}
