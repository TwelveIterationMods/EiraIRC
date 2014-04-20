// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.command.IRCCommandHandler;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.irc.IRCUser;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.server.CommandBroadcast;
import net.minecraft.command.server.CommandEmote;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MCEventHandler {

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		String name = Utils.getAliasForPlayer(event.player);
		String ircMessage = Utils.getLocalizedMessage("irc.display.mc.joinMsg", name);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			for(IRCChannel channel : connection.getChannels()) {
				ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
				if(DisplayConfig.relayMinecraftJoinLeave && !channelConfig.isReadOnly() && channelConfig.relayMinecraftJoinLeave) {
					connection.sendChannelMessage(channel, ircMessage);
				}
				if(channel.hasTopic()) {
					Utils.sendLocalizedMessage(event.player, "irc.display.irc.topic", channel.getName(), channel.getTopic());
				}
				if(channelConfig.isAutoWho()) {
					Utils.sendUserList(event.player, connection, channel);
				}
			}
		}
	}

	@SubscribeEvent
	public void onCommand(CommandEvent event) {
		if(event.command instanceof CommandEmote) {
			if(event.sender instanceof EntityPlayer) {
				String emote = "";
				for(int i = 0; i < event.parameters.length; i++) {
					emote += " " + event.parameters[i];
				}
				emote = emote.trim();
				if(emote.length() == 0) {
					return;
				}
				String alias = Utils.getAliasForPlayer((EntityPlayer) event.sender);
				String mcMessage = (DisplayConfig.emoteColor != null ? Globals.COLOR_CODE_PREFIX + Utils.getColorCode(DisplayConfig.emoteColor) : "") + "* " + alias + " " + emote;
				Utils.addMessageToChat(mcMessage);
				if(!MinecraftServer.getServer().isSinglePlayer()) {
					String ircMessage = Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().ircChannelEmote, event.sender.getCommandSenderName(), alias, emote);
					for(IRCConnection connection : EiraIRC.instance.getConnections()) {
						ServerConfig serverConfig = Utils.getServerConfig(connection);
						for(IRCChannel channel : connection.getChannels()) {
							ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
							if(!channelConfig.isReadOnly()) {
								connection.sendChannelMessage(channel, ircMessage);
							}
						}
					}
				}
				event.setCanceled(true);
			}
		} else if(event.command instanceof CommandBroadcast) {
			String ircMessage = Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().ircBroadcastMessage, "Server", "Server", event.parameters[0]);
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				ServerConfig serverConfig = Utils.getServerConfig(connection);
				for(IRCChannel channel : connection.getChannels()) {
					ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
					if(!channelConfig.isReadOnly() && channelConfig.relayBroadcasts) {
						connection.sendChannelMessage(channel, ircMessage);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean onClientChat(String text) {
		EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
		if(EiraIRC.instance.getConnectionCount() > 0 && IRCCommandHandler.onChatCommand(sender, text, false)) {
			return false;
		}
		String chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return true;
		}
		String[] target = chatTarget.split("/");
		IRCConnection connection = EiraIRC.instance.getConnection(target[0]);
		if(connection != null) {
			String mcMessage = null;
			if(target[1].startsWith("#")) {
				IRCChannel targetChannel = connection.getChannel(target[1]);
				if(targetChannel != null) {
					connection.sendChannelMessage(targetChannel, text);
					mcMessage = Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().mcSendChannelMessage, connection.getHost(), targetChannel.getName(), null, Utils.getColorAliasForPlayer(sender), text);
				}
			} else {
				IRCUser targetUser = connection.getUser(target[1]);
				if(targetUser != null) {
					connection.sendPrivateMessage(targetUser, text);
					mcMessage = Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().mcSendPrivateMessage, connection.getHost(), targetUser.getName(), targetUser.getIdentifier(), Utils.getColorAliasForPlayer(sender), text);
				}
			}
			Utils.addMessageToChat(mcMessage);
		}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onClientEmote(String text) {
		EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
		String chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return true;
		}
		String[] target = chatTarget.split("/");
		IRCConnection connection = EiraIRC.instance.getConnection(target[0]);
		if(connection != null) {
			ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
			String emoteColor = Globals.COLOR_CODE_PREFIX + Utils.getColorCode(ConfigHelper.getEmoteColor(serverConfig));
			String mcMessage = null;
			if(target[1].startsWith("#")) {
				IRCChannel targetChannel = connection.getChannel(target[1]);
				if(targetChannel != null) {
					ChannelConfig channelConfig = serverConfig.getChannelConfig(targetChannel);
					emoteColor = Globals.COLOR_CODE_PREFIX + Utils.getColorCode(ConfigHelper.getEmoteColor(channelConfig));
					connection.sendChannelMessage(targetChannel, IRCConnection.EMOTE_START + text + IRCConnection.EMOTE_END);
					mcMessage = emoteColor + Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().mcSendChannelEmote, connection.getHost(), targetChannel.getName(), null, Utils.getAliasForPlayer(sender), text);
				}
			} else {
				IRCUser targetUser = connection.getUser(target[1]);
				if(targetUser != null) {
					connection.sendPrivateMessage(targetUser, IRCConnection.EMOTE_START + text + IRCConnection.EMOTE_END);
					mcMessage = emoteColor + Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().mcSendPrivateEmote, connection.getHost(), targetUser.getName(), targetUser.getIdentifier(), Utils.getAliasForPlayer(sender), text);
				}
			}
			Utils.addMessageToChat(mcMessage);
		}
		return false;
	}
	
	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		String ircNick = Utils.getAliasForPlayer(event.player);
		String mcNick = Utils.getColorAliasForPlayer(event.player);
		event.component = Utils.getLocalizedChatMessageNoPrefix("chat.type.text", mcNick, event.message);
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			String text = event.message;
			if(IRCCommandHandler.onChatCommand(event.player, text, true)) {
				event.setCanceled(true);
				return;
			}
			String ircMessage = Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().ircChannelMessage, event.player.getCommandSenderName(), ircNick, event.message);
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				ServerConfig serverConfig = Utils.getServerConfig(connection);
				for(IRCChannel channel : connection.getChannels()) {
					ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
					if(!channelConfig.isReadOnly()) {
						connection.sendChannelMessage(channel, ircMessage);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if(!DisplayConfig.relayDeathMessages) {
			return;
		}
		if(event.entityLiving instanceof EntityPlayer) {
			String name = Utils.getAliasForPlayer((EntityPlayer) event.entityLiving);
			String ircMessage = event.entityLiving.func_110142_aN().func_151521_b().getUnformattedText();
			ircMessage = ircMessage.replaceAll(event.entityLiving.getCommandSenderName(), name);
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				ServerConfig serverConfig = Utils.getServerConfig(connection);
				for(IRCChannel channel : connection.getChannels()) {
					ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
					if(!channelConfig.isReadOnly() && channelConfig.relayDeathMessages) {
						connection.sendChannelMessage(channel, ircMessage);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		if(!DisplayConfig.relayMinecraftJoinLeave) {
			return;
		}
		String name = Utils.getAliasForPlayer(event.player);
		String ircMessage = Utils.getLocalizedMessage("irc.display.mc.partMsg", name);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			for(IRCChannel channel : connection.getChannels()) {
				ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
				if(!channelConfig.isReadOnly() && channelConfig.relayMinecraftJoinLeave) {
					connection.sendChannelMessage(channel, ircMessage);
				}
			}
		}
	}

	public void onPlayerNickChange(String oldNick, String newNick) {
		String message = Utils.getLocalizedMessage("irc.display.mc.nickChange", oldNick, newNick);
		Utils.addMessageToChat(message);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			for(IRCChannel channel : connection.getChannels()) {
				ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
				if(!channelConfig.isReadOnly()) {
					connection.sendChannelMessage(channel, message);
				}
			}
		}
	}

}
