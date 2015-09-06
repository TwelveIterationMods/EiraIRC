// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.command.base;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class IgnoreCommand implements ICommand {

	private final String name;
	
	public IgnoreCommand(String name) {
		this.name = name;
	}
	
	@Override
	public int compareTo(Object o) {
		return getName().compareTo(((ICommand) o).getName());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "";
	}

	@Override
	public List getAliases() {
		return new ArrayList();
	}

	@Override
	public void execute(ICommandSender sender, String[] args) {
		System.out.println("Ignoring command " + name + " with " + args.length + " arguments");
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender sender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] sender, int args) {
		return false;
	}

}
