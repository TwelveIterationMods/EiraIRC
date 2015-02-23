package net.blay09.mods.eirairc.api;

import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.minecraft.command.ICommandSender;

import java.util.List;

/**
 * Created by Blay09 on 23.02.2015.
 */
public interface SubCommand {

	public String getCommandName();
	public boolean hasQuickCommand();
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide);
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args);
	public boolean canCommandSenderUseCommand(ICommandSender sender);
	public boolean isUsernameIndex(String[] args, int idx);
	public String getCommandUsage(ICommandSender sender);
	public String[] getAliases();

}
