package net.blay09.mods.eirairc;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class BotCommandSender implements ICommandSender {

	@Override
	public String getCommandSenderName() {
		return null;
	}

	@Override
	public IChatComponent func_145748_c_() {
		return null;
	}

	@Override
	public void addChatMessage(IChatComponent var1) {
		
	}

	@Override
	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return false;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return null;
	}

	@Override
	public World getEntityWorld() {
		return null;
	}

}
