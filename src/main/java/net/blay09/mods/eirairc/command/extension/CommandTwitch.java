// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.extension;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.command.SubCommand;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandTwitch extends SubCommand {

	@Override
	public String getCommandName() {
		return "twitch";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.twitch";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(EiraIRC.instance.getConnectionManager().isConnectedTo(Globals.TWITCH_SERVER)) {
			Utils.sendLocalizedMessage(sender, "irc.general.alreadyConnected", "Twitch");
			return true;
		}
		if(args.length == 0) {
			if(ConfigurationHandler.hasServerConfig(Globals.TWITCH_SERVER)) {
				Utils.sendLocalizedMessage(sender, "irc.basic.connecting", "Twitch");
				ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(Globals.TWITCH_SERVER);
				Utils.connectTo(serverConfig);
				return true;
			} else {
				if(serverSide) {
					throw new WrongUsageException(getCommandUsage(sender));
				} else {
					Utils.sendLocalizedMessage(sender, "irc.general.serverOnlyCommand");
					return true;
				}
			}
		} else {
			if(args.length < 2 && !ConfigurationHandler.hasServerConfig(Globals.TWITCH_SERVER)) {
				throw new WrongUsageException(Globals.MOD_ID + ":irc.commands.twitch");
			}
			ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(Globals.TWITCH_SERVER);
			serverConfig.setNick(args[0]);
			serverConfig.setServerPassword(args[1]);
			String userChannel = "#" + args[0];
			if(!serverConfig.hasChannelConfig(userChannel)) {
				ChannelConfig channelConfig = serverConfig.getOrCreateChannelConfig(userChannel);
				serverConfig.addChannelConfig(channelConfig);
			}
			ConfigurationHandler.addServerConfig(serverConfig);
			ConfigurationHandler.save();
			Utils.sendLocalizedMessage(sender, "irc.basic.connecting", "Twitch");
			Utils.connectTo(serverConfig);
			return true;
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public boolean hasQuickCommand() {
		return true;
	}

}
