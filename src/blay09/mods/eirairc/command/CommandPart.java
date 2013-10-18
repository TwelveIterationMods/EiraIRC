package blay09.mods.eirairc.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class CommandPart implements ICommand {

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "part";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "commands.irc.usage.leave";
	}

	@Override
	public List getCommandAliases() {
		List list = new ArrayList();
		list.add("leave");
		return list;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		IRCCommandHandler.processCommand(sender, IRCCommandHandler.getShiftedArgs(args), true);
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return IRCCommandHandler.addTabCompletionOptions(getCommandName(), sender, IRCCommandHandler.getShiftedArgs(args));
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return IRCCommandHandler.isUsernameIndex(IRCCommandHandler.getShiftedArgs(args), i);
	}

}
