package blay09.mods.eirairc.command;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class CommandConnect implements ICommand {

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "connect";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "commands.irc.usage.connect";
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
