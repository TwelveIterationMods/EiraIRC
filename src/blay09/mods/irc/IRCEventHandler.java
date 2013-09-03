// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc;

import blay09.mods.irc.config.GlobalConfig;
import blay09.mods.irc.config.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class IRCEventHandler implements IPlayerTracker, IConnectionHandler {
	
	public void onIRCConnect(IRCConnection connection) {
		Utils.addLocalizedMessageToChat("irc.connected", connection.getHost());
	}
	
	public void onIRCDisconnect(IRCConnection connection) {
		Utils.addLocalizedMessageToChat("irc.disconnected", connection.getHost());
	}
	
	public void onIRCJoin(IRCConnection connection, String channel, String user, String nick) {
		if(!GlobalConfig.showIRCJoinLeave)  {
			return;
		}
		if(connection.getConfig().hasChannelFlags(channel, "rj")) {
			Utils.addLocalizedMessageToChat("irc.joinMsgIRC", channel, nick);
		}
	}
	
	public void onIRCNickChange(IRCConnection connection, String oldNick, String newNick) {
		Utils.addLocalizedMessageToChat("irc.nickChangeIRC", connection.getHost(), oldNick, newNick);
	}
	
	public void onIRCPart(IRCConnection connection, String channel, String user, String nick) {
		if(!GlobalConfig.showIRCJoinLeave) {
			return;
		}
		if(connection.getConfig().hasChannelFlags(channel, "rj")) {
			Utils.addLocalizedMessageToChat("irc.partMsgIRC", channel, nick);
		}
	}
	
	public void onIRCPrivateMessage(IRCConnection connection, String user, String nick, String message) {
		if(!GlobalConfig.allowPrivateMessages) {
			connection.sendPrivateMessage(nick, "Private Messages are disabled.");
			return;
		}
		if(connection.getConfig().allowPrivateMessages) {
			String mcMessage = "[Private] <" + Utils.getColoredName(nick, GlobalConfig.ircColor) + ">: " + message;
			Utils.addUnlocalizedMessageToChat(mcMessage);
		}
	}
	
	public void onIRCPrivateMessageToPlayer(IRCConnection ircConnection, String user, String nick, EntityPlayer entityPlayer, String message) {
		String mcMessage = "[Private] <" + Utils.getColoredName(nick, GlobalConfig.ircColor) + ">: " + message;
		entityPlayer.sendChatToPlayer(Utils.getUnlocalizedChatMessage(mcMessage));
	}

	public void onIRCPrivateEmote(IRCConnection connection, String user, String nick, String message) {
		if(!GlobalConfig.allowPrivateMessages) {
			return;
		}
		if(connection.getConfig().allowPrivateMessages) {
			String mcMessage = "[Private] * " + nick + " " + message;
			Utils.addUnlocalizedMessageToChat(mcMessage);
		}
	}
	
	public void onIRCMessage(IRCConnection connection, String channel, String user, String nick, String message) {
		if(connection.getConfig().hasChannelFlags(channel, "re")) {
			String mcMessage = "[" + channel + "] <" + Utils.getColoredName(nick, GlobalConfig.ircColor) + ">: " + message;
			Utils.addUnlocalizedMessageToChat(mcMessage);
		}
	}
	
	public void onIRCEmote(IRCConnection connection, String channel, String user, String nick, String message) {
		if(connection.getConfig().hasChannelFlags(channel, "re")) {
			String mcMessage = "[" + channel + "] * " + nick + " " + message;
			Utils.addUnlocalizedMessageToChat(mcMessage);
		}
	}
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		if(!GlobalConfig.showMinecraftJoinLeave) {
			return;
		}
		String name = Utils.getAliasForPlayer(player);
		String ircMessage = Utils.getLocalizedMessage(":irc.joinMsgMC", name);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			connection.broadcastMessage(ircMessage, "wJ");
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
				String message = "* " + Utils.getAliasForPlayer((EntityPlayer) event.sender) + " " + emote;
				Utils.addUnlocalizedMessageToChat(message);
				if(!MinecraftServer.getServer().isSinglePlayer()) {
					for(IRCConnection connection : EiraIRC.instance.getConnections()) {
						connection.broadcastMessage(message, "wE");
					}
				}
				event.setCanceled(true);
			}
		}
	}

	public boolean onClientChat(String text) {
		EnumChatTarget chatTarget = EiraIRC.instance.getChatTarget();
		if(chatTarget == EnumChatTarget.ChannelOnly) {
			String target = EiraIRC.instance.getTargetChannel();
			String[] channel = target.split(":");
			IRCConnection connection = EiraIRC.instance.getConnection(channel[0]);
			if(connection != null) {
				if(channel[1].startsWith("#")) {
					connection.sendChannelMessage(channel[1], text);
				} else {
					connection.sendPrivateMessage(channel[1], text);
				}
				String mcMessage = "[" + channel[1] + "] <" + Minecraft.getMinecraft().thePlayer.username + "> " + text;
				Utils.addUnlocalizedMessageToChat(mcMessage);
			}
			return false;
		}
		boolean doMC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.MinecraftOnly;
		boolean doIRC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.IRCOnly;
		if(!doMC) {
			Utils.addUnlocalizedMessageToChat("[IRC] <" + Minecraft.getMinecraft().thePlayer.username + "> " + text);
		}
		if(doIRC) {
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				connection.broadcastMessage(text, "w");
			}			
		}
		return doMC;
	}
	
	public boolean onClientEmote(String text) {
		EnumChatTarget chatTarget = EiraIRC.instance.getChatTarget();
		if(chatTarget == EnumChatTarget.ChannelOnly) {
			String target = EiraIRC.instance.getTargetChannel();
			String[] channel = target.split(":");
			IRCConnection connection = EiraIRC.instance.getConnection(channel[0]);
			if(connection != null) {
				connection.sendChannelMessage(channel[1], "ACTION " + text + "");
				Utils.addUnlocalizedMessageToChat("[" + channel[1] + "] * " + Minecraft.getMinecraft().thePlayer.username + " " + text);
			}
			return false;
		}
		boolean doMC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.MinecraftOnly;
		boolean doIRC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.IRCOnly;
		if(!doMC) {
			Utils.addUnlocalizedMessageToChat("[IRC] * " + Minecraft.getMinecraft().thePlayer.username + " " + text);
		}
		if(doIRC) {
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				connection.broadcastMessage("ACTION " + text + "", "wE");
			}			
		}
		return doMC;
	}
	
	@ForgeSubscribe
	public void onServerChat(ServerChatEvent event) {
		String name = Utils.getColorAliasForPlayer(event.player);
		event.component = Utils.getUnlocalizedChatMessage("<" + name + "> " + event.message);
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			String ircMessage = "<" + Utils.getAliasForPlayer(event.player) + "> " + event.message;
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				connection.broadcastMessage(ircMessage, "w");
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
			String ircMessage = Utils.getLocalizedMessage("irc.deathMsgMC", name, event.source.damageType);
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				connection.broadcastMessage(ircMessage, "wD");
			}
		}
	}
	
	@Override
	public void onPlayerLogout(EntityPlayer player) {
		if(!GlobalConfig.showMinecraftJoinLeave) {
			return;
		}
		String name = Utils.getAliasForPlayer(player);
		String ircMessage = Utils.getLocalizedMessage("irc.partMsgMC", name);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			connection.broadcastMessage(ircMessage, "wJ");
		}
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
	}

	public void onPlayerNickChange(String oldNick, String newNick) {
		String message = Utils.getLocalizedMessage("irc.nickChangeMC", oldNick, newNick);
		Utils.addLocalizedMessageToChat("irc.nickChangeMC", oldNick, newNick);
		for(IRCConnection connection : EiraIRC.instance.getConnections()) {
			connection.broadcastMessage(message, "w");
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
		if(MinecraftServer.getServer() == null || MinecraftServer.getServer().isSinglePlayer()) {
			EiraIRC.instance.stopIRC();
		}
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
		EiraIRC.instance.startIRC(true);
	}

}
