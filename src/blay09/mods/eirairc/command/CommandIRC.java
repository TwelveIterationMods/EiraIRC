// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.command;

import java.util.ArrayList;
import java.util.List;

import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.Utils;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.Globals;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class CommandIRC implements ICommand {

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "irc";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "commands.irc.usage";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	public boolean isOP(ICommandSender sender) {
		if(sender instanceof EntityPlayer) {
			if(MinecraftServer.getServer().isSinglePlayer()) {
				return true;
			}
			return MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).getOps().contains(((EntityPlayer)sender).username.toLowerCase().trim());
		}
		return true;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length < 1) {
			throw new WrongUsageException("commands.irc.usage", "irc");
		}
		String cmd = args[0];
		if(cmd.equals("who")) {
			IRCCommandHandler.processCommand(sender, args, true);
		} else if(cmd.equals("color")) {
			if(!GlobalConfig.enableNameColors) {
				IRCCommandHandler.sendLocalizedMessage(sender, "irc.colorDisabled");
				return;
			}
			if(args.length < 2) {
				throw new WrongUsageException("commands.irc.usage.color");
			}
			if(sender instanceof EntityPlayer) {
				String colorName = args[1].toLowerCase();
				if(!isOP(sender) && (GlobalConfig.colorBlackList.contains(colorName) || GlobalConfig.opColor.equals(colorName))) {
					IRCCommandHandler.sendLocalizedMessage(sender, "irc.colorBlackList", colorName);
					return;
				}
				if(!colorName.equals("none") && !Utils.isValidColor(colorName)) {
					IRCCommandHandler.sendLocalizedMessage(sender, "irc.colorInvalid", colorName);
					return;
				}
				EntityPlayer entityPlayer = (EntityPlayer) sender;
				NBTTagCompound persistentTag = entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
				NBTTagCompound tagCompound = persistentTag.getCompoundTag("EiraIRC");
				if(colorName.equals("none")) {
					tagCompound.removeTag("NameColor");
				} else {
					tagCompound.setString("NameColor", colorName);
				}
				persistentTag.setTag("EiraIRC", tagCompound);
				entityPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
				IRCCommandHandler.sendLocalizedMessage(sender, "irc.colorSet", colorName);
			}
		} else if(cmd.equals("alias")) {
			if(!GlobalConfig.enableAliases) {
				IRCCommandHandler.sendLocalizedMessage(sender, "irc.aliasDisabled");
				return;
			}
			if(args.length < 2) {
				throw new WrongUsageException("commands.irc.usage.alias");
			}
			List<EntityPlayerMP> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			if(args.length < 3) {
				String alias = args[1];
				for(int i = 0; i < playerEntityList.size(); i++) {
					EntityPlayerMP playerEntity = playerEntityList.get(i);
					if(playerEntity.getEntityData().getCompoundTag("EiraIRC").getString("Alias").equals(alias)) {
						IRCCommandHandler.sendLocalizedMessage(sender, "irc.aliasLookup", alias, playerEntity.username);
						return;
					}
				}
				IRCCommandHandler.sendLocalizedMessage(sender, "irc.aliasNotFound", alias);
			} else {
				if(!isOP(sender)) {
					IRCCommandHandler.sendLocalizedMessage(sender, "irc.nopermission");
					return;
				}
				String username = args[1];
				String alias = args[2];
				EntityPlayerMP entityPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(username);
				if(entityPlayer == null) {
					for(int i = 0; i < playerEntityList.size(); i++) {
						EntityPlayerMP playerEntity = playerEntityList.get(i);
						if(playerEntity.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("EiraIRC").getString("Alias").equals(username)) {
							entityPlayer = playerEntity;
							break;
						}
					}
					if(entityPlayer == null) {
						IRCCommandHandler.sendLocalizedMessage(sender, "irc.nosuchplayer");
						return;
					}
				}
				NBTTagCompound persistentTag = entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
				NBTTagCompound tagCompound = persistentTag.getCompoundTag("EiraIRC");
				String oldAlias = username;
				if(tagCompound.hasKey("Alias")) {
					oldAlias = tagCompound.getString("Alias");
				}
				if(alias.equals("none")) {
					tagCompound.removeTag("Alias");
				} else {
					tagCompound.setString("Alias", alias);
				}
				persistentTag.setTag("EiraIRC", tagCompound);
				entityPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
				IRCCommandHandler.sendLocalizedMessage(sender, "irc.aliasSet", oldAlias, alias);
				EiraIRC.instance.getEventHandler().onPlayerNickChange(oldAlias, alias);
			}
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return IRCCommandHandler.addTabCompletionOptions(getCommandName(), sender, args);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return false;
	}

}
