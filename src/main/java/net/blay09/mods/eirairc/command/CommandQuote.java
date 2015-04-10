// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CommandQuote implements SubCommand {

	@Override
	public String getCommandName() {
		return "quote";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.quote";
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
			IRCContext target = EiraIRCAPI.parseContext(null, args[0], IRCContext.ContextType.IRCConnection).getConnection();
			if(target.getContextType() == IRCContext.ContextType.Error) {
				Utils.sendLocalizedMessage(sender, target.getName(), args[0]);
				return true;
			}
			connection = target.getConnection();
			msgIdx = 1;
		} else {
			connection = context.getConnection();
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
			for(IRCConnection connection : EiraIRC.instance.getConnectionManager().getConnections()) {
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
