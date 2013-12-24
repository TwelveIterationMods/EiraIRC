package blay09.mods.eirairc;

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
import blay09.mods.eirairc.command.IRCCommandHandler;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.ConfigHelper;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IRCChannel;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.util.EnumChatTarget;
import blay09.mods.eirairc.util.Utils;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MCEventHandler implements IPlayerTracker, IConnectionHandler {

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

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		
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
				String mcMessage = (GlobalConfig.emoteColor != null ? "§" + Utils.getColorCode(GlobalConfig.emoteColor) : "") + "* " + alias + " " + emote;
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
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean onClientChat(String text) {
		EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
		if(EiraIRC.instance.getConnectionCount() > 0 && IRCCommandHandler.onChatCommand(sender, text, false)) {
			return false;
		}
		EnumChatTarget chatTarget = EiraIRC.instance.getChatTarget();
		if(chatTarget == EnumChatTarget.ChannelOnly) {
			String target = EiraIRC.instance.getTargetChannel();
			String[] channel = target.split(":");
			IRCConnection connection = EiraIRC.instance.getConnection(channel[0]);
			if(connection != null) {
				String mcMessage = null;
				if(channel[1].startsWith("#")) {
					connection.sendChannelMessage(connection.getChannel(channel[1]), text);
					mcMessage = "[" + channel[1] + "] <" + Utils.getColorAliasForPlayer(sender) + "> " + text;
				} else {
					connection.sendPrivateMessage(connection.getUser(channel[1]), text);
					mcMessage = "[-> " + channel[1] + "] <" + Utils.getColorAliasForPlayer(sender) + "> " + text;
				}
				Utils.addMessageToChat(mcMessage);
			}
			return false;
		}
		boolean doMC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.MinecraftOnly;
		boolean doIRC = chatTarget == EnumChatTarget.All || chatTarget == EnumChatTarget.IRCOnly;
		if(!doMC) {
			Utils.addMessageToChat("[IRC] <" + Utils.getColorAliasForPlayer(sender) + "> " + text);
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
		String ircNick = Utils.getAliasForPlayer(event.player);
		String mcNick = Utils.getColorAliasForPlayer(event.player);
		event.component = Utils.getUnlocalizedChatMessage("<" + mcNick + "> " + event.message);
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			String text = event.message;
			if(IRCCommandHandler.onChatCommand(event.player, text, true)) {
				event.setCanceled(true);
				return;
			}
			String ircMessage = Utils.formatMessage(ConfigHelper.getDisplayFormatConfig().ircChannelMessage, event.player.username, ircNick, event.message);
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
			String ircMessage = event.entityLiving.func_110142_aN().func_94546_b().toString();
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
}
