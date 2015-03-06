// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.interop;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.config.settings.BotBooleanComponent;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;

import java.util.List;

public class InterOpCommandUserModeBase implements SubCommand {

	private String name;
	private String mode;
	
	public InterOpCommandUserModeBase(String name, String mode) {
		this.name = name;
		this.mode = mode;
	}
	
	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
		if(args.length < 2) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		IRCContext targetChannel = EiraIRCAPI.parseContext(null, args[0], IRCContext.ContextType.IRCChannel);
		if(targetChannel.getContextType() == IRCContext.ContextType.Error) {
			Utils.sendLocalizedMessage(sender, targetChannel.getName(), args[0]);
			return true;
		}
		if(!ConfigHelper.getBotSettings(targetChannel).getBoolean(BotBooleanComponent.InterOp)) {
			Utils.sendLocalizedMessage(sender, "irc.interop.disabled");
			return true;
		}
		IRCContext targetUser = EiraIRCAPI.parseContext(targetChannel, args[1], IRCContext.ContextType.IRCUser);
		if(targetUser.getContextType() == IRCContext.ContextType.Error) {
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
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.interop." + name;
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
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {}

}
