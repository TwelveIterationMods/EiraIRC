// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.quick;

import java.util.List;

import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class CommandNick implements ICommand {

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "nick";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return Globals.MOD_ID + ":irc.commands.nick.short";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		IRCCommandHandler.processCommand(sender, IRCCommandHandler.getShiftedArgs(args, getCommandName()), true);
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return IRCCommandHandler.addTabCompletionOptions(getCommandName(), sender, IRCCommandHandler.getShiftedArgs(args, getCommandName()));
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return IRCCommandHandler.isUsernameIndex(IRCCommandHandler.getShiftedArgs(args, getCommandName()), i);
	}

}
