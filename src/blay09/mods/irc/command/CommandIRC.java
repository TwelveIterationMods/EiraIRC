// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.command;

import java.util.ArrayList;
import java.util.List;

import blay09.mods.irc.EiraIRC;
import blay09.mods.irc.Utils;
import blay09.mods.irc.config.GlobalConfig;
import blay09.mods.irc.config.Globals;
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
		return Globals.MOD_ID + ":commands.irc.usage";
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
			throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage", "irc");
		}
		String cmd = args[0];
		if(cmd.equals("who")) {
			IRCCommandHandler.processCommand(sender, args, true);
		} else if(cmd.equals("color")) {
			if(!GlobalConfig.enableNameColors) {
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.colorDisabled"));
				return;
			}
			if(args.length < 2) {
				throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.color");
			}
			if(sender instanceof EntityPlayer) {
				String colorName = args[1].toLowerCase();
				if(!isOP(sender) && (GlobalConfig.colorBlackList.contains(colorName) || GlobalConfig.opColor.equals(colorName))) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.colorBlackList", colorName));
					return;
				}
				if(!Utils.isValidColor(colorName)) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.colorInvalid", colorName));
					return;
				}
				EntityPlayer entityPlayer = (EntityPlayer) sender;
				NBTTagCompound persistentTag = entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
				NBTTagCompound tagCompound = persistentTag.getCompoundTag("EiraIRC");
				tagCompound.setString("NameColor", colorName);
				persistentTag.setTag("EiraIRC", tagCompound);
				entityPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.colorSet", args[1]));
			}
		} else if(cmd.equals("alias")) {
			if(!GlobalConfig.enableAliases) {
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.aliasDisabled"));
				return;
			}
			if(args.length < 2) {
				throw new WrongUsageException(Globals.MOD_ID + ":commands.irc.usage.alias");
			}
			List<EntityPlayerMP> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			if(args.length < 3) {
				for(int i = 0; i < playerEntityList.size(); i++) {
					EntityPlayerMP playerEntity = playerEntityList.get(i);
					if(playerEntity.getEntityData().getCompoundTag("EiraIRC").getString("Alias").equals(args[1])) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.aliasLookup", args[1], playerEntity.username));
						return;
					}
				}
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.aliasNotFound", args[1]));
			} else {
				if(!isOP(sender)) {
					sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.nopermission"));
					return;
				}
				EntityPlayerMP entityPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(args[1]);
				if(entityPlayer == null) {
					for(int i = 0; i < playerEntityList.size(); i++) {
						EntityPlayerMP playerEntity = playerEntityList.get(i);
						if(playerEntity.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("EiraIRC").getString("Alias").equals(args[1])) {
							entityPlayer = playerEntity;
							break;
						}
					}
					if(entityPlayer == null) {
						sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.nosuchplayer"));
						return;
					}
				}
				NBTTagCompound persistentTag = entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
				NBTTagCompound tagCompound = persistentTag.getCompoundTag("EiraIRC");
				tagCompound.setString("Alias", args[2]);
				persistentTag.setTag("EiraIRC", tagCompound);
				entityPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
				sender.sendChatToPlayer(sender.translateString(Globals.MOD_ID + ":irc.aliasSet", args[1], args[2]));
				EiraIRC.instance.getEventHandler().onPlayerNickChange(entityPlayer.username, args[2]);
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
