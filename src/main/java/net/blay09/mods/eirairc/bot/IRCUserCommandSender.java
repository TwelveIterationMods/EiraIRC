package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
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
	public String getName() {
		return "[EiraIRC] " + user.getName();
	}

	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getName());
	}

	@Override
	public void addChatMessage(IChatComponent chatComponent) {
		String message = chatComponent.getUnformattedText();
		if(outputFilter.isEmpty() || message.matches(outputFilter)) {
			if (broadcastResult && channel != null) {
				channel.message(chatComponent.getUnformattedText());
			} else {
				user.notice(chatComponent.getUnformattedText());
			}
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
		return opEnabled;
	}

	@Override
	public BlockPos getPosition() {
		return new BlockPos(0, 0, 0);
	}

	@Override
	public Vec3 getPositionVector() {
		return new Vec3(0, 0, 0);
	}

	@Override
	public World getEntityWorld() {
		return MinecraftServer.getServer().getEntityWorld();
	}

	@Override
	public Entity getCommandSenderEntity() {
		return null;
	}

	@Override
	public boolean sendCommandFeedback() {
		return true;
	}

	@Override
	public void setCommandStat(CommandResultStats.Type type, int amount) {}

}