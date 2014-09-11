// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.BotProfile;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.event.RelayChat;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.CompatibilityConfig;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandServerEmote;
import net.minecraft.command.CommandServerSay;
import net.minecraft.command.ICommandSender;
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
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			IRCBot bot = connection.getBot();
			ServerConfig serverConfig = ConfigHelper.getServerConfig(connection);
			for(IRCChannel channel : connection.getChannels()) {
				if(!bot.isReadOnly(channel) && bot.getBoolean(channel, BotProfile.KEY_RELAYMCJOINLEAVE, false)) {
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
					relayChatServer(event.sender, emote, true, false, null);
				}
				event.setCanceled(true);
			}
		} else if(event.command instanceof CommandServerSay) {
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				IRCBot bot = connection.getBot();
				for(IRCChannel channel : connection.getChannels()) {
					String ircMessage = Utils.formatMessage(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(channel)).ircBroadcastMessage, channel, event.sender, Utils.joinStrings(event.parameters, " "), false, DisplayConfig.hidePlayerTags, true);
					if(!bot.isReadOnly(channel) && bot.getBoolean(channel, BotProfile.KEY_RELAYBROADCASTS, true)) {
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
			relayChatClient(text, false, false, null, true);
			return false;
		}
		String chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return false;
		}
		String[] target = chatTarget.split("/");
		IRCConnection connection = EiraIRC.instance.getConnection(target[0]);
		if(connection != null) {
			IRCBot bot = connection.getBot();
			String mcMessage = null;
			IRCContext context;
			ChatMessageComponent chatComponent;
			if(target[1].startsWith("#")) {
				IRCChannel targetChannel = connection.getChannel(target[1]);
				if(targetChannel == null) {
					return true;
				}
				context = targetChannel;
				chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetChannel)).mcSendChannelMessage, context, sender, text, true, DisplayConfig.hidePlayerTags, false);
			} else {
				IRCUser targetUser = connection.getUser(target[1]);
				if(targetUser == null) {
					return true;
				}
				context = targetUser;
				chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetUser)).mcSendPrivateMessage, context, sender, text, true, DisplayConfig.hidePlayerTags, false);
			}
			relayChatClient(text, false, false, context, false);
			Utils.addMessageToChat(chatComponent);
		}
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onClientEmote(String text) {
		EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
		if(CompatibilityConfig.clientBridge) {
			relayChatClient(text, true, false, null, true);
			return false;
		}
		String chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return false;
		}
		String[] target = chatTarget.split("/");
		IRCConnection connection = EiraIRC.instance.getConnection(target[0]);
		if(connection != null) {
			IRCBot bot = connection.getBot();
			IRCContext context;
			EnumChatFormatting emoteColor;
			ChatMessageComponent chatComponent;
			if(target[1].startsWith("#")) {
				IRCChannel targetChannel = connection.getChannel(target[1]);
				if(targetChannel == null) {
					return true;
				}
				context = targetChannel;
				emoteColor = Utils.getColorFormatting(ConfigHelper.getEmoteColor(targetChannel));
				chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetChannel)).mcSendChannelEmote, context, sender, text, false, DisplayConfig.hidePlayerTags, false);
			} else {
				IRCUser targetUser = connection.getUser(target[1]);
				if(targetUser == null) {
					return true;
				}
				context = targetUser;
				emoteColor = Utils.getColorFormatting(ConfigHelper.getEmoteColor(targetUser));
				chatComponent = Utils.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetUser)).mcSendPrivateEmote, context, sender, text, false, DisplayConfig.hidePlayerTags, false);
			}
			relayChatClient(text, true, false, context, false);
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
			if(IRCCommandHandler.onChatCommand(event.player, event.message, true)) {
				event.setCanceled(true);
				return;
			}
			relayChatServer(event.player, event.message, false, false, null);
		}
	}
	
	@ForgeSubscribe
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.entityLiving instanceof EntityPlayer) {
			String name = Utils.getNickIRC((EntityPlayer) event.entityLiving);
			String ircMessage = event.entityLiving.func_110142_aN().func_94546_b().toString();
			ircMessage = ircMessage.replaceAll(event.entityLiving.getEntityName(), name);
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				IRCBot bot = connection.getBot();
				for(IRCChannel channel : connection.getChannels()) {
					if(!bot.isReadOnly(channel) && bot.getBoolean(channel, BotProfile.KEY_RELAYDEATHMESSAGES, false)) {
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
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			IRCBot bot = connection.getBot();
			for(IRCChannel channel : connection.getChannels()) {
				if(!bot.isReadOnly(channel) && bot.getBoolean(channel, BotProfile.KEY_RELAYMCJOINLEAVE, false)) {
					channel.message(ircMessage);
				}
			}
		}
	}

	public void onPlayerNickChange(String oldNick, String newNick) {
		String message = Utils.getLocalizedMessage("irc.display.mc.nickChange", oldNick, newNick);
		Utils.addMessageToChat(message);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			IRCBot bot = connection.getBot();
			for(IRCChannel channel : connection.getChannels()) {
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
	
	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void relayChatClient(RelayChat event) {
		relayChatClient(event.message, event.isEmote, event.isNotice,
				event.target, CompatibilityConfig.clientBridge);
	}

	private void relayChatClient(String message, boolean isEmote,
			boolean isNotice, IRCContext target, boolean clientBridge) {
		if (target != null) {
			IRCConnection connection = target.getConnection();
			IRCBot bot = connection.getBot();
			if (!bot.isReadOnly(target)) {
				String ircMessage = message;
				if (isEmote) {
					ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage
							+ IRCConnectionImpl.EMOTE_END;
				}
				if (isNotice) {
					target.notice(ircMessage);
				} else {
					target.message(ircMessage);
				}
			}
		} else {
			if (clientBridge) {
				String ircMessage = message;
				if (isEmote) {
					ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage
							+ IRCConnectionImpl.EMOTE_END;
				}
				if (!CompatibilityConfig.clientBridgeMessageToken.isEmpty()) {
					ircMessage = ircMessage + " "
							+ CompatibilityConfig.clientBridgeMessageToken;
				}
				for (IRCConnection connection : EiraIRC.instance
						.getConnections()) {
					IRCBot bot = connection.getBot();
					for (IRCChannel channel : connection.getChannels()) {
						if (!bot.isReadOnly(channel)) {
							if (isNotice) {
								channel.notice(ircMessage);
							} else {
								channel.message(ircMessage);
							}
						}
					}
				}
			} else {
				String chatTarget = EiraIRC.instance.getChatSessionHandler()
						.getChatTarget();
				if (chatTarget == null) {
					return;
				}
				String[] targetArr = chatTarget.split("/");
				IRCConnection connection = EiraIRC.instance
						.getConnection(targetArr[0]);
				if (connection != null) {
					IRCBot bot = connection.getBot();
					IRCContext context;
					if (targetArr[1].startsWith("#")) {
						IRCChannel targetChannel = connection
								.getChannel(targetArr[1]);
						if (targetChannel == null) {
							return;
						}
						context = targetChannel;
					} else {
						IRCUser targetUser = connection.getUser(targetArr[1]);
						if (targetUser == null) {
							return;
						}
						context = targetUser;
					}
					if (!bot.isReadOnly(context)) {
						String ircMessage = message;
						if (isEmote) {
							ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage
									+ IRCConnectionImpl.EMOTE_END;
						}
						if (isNotice) {
							context.notice(ircMessage);
						} else {
							context.message(ircMessage);
						}
					}
				}
			}
		}
	}

	@ForgeSubscribe
	@SideOnly(Side.SERVER)
	public void relayChatServer(RelayChat event) {
		relayChatServer(event.sender, event.message, event.isEmote,
				event.isNotice, event.target);
	}

	private void relayChatServer(ICommandSender sender, String message,
			boolean isEmote, boolean isNotice, IRCContext target) {
		if (target != null) {
			IRCConnection connection = target.getConnection();
			IRCBot bot = connection.getBot();
			if (!bot.isReadOnly(target)) {
				String format = Utils.getMessageFormat(bot, target, isEmote);
				String ircMessage = Utils.formatMessage(format, target, sender,
						message, false, DisplayConfig.hidePlayerTags, true);
				if (isEmote) {
					ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage
							+ IRCConnectionImpl.EMOTE_END;
				}
				if (isNotice) {
					target.notice(ircMessage);
				} else {
					target.message(ircMessage);
				}
			}
		} else {
			for (IRCConnection connection : EiraIRC.instance.getConnections()) {
				IRCBot bot = connection.getBot();
				for (IRCChannel channel : connection.getChannels()) {
					String format = Utils.getMessageFormat(bot, channel,
							isEmote);
					String ircMessage = Utils.formatMessage(format, channel, sender,
							message, false, DisplayConfig.hidePlayerTags, true);
					if (isEmote) {
						ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage
								+ IRCConnectionImpl.EMOTE_END;
					}
					if (!bot.isReadOnly(channel)) {
						if (isNotice) {
							channel.notice(ircMessage);
						} else {
							channel.message(ircMessage);
						}
					}
				}
			}
		}
	}
}
