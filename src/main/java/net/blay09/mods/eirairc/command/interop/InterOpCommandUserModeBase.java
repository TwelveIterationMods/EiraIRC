// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.command.interop;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.settings.BotBooleanComponent;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

public class InterOpCommandUserModeBase implements SubCommand {

    private final String name;
    private final String mode;
    private final boolean useHostMask;

    public InterOpCommandUserModeBase(String name, String mode, boolean useHostMask) {
        this.name = name;
        this.mode = mode;
        this.useHostMask = useHostMask;
    }

    @Override
    public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        IRCContext targetChannel = EiraIRCAPI.parseContext(null, args[0], IRCContext.ContextType.IRCChannel);
        if (targetChannel.getContextType() == IRCContext.ContextType.Error) {
            Utils.sendLocalizedMessage(sender, targetChannel.getName(), args[0]);
            return true;
        }
        if (!ConfigHelper.getBotSettings(targetChannel).getBoolean(BotBooleanComponent.InterOp)) {
            Utils.sendLocalizedMessage(sender, "commands.interop.disabled");
            return true;
        }
        if (args[1].contains("@")) {
            targetChannel.getConnection().mode(targetChannel.getName(), mode, args[1]);
            Utils.sendLocalizedMessage(sender, "commands." + name, args[1], targetChannel.getName());
        } else {
            IRCContext targetUser = EiraIRCAPI.parseContext(targetChannel, args[1], IRCContext.ContextType.IRCUser);
            if (targetUser.getContextType() == IRCContext.ContextType.Error) {
                Utils.sendLocalizedMessage(sender, targetUser.getName(), args[1]);
                return true;
            }
            targetChannel.getConnection().mode(targetChannel.getName(), mode, useHostMask ? ("*!*@" + ((IRCUser) targetUser).getHostname()) : targetUser.getName());
            Utils.sendLocalizedMessage(sender, "commands." + name, targetUser.getName(), targetChannel.getName());
        }
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
        return "eirairc:commands." + name + ".usage";
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
