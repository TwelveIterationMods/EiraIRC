// Copyright (c) 2015 Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.AuthManager;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

public class CommandConnect implements SubCommand {

	@Override
	public String getCommandName() {
		return "connect";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:commands.connect.usage";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(args.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		String host = args[0];
		if(EiraIRC.instance.getConnectionManager().isConnectedTo(host)) {
			Utils.sendLocalizedMessage(sender, "general.alreadyConnected", host);
			return true;
		}
		Utils.sendLocalizedMessage(sender, "general.connecting", host);
		ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(host);
		if(args.length >= 2) {
			AuthManager.putServerPassword(serverConfig.getIdentifier(), args[1]);
		}
		if(Utils.connectTo(serverConfig) != null) {
			ConfigurationHandler.addServerConfig(serverConfig);
			ConfigurationHandler.save();
		} else {
			Utils.sendLocalizedMessage(sender, "commands.connect.error", host);
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
