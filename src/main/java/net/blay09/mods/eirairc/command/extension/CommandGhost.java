// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.extension;

import java.util.List;

import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.command.SubCommand;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.base.ServiceConfig;
import net.blay09.mods.eirairc.config.base.ServiceSettings;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;

public class CommandGhost extends SubCommand {

	@Override
	public String getCommandName() {
		return "ghost";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.ghost";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		IRCConnection connection = null;
		if(args.length > 0) {
			connection = IRCResolver.resolveConnection(args[0], IRCResolver.FLAGS_NONE);
			if(connection == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[0]);
				return true;
			}
		} else {
			if(context == null) {
				Utils.sendLocalizedMessage(sender, "irc.specifyServer");
				return true;
			}
			connection = context.getConnection();
		}
		ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(connection.getHost());
		ServiceSettings settings = ServiceConfig.getSettings(connection.getHost(), connection.getServerType());
		if(settings.hasGhostCommand()) {
			connection.irc(settings.getGhostCommand(serverConfig.getNickServName(), serverConfig.getNickServPassword()));
		} else {
			Utils.sendLocalizedMessage(sender, "irc.general.notSupported", "GHOST");
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
			Utils.addConnectionsToList(list);
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
