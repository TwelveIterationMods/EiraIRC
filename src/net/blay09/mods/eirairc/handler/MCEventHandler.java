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
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandServerEmote;
import net.minecraft.command.CommandServerSay;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MCEventHandler implements IPlayerTracker, IConnectionHandler {

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		String name = Utils.getNickIRC(player);
		String ircMessage = Utils.getLocalizedMessage("irc.display.mc.joinMsg", name);
		for(IIRCConnection connection : EiraIRC.instance.getConnections()) {
			IIRCBot bot = connection.getBot();
			ServerConfig serverConfig = ConfigHelper.getServerConfig(connection);
			for(IIRCChannel channel : connection.getChannels()) {
				if(!bot.isReadOnly(channel) && bot.getBoolean(channel, IBotProfile.KEY_RELAYMCJOINLEAVE, false)) {
					channel.message(ircMessage);
				}
				if(channel.getTopic() != null) {
					Utils.sendLocalizedMessage(player, "irc.display.irc.topic", channel.getName(), channel.getTopic());
				}
				ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
				if(channelConfig.isAutoWho()) {
					Utils.sendUserList(player, connection, channel);
				}
			}
		}
	}

	@ForgeSubscribe
	@SideOnly(Side.SERVER)
	public void onServerCommand(CommandEvent event) {
		if(event.command instanceof CommandServerEmote) {
			if(event.sender instanceof EntityPlayer) {
				String emote = Utils.joinStrings(event.parameters, " ").trim();
				if(emote.length() == 0) {
					return;
				}
				String mcAlias = Utils.getNickGame((EntityPlayer) event.sender);
				String ircAlias = Utils.getNickIRC((EntityPlayer) event.sender);
				String mcMessage = (DisplayConfig.emoteColor != null ? Globals.COLOR_CODE_PREFIX + Utils.getColorCode(DisplayConfig.emoteColor) : "") + "* " + mcAlias + " " + emote;
				Utils.addMessageToChat(mcMessage);
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
		} else if(event.command instanceof CommandServerSay) {
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
			if(text.startsWith("/me") && text.length() > 3) {
				return onClientEmote(text.substring(3));
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
			String mcMessage = null;
			ChatMessageComponent chatComponent;
			if(target[1].startsWith("#")) {
				IIRCChannel targetChannel = connection.getChannel(target[1]);
				if(targetChannel == null) {
					return true;
				}
				targetChannel.message(text);
				chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetChannel)).mcSendChannelMessage, sender, text, true, DisplayConfig.hidePlayerTags);
			} else {
				IIRCUser targetUser = connection.getUser(target[1]);
				if(targetUser == null) {
					return true;
				}
				targetUser.message(text);
				chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetUser)).mcSendPrivateMessage, sender, text, true, DisplayConfig.hidePlayerTags);
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
			EnumChatFormatting emoteColor;
			ChatMessageComponent chatComponent;
			if(target[1].startsWith("#")) {
				IIRCChannel targetChannel = connection.getChannel(target[1]);
				if(targetChannel == null) {
					return true;
				}
				emoteColor = Utils.getColorFormatting(ConfigHelper.getEmoteColor(targetChannel));
				targetChannel.message(IRCConnection.EMOTE_START + text + IRCConnection.EMOTE_END);
				chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetChannel)).mcSendChannelEmote, sender, text, false, DisplayConfig.hidePlayerTags);
			} else {
				IIRCUser targetUser = connection.getUser(target[1]);
				if(targetUser == null) {
					return true;
				}
				emoteColor = Utils.getColorFormatting(ConfigHelper.getEmoteColor(targetUser));
				targetUser.message(IRCConnection.EMOTE_START + text + IRCConnection.EMOTE_END);
				chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetUser)).mcSendPrivateEmote, sender, text, false, DisplayConfig.hidePlayerTags);
			}
			if(emoteColor != null) {
				chatComponent.setColor(emoteColor);
			}
			Utils.addMessageToChat(chatComponent);
		}
		return true;
	}
	
	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void NameFormat(PlayerEvent.NameFormat event) {
		event.displayname = Utils.getAliasForPlayer(event.entityPlayer);
	}
	
	@ForgeSubscribe
	public void onServerChat(ServerChatEvent event) {
		String ircNick = Utils.getNickIRC(event.player);
		String mcNick = Utils.addColorCodes(event.player.getDisplayName(), Utils.getColorCodeForPlayer(event.player));
		event.component = Utils.getLocalizedChatMessageNoPrefix("chat.type.text", mcNick, event.message);
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
	
	@ForgeSubscribe
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.entityLiving instanceof EntityPlayer) {
			String name = Utils.getNickIRC((EntityPlayer) event.entityLiving);
			String ircMessage = event.entityLiving.func_110142_aN().func_94546_b().toString();
			ircMessage = ircMessage.replaceAll(event.entityLiving.getEntityName(), name);
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
	
	@Override
	public void onPlayerLogout(EntityPlayer player) {
		String name = Utils.getNickIRC(player);
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

	@Override
	public void connectionClosed(INetworkManager manager) {
		if(!GlobalConfig.persistentConnection) {
			if(MinecraftServer.getServer() == null || MinecraftServer.getServer().isSinglePlayer()) {
				EiraIRC.instance.stopIRC();
			}
		}
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
		if(!GlobalConfig.persistentConnection || !EiraIRC.instance.isIRCRunning()) {
			EiraIRC.instance.startIRC();
		}
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler,
			INetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler,
			INetworkManager manager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server,
			int port, INetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler,
			MinecraftServer server, INetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}
}
