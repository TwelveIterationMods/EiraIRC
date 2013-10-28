// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.ConfigHelper;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IIRCEventHandler;
import blay09.mods.eirairc.irc.IRCChannel;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.irc.IRCReplyCodes;
import blay09.mods.eirairc.irc.IRCUser;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IRCEventHandler implements IIRCEventHandler, IPlayerTracker, IConnectionHandler {

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		String name = Utils.getAliasForPlayer(player);
		String ircMessage = Utils.getLocalizedMessage("irc.display.mc.joinMsg", name);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			for(IRCChannel channel : connection.getChannels()) {
				ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
				if(GlobalConfig.relayMinecraftJoinLeave && !channelConfig.isReadOnly() && channelConfig.relayMinecraftJoinLeave) {
					connection.sendChannelMessage(channel, ircMessage);
				}
				if(channel.hasTopic()) {
					Utils.sendLocalizedMessage(player, "irc.display.irc.topic", channel.getName(), channel.getTopic());
				}
				if(channelConfig.isAutoWho()) {
					Utils.sendUserList(player, connection, channel);
				}
			}
		}
	}

	@ForgeSubscribe
	public void onCommand(CommandEvent event) {
		if(event.command.getCommandName().equals("me")) {
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
				String mcMessage = (GlobalConfig.emoteColor != null ? Utils.getColorCode(GlobalConfig.emoteColor) : "") + "* " + alias + " " + emote;
				Utils.addMessageToChat(mcMessage);
				if(!MinecraftServer.getServer().isSinglePlayer()) {
					String ircMessage = Utils.formatMessage(GlobalConfig.getDisplayFormatConfig().ircChannelEmote, event.sender.getCommandSenderName(), alias, emote);
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
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean onClientChat(String text) {
		EnumChatTarget chatTarget = EiraIRC.instance.getChatTarget();
		if(chatTarget == EnumChatTarget.ChannelOnly) {
			String target = EiraIRC.instance.getTargetChannel();
			String[] channel = target.split(":");
			IRCConnection connection = EiraIRC.instance.getConnection(channel[0]);
			if(connection != null) {
				String mcMessage = null;
				if(channel[1].startsWith("#")) {
					connection.sendChannelMessage(connection.getChannel(channel[1]), text);
					mcMessage = "[" + channel[1] + "] <" + Utils.getColorAliasForPlayer(Minecraft.getMinecraft().thePlayer) + "> " + text;
				} else {
					connection.sendPrivateMessage(connection.getUser(channel[1]), text);
					mcMessage = "[-> " + channel[1] + "] <" + Utils.getColorAliasForPlayer(Minecraft.getMinecraft().thePlayer) + "> " + text;
				}
				Utils.addMessageToChat(mcMessage);
			}
			return false;
		}
		boolean doMC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.MinecraftOnly;
		boolean doIRC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.IRCOnly;
		if(!doMC) {
			Utils.addMessageToChat("[IRC] <" + Utils.getColorAliasForPlayer(Minecraft.getMinecraft().thePlayer) + "> " + text);
		}
		if(doIRC) {
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				ServerConfig serverConfig = Utils.getServerConfig(connection);
				for(IRCChannel channel : connection.getChannels()) {
					ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
					if(!channelConfig.isReadOnly()) {
						connection.sendChannelMessage(channel, text);
					}
				}
			}
		}
		return doMC;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onClientEmote(String text) {
		EnumChatTarget chatTarget = EiraIRC.instance.getChatTarget();
		if(chatTarget == EnumChatTarget.ChannelOnly) {
			String target = EiraIRC.instance.getTargetChannel();
			String[] channel = target.split(":");
			IRCConnection connection = EiraIRC.instance.getConnection(channel[0]);
			if(connection != null) {
				connection.sendChannelMessage(connection.getChannel(channel[1]), "ACTION " + text + "");
				Utils.addMessageToChat("[" + channel[1] + "] * " + Utils.getAliasForPlayer(Minecraft.getMinecraft().thePlayer) + " " + text);
			}
			return false;
		}
		boolean doMC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.MinecraftOnly;
		boolean doIRC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.IRCOnly;
		if(!doMC) {
			Utils.addMessageToChat("[IRC] * " + Utils.getAliasForPlayer(Minecraft.getMinecraft().thePlayer) + " " + text);
		}
		if(doIRC) {
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				ServerConfig serverConfig = Utils.getServerConfig(connection);
				for(IRCChannel channel : connection.getChannels()) {
					ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
					if(!channelConfig.isReadOnly()) {
						connection.sendChannelMessage(channel, "ACTION " + text + "");
					}
				}
			}
		}
		return doMC;
	}
	
	@ForgeSubscribe
	public void onServerChat(ServerChatEvent event) {
		String nick = Utils.getColorAliasForPlayer(event.player);
		event.line = "<" + nick + "> " + event.message;
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			String ircMessage = Utils.formatMessage(GlobalConfig.getDisplayFormatConfig().ircChannelMessage, event.player.username, nick, event.message);
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
	
	@ForgeSubscribe
	public void onPlayerDeath(LivingDeathEvent event) {
		if(!GlobalConfig.relayDeathMessages) {
			return;
		}
		if(event.entityLiving instanceof EntityPlayer) {
			String name = Utils.getAliasForPlayer((EntityPlayer) event.entityLiving);
			String ircMessage = event.entityLiving.field_94063_bt.func_94546_b();
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
	
	@Override
	public void onPlayerLogout(EntityPlayer player) {
		if(!GlobalConfig.relayMinecraftJoinLeave) {
			return;
		}
		String name = Utils.getAliasForPlayer(player);
		String ircMessage = Utils.getLocalizedMessage("irc.display.irc.partMsg", name);
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

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
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

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
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
			if(GlobalConfig.nick.startsWith(ConfigurationHandler.DEFAULT_NICK)) {
				GlobalConfig.nick = Minecraft.getMinecraft().thePlayer.username;
			}
			EiraIRC.instance.startIRC();
		}
	}

	@Override
	public void onConnected(IRCConnection connection) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.connected", connection.getHost());
		Utils.addMessageToChat(mcMessage);
		ServerConfig serverConfig = Utils.getServerConfig(connection);
		Utils.doNickServ(connection, serverConfig);
		for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
			if(channelConfig.isAutoJoin()) {
				connection.join(channelConfig.getName(), channelConfig.getPassword());
			}
		}
	}

	@Override
	public void onDisconnected(IRCConnection connection) {
		String mcMessage = Utils.getLocalizedMessage("irc.basic.disconnected", connection.getHost());
		Utils.addMessageToChat(mcMessage);
	}

	@Override
	public void onNickChange(IRCConnection connection, IRCUser user, String nick) {
		if(!GlobalConfig.relayNickChanges) {
			return;
		}
		String mcMessage = Utils.getLocalizedMessage("irc.display.irc.nickChange", connection.getHost(), user.getNick(), nick);
		Utils.addMessageToChat(mcMessage);
	}

	@Override
	public void onUserJoin(IRCConnection connection, IRCUser user, IRCChannel channel) {
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(GlobalConfig.relayIRCJoinLeave && !channelConfig.isMuted() && channelConfig.relayIRCJoinLeave) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.joinMsg", channel.getName(), user.getNick());
			Utils.addMessageToChat(mcMessage);
		}
		if(channelConfig.isAutoWho()) {
			Utils.sendUserList(connection, user);
		}
	}

	@Override
	public void onUserPart(IRCConnection connection, IRCUser user, IRCChannel channel, String quitMessage) {
		if(!GlobalConfig.relayIRCJoinLeave) {
			return;
		}
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(!channelConfig.isMuted() && channelConfig.relayIRCJoinLeave) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.partMsg", channel.getName(), user.getNick());
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onUserQuit(IRCConnection connection, IRCUser user, String quitMessage) {
		if(!GlobalConfig.relayIRCJoinLeave) {
			return;
		}
		ServerConfig serverConfig = Utils.getServerConfig(connection);
		boolean hasFlags = false;
		for(IRCChannel channel : user.getChannels()) {
			ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
			if(!channelConfig.isMuted() && channelConfig.relayIRCJoinLeave) {
				hasFlags = true;
				break;
			}
		}
		if(hasFlags) {
			String mcMessage = Utils.getLocalizedMessage("irc.quitMsgIRC", connection.getHost(), user.getNick());
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onPrivateEmote(IRCConnection connection, IRCUser user, String message) {
		if(!GlobalConfig.allowPrivateMessages) {
			return;
		}
		ServerConfig serverConfig = Utils.getServerConfig(connection);
		if(serverConfig.allowsPrivateMessages()) {
			if(GlobalConfig.enableLinkFilter) {
				message = Utils.filterLinks(message);
			}
			message = Utils.filterCodes(message);
			String emoteColor = ConfigHelper.getEmoteColor(serverConfig);
			String mcMessage = (emoteColor != null ? Utils.getColorCode(emoteColor) : "") +Utils.formatMessage(GlobalConfig.getDisplayFormatConfig().mcPrivateEmote, connection, user.getUsername(), user.getNick(), message);
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onPrivateMessage(IRCConnection connection, IRCUser user, String message) {
		if(!GlobalConfig.allowPrivateMessages) {
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.msg.disabled"));
			return;
		}
		ServerConfig serverConfig = Utils.getServerConfig(connection);
		if(serverConfig.allowsPrivateMessages()) {
			if(serverConfig.isClientSide()) {
				if(GlobalConfig.enableLinkFilter) {
					message = Utils.filterLinks(message);
				}
				message = Utils.filterCodes(message);
				String mcMessage = Utils.formatMessage(GlobalConfig.getDisplayFormatConfig().mcPrivateMessage, connection, user.getUsername(), Utils.getColoredName(user.getNick(), ConfigHelper.getIRCColor(serverConfig)), message);
				Utils.addMessageToChat(mcMessage);
				EiraIRC.instance.addPrivateTarget(user.getNick());
			} else {
				onIRCBotPrivateCommand(connection, user, message);
			}
		} else {
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.msg.disabled"));
		}
	}

	@Override
	public void onChannelEmote(IRCConnection connection, IRCChannel channel, IRCUser user, String message) {
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(!channelConfig.isMuted()) {
			if(GlobalConfig.enableLinkFilter) {
				message = Utils.filterLinks(message);
			}
			message = Utils.filterCodes(message);
			String emoteColor = ConfigHelper.getEmoteColor(channelConfig);
			String mcMessage = (emoteColor != null ? Utils.getColorCode(emoteColor) : "") + Utils.formatMessage(GlobalConfig.getDisplayFormatConfig().mcChannelEmote, connection.getHost(), channel.getName(), user.getUsername(), user.getNick(), message);
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onChannelMessage(IRCConnection connection, IRCChannel channel, IRCUser user, String message) {
		if(onIRCBotCommand(connection, channel, user, message)) {
			return;
		}
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(!channelConfig.isMuted()) {
			if(GlobalConfig.enableLinkFilter) {
				message = Utils.filterLinks(message);
			}
			message = Utils.filterCodes(message);
			String mcMessage = Utils.formatMessage(GlobalConfig.getDisplayFormatConfig().mcChannelMessage, connection.getHost(), channel.getName(), user.getUsername(), Utils.getColoredName(user.getNick(), ConfigHelper.getIRCColor(channelConfig)), message);
			Utils.addMessageToChat(mcMessage);
		}
	}

	private boolean onIRCBotCommand(IRCConnection connection, IRCChannel channel, IRCUser user, String message) {
		if(Utils.getServerConfig(connection).isClientSide()) {
			return false;
		}
		if(message.equals("!who")) {
			Utils.sendUserList(connection, channel);
			return true;
		}
		if(message.equals("!help")) {
			connection.sendChannelNotice(channel, Utils.getLocalizedMessage("irc.bot.cmdlist"));
			return true;
		}
		return false;
	}
	
	private void onIRCBotPrivateCommand(IRCConnection connection, IRCUser user, String message) {
		String lmessage = message.toLowerCase();
		if(lmessage.equals("help")) {
			connection.sendPrivateNotice(user, "***** EiraIRC Help *****");
			connection.sendPrivateNotice(user, "EiraIRC connects a Minecraft client or a whole server");
			connection.sendPrivateNotice(user, "to one or multiple IRC channels and servers.");
			connection.sendPrivateNotice(user, "Visit http://blay09.net/?page_id=63 for more information on this bot.");
			connection.sendPrivateNotice(user, " ");
			connection.sendPrivateNotice(user, "The following commands are available:");
			connection.sendPrivateNotice(user, "HELP            Prints this command list");
			connection.sendPrivateNotice(user, "WHO            Prints out a list of all players online");
			connection.sendPrivateNotice(user, "ALIAS            Look up the username of an online player");
			connection.sendPrivateNotice(user, "MSG            Send a private message to an online player");
			connection.sendPrivateNotice(user, "***** End of Help *****");
		} else if(lmessage.equals("who")) {
			Utils.sendUserList(connection, user);
		} else if(lmessage.equals("alias")) {
			if(!GlobalConfig.enableAliases) {
				connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.alias.disabled"));
				return;
			}
			int i = message.indexOf(" ", 7);
			String alias = message.substring(7);
			List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			for(EntityPlayer entity : playerEntityList) {
				if(Utils.getAliasForPlayer(entity).equals(alias)) {
					connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.alias.lookup", alias, entity.username));
					return;
				}
			}
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
		} else if(lmessage.equals("msg")) {
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			if(!GlobalConfig.allowPrivateMessages || !serverConfig.allowsPrivateMessages()) {
				connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.msg.disabled"));
				return;
			}
			int i = message.indexOf(" ", 5);
			String playerName = message.substring(4, i);
			EntityPlayer entityPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
			if(entityPlayer == null) {
				List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
				for(EntityPlayer entity : playerEntityList) {
					if(Utils.getAliasForPlayer(entity).equals(playerName)) {
						entityPlayer = entity;
					}
				}
				if(entityPlayer == null) {
					connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
					return;
				}
			}
			String targetMessage = message.substring(i + 1);
			EiraIRC.instance.getEventHandler().onIRCPrivateMessageToPlayer(connection, user, entityPlayer, targetMessage);
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.bot.msgSent", playerName, targetMessage));
			return;
		} else {
			connection.sendPrivateNotice(user, Utils.getLocalizedMessage("irc.bot.unknownCommand"));
		}
	}
	
	private void onIRCPrivateMessageToPlayer(IRCConnection connection, IRCUser user, EntityPlayer entityPlayer, String message) {
		if(GlobalConfig.enableLinkFilter) {
			message = Utils.filterLinks(message);
		}
		message = Utils.filterCodes(message);
		ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
		String mcMessage = Utils.formatMessage(GlobalConfig.getDisplayFormatConfig().mcPrivateMessage, connection, user.getUsername(), Utils.getColoredName(user.getNick(), ConfigHelper.getIRCColor(serverConfig)), message);
		entityPlayer.sendChatToPlayer(mcMessage);
	}

	@Override
	public void onTopicChange(IRCChannel channel, String topic) {
		String mcMessage = Utils.getLocalizedMessage("irc.display.irc.topic", channel.getName(), channel.getTopic());
		Utils.addMessageToChat(mcMessage);
	}

	@Override
	public void onIRCError(IRCConnection connection, int errorCode, String line, String[] cmd) {
		switch(errorCode) {
		case IRCReplyCodes.ERR_NICKNAMEINUSE:
			String failNick = cmd[3];
			String tryNick = failNick + "_";
			Utils.addMessageToChat(Utils.getLocalizedMessage("irc.bot.nickInUse", failNick, tryNick));
			connection.nick(tryNick);
			break;
		case IRCReplyCodes.ERR_ERRONEUSNICKNAME:
			Utils.addMessageToChat(Utils.getLocalizedMessage("irc.bot.nickInvalid", cmd[3]));
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			if(serverConfig.getNick() != null) {
				serverConfig.setNick(connection.getNick());
			} else {
				GlobalConfig.nick = connection.getNick();
			}
			break;
		default:
			System.out.println("Unhandled error code: " + errorCode + " (" + line + ")");
			break;
		}
	}

}
