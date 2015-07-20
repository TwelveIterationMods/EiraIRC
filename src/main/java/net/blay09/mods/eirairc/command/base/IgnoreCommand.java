// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command.base;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class IgnoreCommand implements ICommand {

	private String name;
	
	public IgnoreCommand(String name) {
		this.name = name;
	}
	
	@Override
	public int compareTo(Object o) {
		return getCommandName().compareTo(((ICommand) o).getCommandName());
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		System.out.println("Ignoring command " + name + " with " + args.length + " arguments");
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] sender, int args) {
		return false;
	}

}
