// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.extension;

import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

public class CommandNickServ implements SubCommand {

	@Override
	public String getCommandName() {
		return "nickserv";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.nickserv";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "ns" };
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(!serverSide) {
			Utils.sendLocalizedMessage(sender, "irc.general.serverOnlyCommand");
			return true;
		}
		if(args.length < 2) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		IRCConnection connection = null;
		int argidx = 0;
		if(args.length >= 3) {
			connection = IRCResolver.resolveConnection(args[0], IRCResolver.FLAGS_NONE);
			if(connection == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[0]);
				return true;
			}
			argidx = 1;
		} else {
			if(context == null) {
				Utils.sendLocalizedMessage(sender, "irc.specifyServer");
				return true;
			}
			connection = context.getConnection();
		}
		ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(connection.getHost());
		serverConfig.setNickServ(args[argidx], args[argidx + 1]);
		ConfigurationHandler.save();
		Utils.doNickServ(connection, serverConfig);
		Utils.sendLocalizedMessage(sender, "irc.basic.nickServUpdated", connection.getHost());
		return true;
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
		return false;
	}

}
