// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.api.event.RelayChat;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.config.*;
import net.blay09.mods.eirairc.config.settings.*;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.net.PacketHandler;
import net.blay09.mods.eirairc.net.message.MessageRedirect;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.IRCFormatting;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBroadcast;
import net.minecraft.command.server.CommandEmote;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.regex.Pattern;

public class MCEventHandler {

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getAddress());
			if(connection != null) {
				for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
					IRCChannel channel = connection.getChannel(channelConfig.getName());
					if (channel != null) {
						GeneralSettings generalSettings = ConfigHelper.getGeneralSettings(channel);
						BotSettings botSettings = ConfigHelper.getBotSettings(channel);
						String ircMessage = MessageFormat.formatMessage(botSettings.getMessageFormat().ircPlayerJoin, channel, event.player, "", MessageFormat.Target.IRC, MessageFormat.Mode.Message);
						if (!generalSettings.isReadOnly() && botSettings.getBoolean(BotBooleanComponent.RelayMinecraftJoinLeave)) {
							channel.message(ircMessage);
						}
						if (channel.getTopic() != null) {
							Utils.sendLocalizedMessage(event.player, "irc.display.irc.topic", channel.getName(), channel.getTopic());
						}
						if (generalSettings.getBoolean(GeneralBooleanComponent.AutoJoin)) {
							Utils.sendUserList(event.player, connection, channel);
						}
					}
				}
			}
		}

		// Send redirect configurations to client
		if(MinecraftServer.getServer() != null && MinecraftServer.getServer().isDedicatedServer()) {
			for (ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				if (serverConfig.isRedirect()) {
					PacketHandler.instance.sendTo(new MessageRedirect(serverConfig.toJsonObject().toString()), (EntityPlayerMP) event.player);
				}
			}
		}

	}

	@SubscribeEvent
	public void onServerCommand(CommandEvent event) {
		if(event.command instanceof CommandEmote) {
			if(event.sender instanceof EntityPlayer) {
				String emote = Utils.joinStrings(event.parameters, " ", 0).trim();
				if(emote.length() == 0) {
					return;
				}
				String mcAlias = Utils.getNickGame((EntityPlayer) event.sender);
				IChatComponent chatComponent = MessageFormat.createChatComponentForMessage("* " + mcAlias + " " + emote);
				EnumChatFormatting emoteColor = SharedGlobalConfig.theme.getColor(ThemeColorComponent.emoteTextColor);
				if(emoteColor != null) {
					chatComponent.getChatStyle().setColor(emoteColor);
				}
				Utils.addMessageToChat(chatComponent);
				if(!MinecraftServer.getServer().isSinglePlayer()) {
					relayChatServer(event.sender, emote, true, false, null);
				}
				event.setCanceled(true);
			}
		} else if(event.command instanceof CommandBroadcast) {
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getAddress());
				if(connection != null) {
					for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
						IRCChannel channel = connection.getChannel(channelConfig.getName());
						if (channel != null) {
							GeneralSettings generalSettings = ConfigHelper.getGeneralSettings(channel);
							BotSettings botSettings = ConfigHelper.getBotSettings(channel);
							String ircMessage = MessageFormat.formatMessage(botSettings.getMessageFormat().ircBroadcastMessage, channel, event.sender, Utils.joinStrings(event.parameters, " ", 0), MessageFormat.Target.IRC, MessageFormat.Mode.Message);
							if (!generalSettings.isReadOnly() && botSettings.getBoolean(BotBooleanComponent.RelayBroadcasts)) {
								channel.message(ircMessage);
							}
						}
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
		if(EiraIRC.instance.getConnectionManager().getConnectionCount() > 0 && IRCCommandHandler.onChatCommand(sender, text, false)) {
			return true;
		}
		if(ClientGlobalConfig.clientBridge) {
			relayChatClient(text, false, false, null, true);
			return false;
		}
		IRCContext chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return false;
		}
		IChatComponent chatComponent;
		if(chatTarget instanceof IRCChannel) {
			BotSettings botSettings = ConfigHelper.getBotSettings(chatTarget);
			chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendChannelMessage, chatTarget, sender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Message);
		} else if(chatTarget instanceof IRCUser) {
			BotSettings botSettings = ConfigHelper.getBotSettings(chatTarget);
			chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendPrivateMessage, chatTarget, sender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Message);
		} else {
			return false;
		}
		relayChatClient(text, false, false, chatTarget, false);
		Utils.addMessageToChat(chatComponent);
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onClientEmote(String text) {
		EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
		if(ClientGlobalConfig.clientBridge) {
			relayChatClient(text, true, false, null, true);
			return false;
		}
		IRCContext chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return false;
		}
		EnumChatFormatting emoteColor;
		IChatComponent chatComponent;
		if(chatTarget instanceof IRCChannel) {
			emoteColor = ConfigHelper.getTheme(chatTarget).getColor(ThemeColorComponent.emoteTextColor);
			BotSettings botSettings = ConfigHelper.getBotSettings(chatTarget);
			chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendChannelEmote, chatTarget, sender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Emote);
		} else if(chatTarget instanceof IRCUser) {
			emoteColor = ConfigHelper.getTheme(chatTarget).getColor(ThemeColorComponent.emoteTextColor);
			BotSettings botSettings = ConfigHelper.getBotSettings(chatTarget);
			chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendPrivateEmote, chatTarget, sender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Emote);
		} else {
			return false;
		}
		relayChatClient(text, true, false, chatTarget, false);
		if(emoteColor != null) {
			chatComponent.getChatStyle().setColor(emoteColor);
		}
		Utils.addMessageToChat(chatComponent);
		return true;
	}
	
	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		IChatComponent senderComponent = event.player.func_145748_c_();
		EnumChatFormatting nameColor = Utils.getColorFormattingForPlayer(event.player);
		if(nameColor != null) {
			senderComponent.getChatStyle().setColor(nameColor);
		}
		event.component = new ChatComponentTranslation("chat.type.text", senderComponent, MessageFormat.createChatComponentForMessage(event.message));
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			if(IRCCommandHandler.onChatCommand(event.player, event.message, true)) {
				event.setCanceled(true);
				return;
			}
			relayChatServer(event.player, event.message, false, false, null);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void relayChatClient(RelayChat event) {
		relayChatClient(event.message, event.isEmote, event.isNotice, event.target, ClientGlobalConfig.clientBridge);
	}

	@SideOnly(Side.CLIENT)
	private void relayChatClient(String message, boolean isEmote, boolean isNotice, IRCContext target, boolean clientBridge) {
		if(target != null) {
			if(!ConfigHelper.getGeneralSettings(target).isReadOnly()) {
				String ircMessage = message;
				if(isEmote) {
					ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage + IRCConnectionImpl.EMOTE_END;
				}
				if(isNotice) {
					target.notice(ircMessage);
				} else {
					target.message(ircMessage);
				}
			}
		} else {
			if(clientBridge) {
				String ircMessage = message;
				if(isEmote) {
					ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage + IRCConnectionImpl.EMOTE_END;
				}
				if(!ClientGlobalConfig.clientBridgeMessageToken.isEmpty()) {
					ircMessage = ircMessage + " " + ClientGlobalConfig.clientBridgeMessageToken;
				}
				for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
					IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getAddress());
					if(connection != null) {
						for (ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
							IRCChannel channel = connection.getChannel(channelConfig.getName());
							if (channel != null) {
								if (!ConfigHelper.getGeneralSettings(channel).isReadOnly()) {
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
			} else {
				IRCContext chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
				if(chatTarget != null && !ConfigHelper.getGeneralSettings(chatTarget).isReadOnly()) {
					String ircMessage = message;
					if(isEmote) {
						ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage + IRCConnectionImpl.EMOTE_END;
					}
					if(isNotice) {
						chatTarget.notice(ircMessage);
					} else {
						chatTarget.message(ircMessage);
					}
				}

			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public void relayChatServer(RelayChat event) {
		relayChatServer(event.sender, event.message, event.isEmote, event.isNotice, event.target);
	}

	private void relayChatServer(ICommandSender sender, String message, boolean isEmote, boolean isNotice, IRCContext target) {
		if(target != null) {
			if(!ConfigHelper.getGeneralSettings(target).isReadOnly()) {
				String format = MessageFormat.getMessageFormat(target, isEmote);
				String ircMessage = MessageFormat.formatMessage(format, target, sender, message, MessageFormat.Target.IRC, (isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message));
				if(isEmote) {
					ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage + IRCConnectionImpl.EMOTE_END;
				}
				if(isNotice) {
					target.notice(ircMessage);
				} else {
					target.message(ircMessage);
				}
			}
		} else {
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getAddress());
				if(connection != null) {
					for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
						IRCChannel channel = connection.getChannel(channelConfig.getName());
						if(channel != null) {
							String format = MessageFormat.getMessageFormat(channel, isEmote);
							String ircMessage = MessageFormat.formatMessage(format, channel, sender, message, MessageFormat.Target.IRC, (isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message));
							if (isEmote) {
								ircMessage = IRCConnectionImpl.EMOTE_START + ircMessage + IRCConnectionImpl.EMOTE_END;
							}
							if (!ConfigHelper.getGeneralSettings(channel).isReadOnly()) {
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
	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.entityLiving instanceof EntityPlayer) {
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getAddress());
				if(connection != null) {
					for (ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
						IRCChannel channel = connection.getChannel(channelConfig.getName());
						if (channel != null) {
							GeneralSettings generalSettings = ConfigHelper.getGeneralSettings(channel);
							BotSettings botSettings = ConfigHelper.getBotSettings(channel);
							String name = Utils.getNickIRC((EntityPlayer) event.entityLiving, channel);
							String ircMessage = event.entityLiving.func_110142_aN().func_151521_b().getUnformattedText();
							ircMessage = ircMessage.replaceAll(Pattern.quote(event.entityLiving.getCommandSenderName()), name);
							ircMessage = IRCFormatting.toIRC(ircMessage, !botSettings.getBoolean(BotBooleanComponent.ConvertColors));
							if (!generalSettings.isReadOnly() && botSettings.getBoolean(BotBooleanComponent.RelayDeathMessages)) {
								channel.message(ircMessage);
							}
						}
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
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getAddress());
			if(connection != null) {
				for (ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
					IRCChannel channel = connection.getChannel(channelConfig.getName());
					if (channel != null) {
						GeneralSettings generalSettings = ConfigHelper.getGeneralSettings(channel);
						BotSettings botSettings = ConfigHelper.getBotSettings(channel);
						String ircMessage = MessageFormat.formatMessage(botSettings.getMessageFormat().ircPlayerLeave, channel, event.player, "", MessageFormat.Target.IRC, MessageFormat.Mode.Message);
						if (!generalSettings.isReadOnly() && botSettings.getBoolean(BotBooleanComponent.RelayMinecraftJoinLeave)) {
							channel.message(ircMessage);
						}
					}
				}
			}
		}
	}

	public void onPlayerNickChange(EntityPlayer player, String oldNick) {
		String format = SharedGlobalConfig.botSettings.getMessageFormat().ircPlayerNickChange;
		format = format.replace("{OLDNICK}", oldNick);
		IChatComponent chatComponent = MessageFormat.formatChatComponent(format, null, player, "", MessageFormat.Target.Minecraft, MessageFormat.Mode.Emote);
		Utils.addMessageToChat(chatComponent);
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getAddress());
			if(connection != null) {
				for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
					IRCChannel channel = connection.getChannel(channelConfig.getName());
					if(channel != null) {
						if(!ConfigHelper.getGeneralSettings(channel).isReadOnly()) {
							format = ConfigHelper.getBotSettings(channel).getMessageFormat().ircPlayerNickChange;
							format = format.replace("{OLDNICK}", oldNick);
							channel.message(MessageFormat.formatMessage(format, channel, player, "", MessageFormat.Target.IRC, MessageFormat.Mode.Emote));
						}
					}
				}
			}
		}
	}

}
