// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.blay09.mods.eirairc.wrapper.SubCommandWrapper;
import net.minecraft.command.CommandException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CommandConfig implements SubCommand {

	public static final String TARGET_GLOBAL = "global";
	
	@Override
	public String getCommandName() {
		return "config";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:commands.config.usage";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "cfg" };
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
		if(args.length < 1) {
			SubCommandWrapper.throwWrongUsageException(this, sender);
		}
		String target = args[0];
		if(target.equals("reload")) {
			Utils.sendLocalizedMessage(sender, "commands.config.reload");
			ConnectionManager.stopIRC();
			ConfigurationHandler.reloadAll();
			ConnectionManager.startIRC();
			return true;
		}
		if(args.length < 2) {
			SubCommandWrapper.throwWrongUsageException(this, sender);
		}
		String config = args[1];
		if(args.length > 2) {
			ConfigurationHandler.handleConfigCommand(sender, target, config, StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " "));
		} else {
			ConfigurationHandler.handleConfigCommand(sender, target, config);
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		if(args.length == 1) {
			list.add("reload");
			list.add(TARGET_GLOBAL);
			Utils.addConnectionsToList(list);
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				list.add(serverConfig.getAddress());
				for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
					list.add(channelConfig.getName());
				}
			}
		} else if(args.length == 2) {
			if(args[0].equals("reload")) {
				return;
			}
			if(args[0].equals(TARGET_GLOBAL)) {
				ConfigurationHandler.addOptionsToList(list, args[1], true);
			} else if(IRCResolver.isChannel(args[0])) {
				IRCContext channel = EiraIRCAPI.parseContext(null, args[0], IRCContext.ContextType.IRCChannel);
				ConfigHelper.getBotSettings(channel).addOptionsToList(list, args[1], true);
			} else {
				IRCContext server = EiraIRCAPI.parseContext(null, args[0], IRCContext.ContextType.IRCConnection);
				ConfigHelper.getBotSettings(server).addOptionsToList(list, args[1], true);
			}
		} else if(args.length == 3) {
			if(args[0].equals("reload")) {
				return;
			}
			if(args[0].equals(TARGET_GLOBAL)) {
				ConfigurationHandler.addOptionsToList(list, args[1], false);
			} else if(IRCResolver.isChannel(args[0])) {
				IRCContext channel = EiraIRCAPI.parseContext(null, args[0], IRCContext.ContextType.IRCChannel);
				ConfigHelper.getBotSettings(channel).addOptionsToList(list, args[1], false);
			} else {
				IRCContext server = EiraIRCAPI.parseContext(null, args[0], IRCContext.ContextType.IRCConnection);
				ConfigHelper.getBotSettings(server).addOptionsToList(list, args[1], false);
			}
		}
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public boolean hasQuickCommand() {
		return false;
	}

}
