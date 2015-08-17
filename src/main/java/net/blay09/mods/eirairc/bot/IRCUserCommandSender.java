// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

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
	private final String outputFilter;
	
	public IRCUserCommandSender(IRCChannel channel, IRCUser user, boolean broadcastResult, boolean opEnabled, String outputFilter) {
		this.channel = channel;
		this.user = user;
		this.broadcastResult = broadcastResult;
		this.opEnabled = opEnabled;
		this.outputFilter = outputFilter;
	}
	
	@Override
	public String getCommandSenderName() {
		return "[EiraIRC] " + user.getName();
	}

	@Override
	public IChatComponent func_145748_c_() { // getFormattedCommandSenderName
		return new ChatComponentText(this.getCommandSenderName());
	}

	@Override
	public void addChatMessage(IChatComponent chatComponent) {
		String message = chatComponent.getUnformattedText();
		if(outputFilter.isEmpty() || message.matches(outputFilter)) {
			if(broadcastResult && channel != null) {
				channel.message(message);
			} else {
				user.notice(message);
			}
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(int level, String var2) {
		return opEnabled;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() { // getCommandSenderPosition
		return new ChunkCoordinates(0, 0, 0);
	}

	@Override
	public World getEntityWorld() {
		return MinecraftServer.getServer().getEntityWorld();
	}

}
