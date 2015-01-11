// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import java.util.ArrayList;
import java.util.List;

import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;

public abstract class SubCommand implements ICommand {

	public abstract boolean hasQuickCommand();
	
	public abstract boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException;
	
	public abstract String getUsageString(ICommandSender sender);
	
	public abstract String[] getAliases();
	
	@Override
	public abstract String getCommandName();
	
	@Override
	public final String getCommandUsage(ICommandSender sender) {
		return Globals.MOD_ID + ":" + getUsageString(sender);
	}
	
	@Override
	public final List<String> getCommandAliases() {
		String[] aliases = getAliases();
		List<String> list = new ArrayList<String>();
		if(aliases != null) {
			for(int i = 0; i < aliases.length; i++) {
				list.add(aliases[i]);
			}
			return list;
		}
		return list;
	}
	
	@Override
	public abstract boolean canCommandSenderUseCommand(ICommandSender sender);
	
	@Override
	public abstract boolean isUsernameIndex(String[] args, int idx);
	
	public abstract void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args, BlockPos pos);
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		processCommand(sender, Utils.getSuggestedTarget(), args, Utils.isServerSide());
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		List<String> list = new ArrayList<String>();
		addTabCompletionOptions(list, sender, args, pos);
		return list;
	}

	@Override
	public int compareTo(Object o) {
		return getCommandName().compareTo(((ICommand) o).getCommandName());
	}
	
}
