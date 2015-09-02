// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.command.extension;

import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.ThemeColorComponent;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.IRCFormatting;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandColor implements SubCommand {

	private static final String COLOR_NONE = "none";
	
	@Override
	public String getCommandName() {
		return "color";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "eirairc:commands.color.usage";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
		if(!(sender instanceof EntityPlayer)) {
			return true;
		}
		if(!SharedGlobalConfig.enablePlayerColors.get()) {
			Utils.sendLocalizedMessage(sender, "commands.color.disabled");
			return true;
		}
		if(args.length < 1) {
			throw new WrongUsageException(I19n.format("eirairc:commands.color.usage"));
		}
		String colorName = args[0].toLowerCase();
		EnumChatFormatting mcOpColor = SharedGlobalConfig.theme.getColor(ThemeColorComponent.mcOpNameColor);
		if(!Utils.isOP(sender) && (SharedGlobalConfig.colorBlacklist.contains(colorName) || (mcOpColor != null && mcOpColor.name().toLowerCase().equals(colorName)))) {
			Utils.sendLocalizedMessage(sender, "commands.color.blackList", colorName);
			return true;
		}
		boolean isNone = colorName.equals(COLOR_NONE);
		if(!isNone && !IRCFormatting.isValidColor(colorName)) {
			Utils.sendLocalizedMessage(sender, "commands.color.invalid", colorName);
			return true;
		}
		EntityPlayer entityPlayer = (EntityPlayer) sender;
		NBTTagCompound persistentTag = entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		NBTTagCompound tagCompound = persistentTag.getCompoundTag(Globals.NBT_EIRAIRC);
		if(isNone) {
			tagCompound.removeTag(Globals.NBT_NAMECOLOR_DEPRECATED);
			tagCompound.removeTag(Globals.NBT_NAMECOLOR);
		} else {
			EnumChatFormatting color = IRCFormatting.getColorFromName(colorName);
			if(color != null) {
				tagCompound.removeTag(Globals.NBT_NAMECOLOR_DEPRECATED);
				tagCompound.setByte(Globals.NBT_NAMECOLOR, (byte) color.ordinal());
			}
		}
		persistentTag.setTag(Globals.NBT_EIRAIRC, tagCompound);
		entityPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
		entityPlayer.refreshDisplayName();
		if(isNone) {
			Utils.sendLocalizedMessage(sender, "commands.color.reset");
		} else {
			Utils.sendLocalizedMessage(sender, "commands.color.set", colorName);
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		list.add(COLOR_NONE);
		IRCFormatting.addValidColorsToList(list);
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
