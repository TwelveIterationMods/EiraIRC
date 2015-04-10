// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.extension;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

public class CommandTwitch implements SubCommand {

	@Override
	public String getCommandName() {
		return "twitch";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.twitch";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
		if(EiraIRCAPI.isConnectedTo(Globals.TWITCH_SERVER)) {
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
			if(args.length < 2) {
				throw new WrongUsageException(Globals.MOD_ID + ":irc.commands.twitch");
			}
			ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(Globals.TWITCH_SERVER);
			serverConfig.setNick(args[0]);
			serverConfig.setServerPassword(args[1]);
			ConfigurationHandler.addServerConfig(serverConfig);
			ConfigurationHandler.save();
			Utils.sendLocalizedMessage(sender, "irc.basic.connecting", "Twitch");
			Utils.connectTo(serverConfig);
			return true;
		}
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
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
