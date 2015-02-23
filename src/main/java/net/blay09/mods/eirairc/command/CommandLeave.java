// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

public class CommandLeave implements SubCommand {

	private static final String TARGET_ALL = "all";

	@Override
	public String getCommandName() {
		return "leave";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.leave";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "part" };
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(context == null && args.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		IRCConnection connection;
		String channelName;
		if(args.length > 0) {
			IRCContext target = EiraIRCAPI.parseContext(null, args[0], IRCContext.ContextType.IRCConnection);
			if(target.getContextType() == IRCContext.ContextType.Error) {
				Utils.sendLocalizedMessage(sender, target.getName(), args[0]);
				return true;
			}
			connection = target.getConnection();
			channelName = IRCResolver.stripPath(args[0]);
		} else {
			if(context == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.specifyServer");
				return true;
			}
			connection = context.getConnection();
			channelName = context.getName();
		}
		if(channelName.equals(TARGET_ALL)) {
			for(IRCChannel channel : connection.getChannels()) {
				connection.part(channel.getName());
			}
			Utils.sendLocalizedMessage(sender, "irc.basic.leavingChannel", "<all>", connection.getHost());
		} else {
			Utils.sendLocalizedMessage(sender, "irc.basic.leavingChannel", channelName, connection.getHost());
			connection.part(channelName);
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
