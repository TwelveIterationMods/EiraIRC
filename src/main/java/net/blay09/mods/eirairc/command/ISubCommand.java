package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.irc.IRCTarget;
import net.minecraft.command.ICommandSender;

public interface ISubCommand {

	public String getCommandName();
	public String getCommandUsage(ICommandSender sender);
	public List<String> getCommandAliases();
	public boolean processCommand(ICommandSender sender, IRCTarget context, String[] args, boolean serverSide);
	public boolean canCommandSenderUseCommand(ICommandSender sender);
	public boolean isUsernameIndex(String[] args, int idx);
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args);
	
}
