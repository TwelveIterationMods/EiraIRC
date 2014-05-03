package net.blay09.mods.eirairc.command.base;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class CommandClientEmote implements ICommand {

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "me";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.me.usage";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
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
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

}
