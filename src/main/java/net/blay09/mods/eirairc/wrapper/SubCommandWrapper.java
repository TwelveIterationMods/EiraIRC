// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.wrapper;

import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.ArrayList;
import java.util.List;

public final class SubCommandWrapper implements ICommand {

	public final SubCommand command;

	public SubCommandWrapper(SubCommand command) {
		this.command = command;
	}

	@Override
	public String getCommandName() {
		return command.getCommandName();
	}
	
	@Override
	public final String getCommandUsage(ICommandSender sender) {
		return command.getCommandUsage(new ICommandSender(sender));
	}
	
	@Override
	public final List<String> getCommandAliases() {
		String[] aliases = command.getAliases();
		if(aliases != null) {
			List<String> list = new ArrayList<>();
			for(int i = 0; i < aliases.length; i++) {
				list.add(aliases[i]);
			}
			return list;
		}
		return null;
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return command.canCommandSenderUseCommand(new ICommandSender(sender));
	}
	
	@Override
	public boolean isUsernameIndex(String[] args, int idx){
		return command.isUsernameIndex(args, idx);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		command.processCommand(new ICommandSender(sender), Utils.getSuggestedTarget(), args, Utils.isServerSide());
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		List<String> list = new ArrayList<>();
		command.addTabCompletionOptions(list, new ICommandSender(sender), args);
		return list;
	}

	@Override
	public int compareTo(Object o) {
		return getCommandName().compareTo(((ICommand) o).getCommandName());
	}

	public static void throwWrongUsageException(SubCommand command, ICommandSender sender) {
		throw new WrongUsageException(command.getCommandUsage(sender));
	}
}
