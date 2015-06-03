package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class IRCUserCommandSender implements ICommandSender {

	private final IRCChannel channel;
	private final IRCUser user;
	private final boolean broadcastResult;
	private final boolean opEnabled;
	
	public IRCUserCommandSender(IRCChannel channel, IRCUser user, boolean broadcastResult, boolean opEnabled) {
		this.channel = channel;
		this.user = user;
		this.broadcastResult = broadcastResult;
		this.opEnabled = opEnabled;
	}
	
	@Override
	public String getCommandSenderName() {
		return "[EiraIRC] " + user.getName();
	}

	@Override
	public IChatComponent getFormattedCommandSenderName() {
		return new ChatComponentText(this.getCommandSenderName());
	}

	@Override
	public void addChatMessage(IChatComponent chatComponent) {
		if(broadcastResult && channel != null) {
			channel.message(chatComponent.getUnformattedText());
		} else {
			user.notice(chatComponent.getUnformattedText());
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(int level, String var2) {
		return opEnabled;
	}

	@Override
	public ChunkCoordinates getCommandSenderPosition() {
		return new ChunkCoordinates(0, 0, 0);
	}

	@Override
	public World getEntityWorld() {
		return MinecraftServer.getServer().getEntityWorld();
	}

}
