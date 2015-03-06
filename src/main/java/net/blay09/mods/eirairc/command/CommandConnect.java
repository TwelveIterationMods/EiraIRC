// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandConnect implements SubCommand {

	@Override
	public String getCommandName() {
		return "connect";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.connect";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
		if(args.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		String host = args[0];
		if(EiraIRC.instance.getConnectionManager().isConnectedTo(host)) {
			Utils.sendLocalizedMessage(sender, "irc.general.alreadyConnected", host);
			return true;
		}
		Utils.sendLocalizedMessage(sender, "irc.basic.connecting", host);
		ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(host);
		if(args.length >= 2) {
			serverConfig.setServerPassword(args[1]);
		}
		if(Utils.connectTo(serverConfig) != null) {
			ConfigurationHandler.addServerConfig(serverConfig);
			ConfigurationHandler.save();
		} else {
			Utils.sendLocalizedMessage(sender, "irc.connect.error", host);
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		if(args.length == 0) {
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				list.add(serverConfig.getAddress());
			}
		}
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
