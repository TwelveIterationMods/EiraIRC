// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class CommandIRC implements ICommand {

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "irc";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return Globals.MOD_ID + ":irc.commands.irc";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length < 1) {
			IRCCommandHandler.sendIRCUsage(sender);
			return;
		}
		String cmd = args[0];
		IRCCommandHandler.processCommand(sender, args, true);
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return IRCCommandHandler.addTabCompletionOptions(getCommandName(), sender, args);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return IRCCommandHandler.isUsernameIndex(IRCCommandHandler.getShiftedArgs(args, getCommandName()), i);
	}

}
