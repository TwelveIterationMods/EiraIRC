// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.event.ClientChatEvent;
import net.blay09.mods.eirairc.api.event.RelayChat;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
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
import net.minecraftforge.event.entity.player.AchievementEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class MCEventHandler {

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getIdentifier());
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
							IChatComponent chatComponent = MessageFormat.formatChatComponent(ConfigHelper.getBotSettings(channel).getMessageFormat().mcTopic, connection, channel, null, channel.getTopic(), MessageFormat.Target.Minecraft, MessageFormat.Mode.Message);
							event.player.addChatMessage(chatComponent);
						}
						if (generalSettings.getBoolean(GeneralBooleanComponent.AutoWho)) {
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
				String emote = StringUtils.join(event.parameters, " ").trim();
				if(emote.length() == 0) {
					return;
				}
				IChatComponent chatComponent = new ChatComponentTranslation("* %s %s", event.sender.func_145748_c_(), MessageFormat.createChatComponentForMessage(emote)); // getFormattedCommandSenderName
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
				IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getIdentifier());
				if(connection != null) {
					for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
						IRCChannel channel = connection.getChannel(channelConfig.getName());
						if (channel != null) {
							GeneralSettings generalSettings = ConfigHelper.getGeneralSettings(channel);
							BotSettings botSettings = ConfigHelper.getBotSettings(channel);
							String ircMessage = MessageFormat.formatMessage(botSettings.getMessageFormat().ircBroadcastMessage, channel, event.sender, StringUtils.join(event.parameters, " "), MessageFormat.Target.IRC, MessageFormat.Mode.Message);
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
	@SubscribeEvent
	public void onClientChat(ClientChatEvent event) {
		if(event.message.startsWith("/")) {
			if(event.message.startsWith("/me ") && event.message.length() > 4) {
				onClientEmote(event);
			}
			return;
		}
		EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
		if(EiraIRC.instance.getConnectionManager().getConnectionCount() > 0 && IRCCommandHandler.onChatCommand(sender, event.message, false)) {
			event.setCanceled(true);
			return;
		}
		if(ClientGlobalConfig.clientBridge.get()) {
			relayChatClient(event.message, false, false, null);
			return;
		}
		IRCContext chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return;
		}
		IRCUser ircSender = chatTarget.getConnection().getBotUser();
		IChatComponent chatComponent;
		if(chatTarget instanceof IRCChannel) {
			BotSettings botSettings = ConfigHelper.getBotSettings(chatTarget);
			chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendChannelMessage, chatTarget.getConnection(), chatTarget, ircSender, event.message, MessageFormat.Target.Minecraft, MessageFormat.Mode.Message);
		} else if(chatTarget instanceof IRCUser) {
			BotSettings botSettings = ConfigHelper.getBotSettings(chatTarget);
			chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendPrivateMessage, chatTarget.getConnection(), chatTarget, ircSender, event.message, MessageFormat.Target.Minecraft, MessageFormat.Mode.Message);
		} else {
			return;
		}
		relayChatClient(event.message, false, false, chatTarget);
		EiraIRCAPI.getChatHandler().addChatMessage(chatComponent);
		event.setCanceled(true);
	}
	
	@SideOnly(Side.CLIENT)
	public void onClientEmote(ClientChatEvent event) {
		String text = event.message.substring(4);
		EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
		if(ClientGlobalConfig.clientBridge.get()) {
			relayChatClient(text, true, false, null);
			return;
		}
		IRCContext chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
		if(chatTarget == null) {
			return;
		}
		IRCUser ircSender = chatTarget.getConnection().getBotUser();
		EnumChatFormatting emoteColor;
		IChatComponent chatComponent;
		if(chatTarget instanceof IRCChannel) {
			emoteColor = ConfigHelper.getTheme(chatTarget).getColor(ThemeColorComponent.emoteTextColor);
			BotSettings botSettings = ConfigHelper.getBotSettings(chatTarget);
			chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendChannelEmote, chatTarget.getConnection(), chatTarget, ircSender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Emote);
		} else if(chatTarget instanceof IRCUser) {
			emoteColor = ConfigHelper.getTheme(chatTarget).getColor(ThemeColorComponent.emoteTextColor);
			BotSettings botSettings = ConfigHelper.getBotSettings(chatTarget);
			chatComponent = MessageFormat.formatChatComponent(botSettings.getMessageFormat().mcSendPrivateEmote, chatTarget.getConnection(), chatTarget, ircSender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Emote);
		} else {
			return;
		}
		relayChatClient(text, true, false, chatTarget);
		if(emoteColor != null) {
			chatComponent.getChatStyle().setColor(emoteColor);
		}
		EiraIRCAPI.getChatHandler().addChatMessage(chatComponent);
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		IChatComponent senderComponent = event.player.func_145748_c_(); // getFormattedCommandSenderName
		EnumChatFormatting nameColor = IRCFormatting.getColorFormattingForPlayer(event.player);
		if(nameColor != null && nameColor != EnumChatFormatting.WHITE) {
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
		relayChatClient(event.message, event.isEmote, event.isNotice, event.target);
	}

	@SideOnly(Side.CLIENT)
	public void relayChatClient(String message, boolean isEmote, boolean isNotice, IRCContext target) {
		if(target != null) {
			if(!ConfigHelper.getGeneralSettings(target).isReadOnly()) {
				if(isEmote) {
					if (isNotice) {
						target.ctcpNotice("ACTION " + message);
					} else {
						target.ctcpMessage("ACTION " + message);
					}
				} else {
					if (isNotice) {
						target.notice(message);
					} else {
						target.message(message);
					}
				}
			} else {
				EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:general.readOnly", target.getName()));
			}
		} else {
			if(ClientGlobalConfig.clientBridge.get()) {
				boolean isCTCP = false;
				String ircMessage = message;
				if(isEmote) {
					isCTCP = true;
					ircMessage = "ACTION " + ircMessage;
				}
				for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
					IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getIdentifier());
					if(connection != null) {
						for (ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
							IRCChannel channel = connection.getChannel(channelConfig.getName());
							if (channel != null) {
								if (!ConfigHelper.getGeneralSettings(channel).isReadOnly()) {
									if(isCTCP) {
										if (isNotice) {
											channel.ctcpNotice(ircMessage);
										} else {
											channel.ctcpMessage(ircMessage);
										}
									} else {
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
			} else {
				IRCContext chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
				if(chatTarget != null){
					if(!ConfigHelper.getGeneralSettings(chatTarget).isReadOnly()) {
						if (isEmote) {
							if (isNotice) {
								chatTarget.ctcpNotice("ACTION " + message);
							} else {
								chatTarget.ctcpMessage("ACTION " + message);
							}
						} else {
							if (isNotice) {
								chatTarget.notice(message);
							} else {
								chatTarget.message(message);
							}
						}
					} else {
						EiraIRCAPI.getChatHandler().addChatMessage(new ChatComponentTranslation("eirairc:general.readOnly", chatTarget.getName()));
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

	public void relayChatServer(ICommandSender sender, String message, boolean isEmote, boolean isNotice, IRCContext target) {
		if(target != null) {
			if(!ConfigHelper.getGeneralSettings(target).isReadOnly()) {
				String format = MessageFormat.getMessageFormat(target, isEmote);
				String ircMessage = MessageFormat.formatMessage(format, target, sender, message, MessageFormat.Target.IRC, (isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message));
				if(isEmote) {
					ircMessage = IRCConnectionImpl.CTCP_START + ircMessage + IRCConnectionImpl.CTCP_END;
				}
				if(isNotice) {
					target.notice(ircMessage);
				} else {
					target.message(ircMessage);
				}
			}
		} else {
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getIdentifier());
				if(connection != null) {
					for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
						IRCChannel channel = connection.getChannel(channelConfig.getName());
						if(channel != null) {
							String format = MessageFormat.getMessageFormat(channel, isEmote);
							String ircMessage = MessageFormat.formatMessage(format, channel, sender, message, MessageFormat.Target.IRC, (isEmote ? MessageFormat.Mode.Emote : MessageFormat.Mode.Message));
							if (!ConfigHelper.getGeneralSettings(channel).isReadOnly()) {
								if(isEmote) {
									if (isNotice) {
										channel.ctcpNotice("ACTION " + ircMessage);
									} else {
										channel.ctcpMessage("ACTION " + ircMessage);
									}
								} else {
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
	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.entityLiving instanceof EntityPlayer) {
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getIdentifier());
				if(connection != null) {
					for (ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
						IRCChannel channel = connection.getChannel(channelConfig.getName());
						if (channel != null) {
							GeneralSettings generalSettings = ConfigHelper.getGeneralSettings(channel);
							BotSettings botSettings = ConfigHelper.getBotSettings(channel);
							String name = Utils.getNickIRC((EntityPlayer) event.entityLiving, channel);
							String ircMessage = event.entityLiving.func_110142_aN().func_151521_b().getUnformattedText(); // getCombatTracker
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
	
	@SubscribeEvent
	public void onAchievement(AchievementEvent event) {
		if(((EntityPlayerMP) event.entityPlayer).func_147099_x().hasAchievementUnlocked(event.achievement)) { // getStatFile
			// This is necessary because the Achievement event fires even if an achievement is already unlocked.
			return;
		}
		if(!((EntityPlayerMP) event.entityPlayer).func_147099_x().canUnlockAchievement(event.achievement)) { // getStatFile
			// This is necessary because the Achievement event fires even if an achievement can not be unlocked yet.
			return;
		}
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getIdentifier());
			if(connection != null) {
				for (ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
					IRCChannel channel = connection.getChannel(channelConfig.getName());
					if (channel != null) {
						GeneralSettings generalSettings = ConfigHelper.getGeneralSettings(channel);
						BotSettings botSettings = ConfigHelper.getBotSettings(channel);
						String ircMessage = MessageFormat.formatMessage(botSettings.getMessageFormat().ircAchievement, channel, event.entityPlayer, event.achievement.func_150951_e().getUnformattedText(), MessageFormat.Target.IRC, MessageFormat.Mode.Emote); // getStatName
						if (!generalSettings.isReadOnly() && botSettings.getBoolean(BotBooleanComponent.RelayAchievements)) {
							channel.message(ircMessage);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getIdentifier());
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
			IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getIdentifier());
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
