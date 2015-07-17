// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command.extension;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.IgnoreList;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

public class CommandUnignore implements SubCommand {

	@Override
	public String getCommandName() {
		return "unignore";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.unignore";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(args.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		IRCContext target = EiraIRCAPI.parseContext(context, args[0], IRCContext.ContextType.IRCUser);
		if(target.getContextType() == IRCContext.ContextType.Error) {
			Utils.sendLocalizedMessage(sender, target.getName(), args[0]);
			return true;
		}
		IRCUser user = (IRCUser) target;
		if(user.getHostname() == null) {
			Utils.sendLocalizedMessage(sender, "irc.commands.unignore.notKnown", target.getName());
			return true;
		}
		if(!IgnoreList.isIgnored(user)) {
			Utils.sendLocalizedMessage(sender, "irc.commands.unignore.notIgnored", target.getName());
			return true;
		}
		IgnoreList.removeFromIgnoreList(user);
		Utils.sendLocalizedMessage(sender, "irc.commands.unignore.removed", target.getName());
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public boolean hasQuickCommand() {
		return true;
	}

}
