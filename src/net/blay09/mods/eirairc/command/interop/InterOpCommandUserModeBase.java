// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.interop;

import java.util.List;

import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.command.SubCommand;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.IRCTargetError;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class InterOpCommandUserModeBase extends SubCommand {

	private String name;
	private String mode;
	
	public InterOpCommandUserModeBase(String name, String mode) {
		this.name = name;
		this.mode = mode;
	}
	
	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(args.length < 2) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		IRCContext targetChannel = IRCResolver.resolveTarget(args[0], (short) (IRCResolver.FLAG_CHANNEL + IRCResolver.FLAG_ONCHANNEL));
		if(targetChannel instanceof IRCTargetError) {
			Utils.sendLocalizedMessage(sender, targetChannel.getName(), args[0]);
			return true;
		}
		if(!targetChannel.getConnection().getBot().getProfile(targetChannel).isInterOp()) {
			Utils.sendLocalizedMessage(sender, "irc.interop.disabled");
			return true;
		}
		IRCContext targetUser = IRCResolver.resolveTarget(args[1], (short) (IRCResolver.FLAG_USER + IRCResolver.FLAG_USERONCHANNEL));
		if(targetUser instanceof IRCTargetError) {
			Utils.sendLocalizedMessage(sender, targetUser.getName(), args[1]);
			return true;
		}
		targetChannel.getConnection().mode(targetChannel.getName(), mode, targetUser.getName());
		Utils.sendLocalizedMessage(sender, "irc.interop." + name, targetUser.getName(), targetChannel.getName());
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public boolean hasQuickCommand() {
		return false;
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.interop." + name;
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
	}

}
