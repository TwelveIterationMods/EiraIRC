// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.extension;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.command.SubCommand;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class CommandAlias extends SubCommand {

	private static final String ALIAS_NONE = "none";
	
	@Override
	public String getCommandName() {
		return "alias";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.alias";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(!SharedGlobalConfig.enablePlayerAliases) {
			Utils.sendLocalizedMessage(sender, "irc.alias.disabled");
			return true;
		}
		if(args.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		List<EntityPlayerMP> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		if(args.length < 2) {
			String alias = args[0];
			for(int i = 0; i < playerEntityList.size(); i++) {
				EntityPlayerMP playerEntity = playerEntityList.get(i);
				if(playerEntity.getEntityData().getCompoundTag(Globals.NBT_EIRAIRC).getString(Globals.NBT_ALIAS).equals(alias)) {
					Utils.sendLocalizedMessage(sender, "irc.alias.lookup", alias, playerEntity.getCommandSenderName());
					return true;
				}
			}
			Utils.sendLocalizedMessage(sender, "irc.alias.notFound", alias);
		} else {
			String username = args[0];
			String alias = args[1];
			EntityPlayerMP entityPlayer = MinecraftServer.getServer().getConfigurationManager().func_152612_a(username); // getPlayerForUsername
			if(entityPlayer == null) {
				for(int i = 0; i < playerEntityList.size(); i++) {
					EntityPlayerMP playerEntity = playerEntityList.get(i);
					if(playerEntity.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag(Globals.NBT_EIRAIRC).getString(Globals.NBT_ALIAS).equals(username)) {
						entityPlayer = playerEntity;
						break;
					}
				}
				if(entityPlayer == null) {
					Utils.sendLocalizedMessage(sender, "irc.general.noSuchPlayer");
					return true;
				}
			}
			NBTTagCompound persistentTag = entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			NBTTagCompound tagCompound = persistentTag.getCompoundTag(Globals.NBT_EIRAIRC);
			String oldAlias = username;
			if(tagCompound.hasKey(Globals.NBT_ALIAS)) {
				oldAlias = tagCompound.getString(Globals.NBT_ALIAS);
			}
			if(alias.equals(ALIAS_NONE)) {
				tagCompound.removeTag(Globals.NBT_ALIAS);
			} else {
				tagCompound.setString(Globals.NBT_ALIAS, alias);
			}
			persistentTag.setTag(Globals.NBT_EIRAIRC, tagCompound);
			entityPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
			if(alias.equals(ALIAS_NONE)) {
				Utils.sendLocalizedMessage(sender, "irc.alias.reset");
			} else {
				Utils.sendLocalizedMessage(sender, "irc.alias.set", oldAlias, alias);
			}
			entityPlayer.refreshDisplayName();
			EiraIRC.instance.getMCEventHandler().onPlayerNickChange(entityPlayer, oldAlias);
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		list.add(ALIAS_NONE);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public boolean hasQuickCommand() {
		return true;
	}

}
