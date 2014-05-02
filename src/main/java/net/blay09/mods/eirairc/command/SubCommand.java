package net.blay09.mods.eirairc.command;

import java.util.ArrayList;
import java.util.List;

import net.blay09.mods.eirairc.irc.IRCTarget;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public abstract class SubCommand implements ICommand {

	public abstract boolean hasQuickCommand();
	
	public abstract boolean processCommand(ICommandSender sender, IRCTarget context, String[] args, boolean serverSide);
	
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
		if(aliases != null) {
			List<String> list = new ArrayList<String>();
			for(int i = 0; i < aliases.length; i++) {
				list.add(aliases[i]);
			}
			return list;
		}
		return null;
	}
	
	@Override
	public abstract boolean canCommandSenderUseCommand(ICommandSender sender);
	
	@Override
	public abstract boolean isUsernameIndex(String[] args, int idx);
	
	public abstract void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args);
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		processCommand(sender, Utils.getSuggestedTarget(), args, Utils.isServerSide());
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		List<String> list = new ArrayList<String>();
		addTabCompletionOptions(list, sender, args);
		return list;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
	
	public static String joinArgs(String[] args, int startIdx) {
		StringBuilder sb = new StringBuilder();
		for(int i = startIdx; i < args.length; i++) {
			sb.append(args[i]);
		}
		return sb.toString();
	}
}
