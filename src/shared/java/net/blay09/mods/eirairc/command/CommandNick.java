// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.blay09.mods.eirairc.wrapper.CommandSender;
import net.blay09.mods.eirairc.wrapper.SubCommandWrapper;
import net.minecraft.command.CommandException;

import java.util.List;

public class CommandNick implements SubCommand {

	@Override
	public String getCommandName() {
		return "nick";
	}

	@Override
	public String getCommandUsage(CommandSender sender) {
		return "eirairc:commands.nick.usage";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(CommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
		if(args.length < 1) {
			SubCommandWrapper.throwWrongUsageException(this, sender);
		}
		if(args.length >= 2) {
			ServerConfig serverConfig = ConfigHelper.resolveServerConfig(args[0]);
			if(serverConfig == null) {
				Utils.sendLocalizedMessage(sender, "error.serverNotFound", args[0]);
				return true;
			}
			String nick = args[1];
			Utils.sendLocalizedMessage(sender, "commands.nick", serverConfig.getAddress(), nick);
			serverConfig.setNick(nick);
			IRCConnection connection = ConnectionManager.getConnection(serverConfig.getIdentifier());
			if(connection != null) {
				connection.nick(nick);
			}
		} else {
			String nick = args[0];
			if(context == null) {
				Utils.sendLocalizedMessage(sender, "commands.nick", "Global", nick);
				for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
					if(serverConfig.getAddress().equals(Globals.TWITCH_SERVER)) {
						continue;
					}
					if(serverConfig.getNick() == null || serverConfig.getNick().isEmpty()) {
						IRCConnection connection = ConnectionManager.getConnection(serverConfig.getIdentifier());
						if(connection != null) {
							connection.nick(ConfigHelper.formatNick(nick));
						}
					}
				}
			} else {
				IRCConnection connection = context.getConnection();
				if(connection.isTwitch()) {
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
	public boolean canCommandSenderUseCommand(CommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, CommandSender sender, String[] args) {
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
