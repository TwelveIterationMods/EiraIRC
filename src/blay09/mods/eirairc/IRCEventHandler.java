// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.Globals;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IIRCEventHandler;
import blay09.mods.eirairc.irc.IRCChannel;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.irc.IRCUser;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IRCEventHandler implements IIRCEventHandler, IPlayerTracker, IConnectionHandler {

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		if(!GlobalConfig.showMinecraftJoinLeave) {
			return;
		}
		String name = Utils.getAliasForPlayer(player);
		String ircMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.joinMsgMC", name);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			for(IRCChannel channel : connection.getChannels()) {
				ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
				if(!channelConfig.isObserver() && channelConfig.relayMinecraftJoinLeave) {
					connection.sendChannelMessage(channel, ircMessage);
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
				String mcMessage = "* " + alias + " " + emote;
				Utils.addMessageToChat(mcMessage);
				if(!MinecraftServer.getServer().isSinglePlayer()) {
					String ircMessage = Utils.formatMessage(GlobalConfig.ircChannelEmtFormat, event.sender.getCommandSenderName(), alias, emote);
					for(IRCConnection connection : EiraIRC.instance.getConnections()) {
						ServerConfig serverConfig = Utils.getServerConfig(connection);
						for(IRCChannel channel : connection.getChannels()) {
							ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
							if(!channelConfig.isObserver()) {
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
				if(channel[1].startsWith("#")) {
					connection.sendChannelMessage(connection.getChannel(channel[1]), text);
				} else {
					connection.sendPrivateMessage(connection.getUser(channel[1]), text);
				}
				String mcMessage = "[" + channel[1] + "] <" + Utils.getColorAliasForPlayer(Minecraft.getMinecraft().thePlayer) + "> " + text;
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
					if(!channelConfig.isObserver()) {
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
					if(!channelConfig.isObserver()) {
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
			String ircMessage = Utils.formatMessage(GlobalConfig.mcChannelMsgFormat, event.player.username, nick, event.message);
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				ServerConfig serverConfig = Utils.getServerConfig(connection);
				for(IRCChannel channel : connection.getChannels()) {
					ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
					if(!channelConfig.isObserver()) {
						connection.sendChannelMessage(channel, ircMessage);
					}
				}
			}
		}
	}
	
	@ForgeSubscribe
	public void onPlayerDeath(LivingDeathEvent event) {
		if(!GlobalConfig.showDeathMessages) {
			return;
		}
		if(event.entityLiving instanceof EntityPlayer) {
			String name = Utils.getAliasForPlayer((EntityPlayer) event.entityLiving);
			String ircMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.deathMsgMC", name, event.source.damageType);
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				ServerConfig serverConfig = Utils.getServerConfig(connection);
				for(IRCChannel channel : connection.getChannels()) {
					ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
					if(!channelConfig.isObserver() && channelConfig.relayDeathMessages) {
						connection.sendChannelMessage(channel, ircMessage);
					}
				}
			}
		}
	}
	
	@Override
	public void onPlayerLogout(EntityPlayer player) {
		if(!GlobalConfig.showMinecraftJoinLeave) {
			return;
		}
		String name = Utils.getAliasForPlayer(player);
		String ircMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.partMsgMC", name);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			for(IRCChannel channel : connection.getChannels()) {
				ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
				if(!channelConfig.isObserver() && channelConfig.relayMinecraftJoinLeave) {
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
		String message = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.nickChangeMC", oldNick, newNick);
		Utils.addMessageToChat(message);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			for(IRCChannel channel : connection.getChannels()) {
				ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
				if(!channelConfig.isObserver()) {
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
		String mcMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.connected", connection.getHost());
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
		String mcMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.disconnected", connection.getHost());
		Utils.addMessageToChat(mcMessage);
	}

	@Override
	public void onIRCError(IRCConnection connection, int errorCode) {
		
	}

	@Override
	public void onNickChange(IRCConnection connection, IRCUser user, String nick) {
		if(!GlobalConfig.showNickChanges) {
			return;
		}
		String mcMessage = StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.nickChangeIRC", connection.getHost(), user.getNick(), nick);
		Utils.addMessageToChat(mcMessage);
	}

	@Override
	public void onUserJoin(IRCConnection connection, IRCUser user, IRCChannel channel) {
		if(!GlobalConfig.showIRCJoinLeave)  {
			return;
		}
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(!channelConfig.isMuted() && channelConfig.relayIRCJoinLeave) {
			String mcMessage = EnumChatFormatting.YELLOW + StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.joinMsgIRC", channel.getName(), user.getNick());
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onUserPart(IRCConnection connection, IRCUser user, IRCChannel channel, String quitMessage) {
		if(!GlobalConfig.showIRCJoinLeave) {
			return;
		}
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(!channelConfig.isMuted() && channelConfig.relayIRCJoinLeave) {
			String mcMessage = EnumChatFormatting.YELLOW + StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.partMsgIRC", channel.getName(), user.getNick());
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onUserQuit(IRCConnection connection, IRCUser user, String quitMessage) {
		if(!GlobalConfig.showIRCJoinLeave) {
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
			String mcMessage = EnumChatFormatting.YELLOW + StringTranslate.getInstance().translateKeyFormat(Globals.MOD_ID + ":irc.quitMsgIRC", connection.getHost(), user.getNick());
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
			String mcMessage = Utils.formatMessage(GlobalConfig.mcPrivateEmtFormat, connection, user.getUsername(), user.getNick(), message);
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onPrivateMessage(IRCConnection connection, IRCUser user, String message) {
		if(!GlobalConfig.allowPrivateMessages) {
			connection.sendPrivateMessage(user, "Private Messages are disabled.");
			return;
		}
		ServerConfig serverConfig = Utils.getServerConfig(connection);
		if(serverConfig.allowsPrivateMessages()) {
			if(GlobalConfig.enableLinkFilter) {
				message = Utils.filterLinks(message);
			}
			String mcMessage = Utils.formatMessage(GlobalConfig.mcPrivateMsgFormat, connection, user.getUsername(), Utils.getColoredName(user.getNick(), GlobalConfig.ircColor), message);
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onChannelEmote(IRCConnection connection, IRCChannel channel, IRCUser user, String message) {
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(!channelConfig.isMuted()) {
			if(GlobalConfig.enableLinkFilter) {
				message = Utils.filterLinks(message);
			}
			String mcMessage = Utils.formatMessage(GlobalConfig.mcChannelEmtFormat, connection.getHost(), channel.getName(), user.getUsername(), user.getNick(), message);
			Utils.addMessageToChat(mcMessage);
		}
	}

	@Override
	public void onChannelMessage(IRCConnection connection, IRCChannel channel, IRCUser user, String message) {
		ChannelConfig channelConfig = Utils.getServerConfig(connection).getChannelConfig(channel);
		if(!channelConfig.isMuted()) {
			if(GlobalConfig.enableLinkFilter) {
				message = Utils.filterLinks(message);
			}
			String mcMessage = Utils.formatMessage(GlobalConfig.mcChannelMsgFormat, connection.getHost(), channel.getName(), user.getUsername(), Utils.getColoredName(user.getNick(), GlobalConfig.ircColor), message);
			Utils.addMessageToChat(mcMessage);
		}
	}

	private void onIRCPrivateMessageToPlayer(IRCConnection connection, IRCUser user, String nick, EntityPlayer entityPlayer, String message) {
		if(GlobalConfig.enableLinkFilter) {
			message = Utils.filterLinks(message);
		}
		String mcMessage = Utils.formatMessage(GlobalConfig.mcPrivateMsgFormat, connection, user.getUsername(), Utils.getColoredName(nick, GlobalConfig.ircColor), message);
		entityPlayer.sendChatToPlayer(mcMessage);
	}
}
