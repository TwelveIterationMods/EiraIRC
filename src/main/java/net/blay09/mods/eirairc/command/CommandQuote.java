// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;

public class CommandQuote extends SubCommand {

	@Override
	public String getCommandName() {
		return "quote";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.quote";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		int msgIdx = 0;
		IRCConnection connection;
		if(context == null) {
			if(args.length < 2) {
				Utils.sendLocalizedMessage(sender, "irc.target.specifyServer");
				return true;
			}
			connection = IRCResolver.resolveConnection(args[0], IRCResolver.FLAGS_NONE);
			msgIdx = 1;
		} else {
			connection = context.getConnection();
		}
		if(connection == null) {
			Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[0]);
			return true;
		}
		String msg = Utils.joinStrings(args, " ", msgIdx);
		connection.irc(msg);
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		if(args.length == 0) {
			for(IRCConnection connection : EiraIRC.instance.getConnections()) {
				list.add(connection.getHost());
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
