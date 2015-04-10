// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

public class CommandJoin implements SubCommand {

	@Override
	public String getCommandName() {
		return "join";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.join";
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
		IRCContext target = EiraIRCAPI.parseContext(null, args[0], IRCContext.ContextType.IRCConnection);
		IRCConnection connection;
		if(target.getContextType() == IRCContext.ContextType.Error) {
			if(args[0].indexOf('/') != -1) {
				Utils.sendLocalizedMessage(sender, target.getName(), args[0]);
				return true;
			} else {
				if(context == null) {
					Utils.sendLocalizedMessage(sender, "irc.target.specifyServer");
					return true;
				}
				target = context.getConnection();
			}
		}
		connection = (IRCConnection) target;
		ServerConfig serverConfig = ConfigHelper.getServerConfig(connection);
		String channelName = IRCResolver.stripPath(args[0]);
		ChannelConfig channelConfig = serverConfig.getOrCreateChannelConfig(channelName);
		if(args.length >= 2) {
			channelConfig.setPassword(args[1]);
		}
		Utils.sendLocalizedMessage(sender, "irc.basic.joiningChannel", channelConfig.getName(), connection.getHost());
		connection.join(channelConfig.getName(), channelConfig.getPassword());
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
				for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
					list.add(channelConfig.getName());
				}
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
