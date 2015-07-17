// Copyright (c) 2015 Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.irc.IRCChannelImpl;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CommandWho implements SubCommand {

	@Override
	public String getCommandName() {
		return "who";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.who";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		IRCContext target = null;
		if(args.length > 0) {
			target = EiraIRCAPI.parseContext(null, args[0], null);
			if(target.getContextType() == IRCContext.ContextType.Error) {
				Utils.sendLocalizedMessage(sender, target.getName(), args[0]);
				return true;
			}
		} else {
			if(context != null) {
				target = context;
			}
		}
		if(target == null) {
			for(IRCConnection connection : EiraIRC.instance.getConnectionManager().getConnections()) {
				for(IRCChannel channel : connection.getChannels()) {
					Utils.sendUserList(sender, connection, channel);
				}
			}
		} else if(target.getContextType() == IRCContext.ContextType.IRCChannel) {
			Utils.sendUserList(sender, target.getConnection(), (IRCChannelImpl) target);
		} else if(target.getContextType() == IRCContext.ContextType.IRCConnection) {
			for(IRCChannel channel : target.getConnection().getChannels()) {
				Utils.sendUserList(sender, target.getConnection(), channel);
			}
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
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
