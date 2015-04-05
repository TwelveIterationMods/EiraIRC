// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandNick implements SubCommand {

	@Override
	public String getCommandName() {
		return "nick";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.nick";
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
		if(args.length >= 2) {
			ServerConfig serverConfig = ConfigHelper.resolveServerConfig(args[0]);
			if(serverConfig == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[0]);
				return true;
			}
			String nick = args[1];
			Utils.sendLocalizedMessage(sender, "irc.basic.changingNick", serverConfig.getAddress(), nick);
			serverConfig.setNick(nick);
			IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getAddress());
			if(connection != null) {
				connection.nick(nick);
			}
		} else {
			String nick = args[0];
			if(context == null) {
				Utils.sendLocalizedMessage(sender, "irc.basic.changingNick", "Global", nick);
				for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
					if(serverConfig.getAddress().equals(Globals.TWITCH_SERVER)) {
						continue;
					}
					if(serverConfig.getNick() == null || serverConfig.getNick().isEmpty()) {
						IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(serverConfig.getAddress());
						if(connection != null) {
							connection.nick(ConfigHelper.formatNick(nick));
						}
					}
				}
			} else {
				IRCConnection connection = context.getConnection();
				if(connection.getHost().equals(Globals.TWITCH_SERVER)) {
					return true;
				}
				connection.nick(ConfigHelper.formatNick(nick));
				ServerConfig serverConfig = ConfigHelper.getServerConfig(connection);
				serverConfig.setNick(nick);
			}
		}
		ConfigurationHandler.save();
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
		return true;
	}

}
