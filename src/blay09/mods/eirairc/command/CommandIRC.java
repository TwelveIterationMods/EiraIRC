// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.command;

import java.util.ArrayList;
import java.util.List;

import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.Globals;
import blay09.mods.eirairc.util.Utils;
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
		return "irc.commands.irc";
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
			IRCCommandHandler.sendIRCUsage(sender);
			return;
		}
		String cmd = args[0];
		if(cmd.equals("who")) {
			IRCCommandHandler.processCommand(sender, args, true);
		} else if(cmd.equals("color")) {
			if(!GlobalConfig.enableNameColors) {
				Utils.sendLocalizedMessage(sender, "irc.color.disabled");
				return;
			}
			if(args.length < 2) {
				throw new WrongUsageException("EiraIRC:irc.commands.color");
			}
			if(sender instanceof EntityPlayer) {
				String colorName = args[1].toLowerCase();
				if(!isOP(sender) && (GlobalConfig.colorBlackList.contains(colorName) || GlobalConfig.opColor.equals(colorName))) {
					Utils.sendLocalizedMessage(sender, "irc.color.blackList", colorName);
					return;
				}
				if(!colorName.equals("none") && !Utils.isValidColor(colorName)) {
					Utils.sendLocalizedMessage(sender, "irc.color.invalid", colorName);
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
				if(colorName.equals("none")) {
					Utils.sendLocalizedMessage(sender, "irc.color.reset", colorName);
				} else {
					Utils.sendLocalizedMessage(sender, "irc.color.set", colorName);
				}
			}
		} else if(cmd.equals("alias")) {
			if(!GlobalConfig.enableAliases) {
				Utils.sendLocalizedMessage(sender, "irc.alias.disabled");
				return;
			}
			if(args.length < 2) {
				throw new WrongUsageException("EiraIRC:irc.commands.alias");
			}
			List<EntityPlayerMP> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			if(args.length < 3) {
				String alias = args[1];
				for(int i = 0; i < playerEntityList.size(); i++) {
					EntityPlayerMP playerEntity = playerEntityList.get(i);
					if(playerEntity.getEntityData().getCompoundTag("EiraIRC").getString("Alias").equals(alias)) {
						Utils.sendLocalizedMessage(sender, "irc.alias.lookup", alias, playerEntity.username);
						return;
					}
				}
				Utils.sendLocalizedMessage(sender, "irc.alias.notFound", alias);
			} else {
				if(!isOP(sender)) {
					Utils.sendLocalizedMessage(sender, "irc.gemeral.noPermission");
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
						Utils.sendLocalizedMessage(sender, "irc.general.noSuchPlayer");
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
				if(alias.equals("none")) {
					Utils.sendLocalizedMessage(sender, "irc.alias.reset", oldAlias, alias);
				} else {
					Utils.sendLocalizedMessage(sender, "irc.alias.set", oldAlias, alias);
				}
				EiraIRC.instance.getMCEventHandler().onPlayerNickChange(oldAlias, alias);
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
		return IRCCommandHandler.isUsernameIndex(IRCCommandHandler.getShiftedArgs(args, getCommandName()), i);
	}

}
