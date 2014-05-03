package net.blay09.mods.eirairc.bot;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IBot;
import net.blay09.mods.eirairc.api.IBotCommand;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class BotCommandMessage implements IBotCommand {

	@Override
	public String getCommandName() {
		return "msg";
	}

	@Override
	public boolean isChannelCommand() {
		return false;
	}

	@Override
	public void processCommand(IBot bot, IIRCChannel channel, IIRCUser user, String[] args) {
		if(!GlobalConfig.allowPrivateMessages || !bot.getBoolean("allowPrivateMessages")) {
			user.notice(Utils.getLocalizedMessage("irc.msg.disabled"));
		}
		String playerName = args[0];
		EntityPlayer entityPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
		if(entityPlayer == null) {
			List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			for(EntityPlayer entity : playerEntityList) {
				if(Utils.getAliasForPlayer(entity, false).equals(playerName) || Utils.getAliasForPlayer(entity, true).equals(playerName)) {
					entityPlayer = entity;
				}
			}
			if(entityPlayer == null) {
				user.notice(Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
				return;
			}
		}
		String message = Utils.joinArgs(args, 1);
		EiraIRC.instance.getIRCEventHandler().onIRCPrivateMessageToPlayer(bot.getConnection(), user, entityPlayer, message);
		user.notice(Utils.getLocalizedMessage("irc.bot.msgSent", playerName, message));
	}
	
}
