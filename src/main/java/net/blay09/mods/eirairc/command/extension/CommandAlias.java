// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.extension;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class CommandAlias implements SubCommand {

	private static final String ALIAS_NONE = "none";
	
	@Override
	public String getCommandName() {
		return "alias";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:irc.commands.alias";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
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
			for (EntityPlayerMP playerEntity : playerEntityList) {
				if (playerEntity.getEntityData().getCompoundTag(Globals.NBT_EIRAIRC).getString(Globals.NBT_ALIAS).equals(alias)) {
					Utils.sendLocalizedMessage(sender, "irc.alias.lookup", alias, playerEntity.getName());
					return true;
				}
			}
			Utils.sendLocalizedMessage(sender, "irc.alias.notFound", alias);
		} else {
			String username = args[0];
			String alias = args[1];
			EntityPlayerMP entityPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username);
			if(entityPlayer == null) {
				for (EntityPlayerMP playerEntity : playerEntityList) {
					if (playerEntity.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag(Globals.NBT_EIRAIRC).getString(Globals.NBT_ALIAS).equals(username)) {
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
