package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.irc.IRCUser;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class IRCUserCommandSender implements ICommandSender {

	private final IIRCChannel channel;
	private final IIRCUser user;
	private final boolean broadcastResult;
	private final boolean opEnabled;
	
	public IRCUserCommandSender(IIRCChannel channel, IIRCUser user, boolean broadcastResult, boolean opEnabled) {
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
	public boolean canCommandSenderUseCommand(int level, String var2) {
		return opEnabled;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(0, 0, 0);
	}

	@Override
	public World getEntityWorld() {
		return MinecraftServer.getServer().getEntityWorld();
	}

	@Override
	public void sendChatToPlayer(ChatMessageComponent chatComponent) {
		if(broadcastResult) {
			channel.message(chatComponent.toString());
		} else {
			user.notice(chatComponent.toString());
		}
	}

}
