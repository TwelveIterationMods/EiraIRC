// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import java.util.HashMap;
import java.util.Map;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCContext;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IBotProfile;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.config.BotProfile;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class EiraIRCBot implements IIRCBot {

	private final IRCConnection connection;
	private final BotProfile mainProfile;
	private final Map<String, BotProfile> profiles = new HashMap<String, BotProfile>();
	private final Map<String, IBotCommand> botCommands = new HashMap<String, IBotCommand>();
	private final StringBuffer logBuffer = new StringBuffer();
	
	public EiraIRCBot(IRCConnection connection, BotProfile defaultProfile) {
		this.connection = connection;
		this.mainProfile = defaultProfile;
	}
	
	public void setProfile(String channelName, BotProfile profile) {
		profiles.put(channelName.toLowerCase(), profile);
	}
	
	public BotProfile getProfile(String channelName) {
		BotProfile profile = profiles.get(channelName.toLowerCase());
		if(profile == null) {
			return mainProfile;
		}
		return profile;
	}
	
	@Override
	public IBotProfile getProfile(IIRCContext channel) {
		return getProfile(channel.getName());
	}
	
	@Override
	public IBotProfile getMainProfile() {
		return mainProfile;
	}
	
	@Override
	public String getCommandSenderName() {
		return "EiraIRC Bot (" + connection.getHost() + ")";
	}

	@Override
	public IChatComponent func_145748_c_() {
		return new ChatComponentText(this.getCommandSenderName());
	}

	@Override
	public void addChatMessage(IChatComponent chatComponent) {
		logBuffer.append(chatComponent.getUnformattedText());
	}

	@Override
	public boolean canCommandSenderUseCommand(int level, String commandName) {
		IBotCommand command = botCommands.get(commandName);
		if(command instanceof BotCommandCustom) {
			return ((BotCommandCustom) command).runAsOp();
		}
		return true;
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
	public IIRCConnection getConnection() {
		return connection;
	}

	@Override
	public void resetLog() {
		logBuffer.setLength(0);
	}

	@Override
	public String getLogContents() {
		return logBuffer.toString();
	}

	@Override
	public boolean processCommand(IIRCChannel channel, IIRCUser sender, String message) {
		String[] args = message.split(" ");
		IBotCommand botCommand = botCommands.get(args[0]);
		if(botCommand == null) {
			return false;
		}
		if(channel != null && !botCommand.isChannelCommand()) {
			return false;
		}
		String[] shiftedArgs = Utils.shiftArgs(args, 1);
		botCommand.processCommand(this, channel, sender, shiftedArgs);
		return true;
	}

	@Override
	public boolean getBoolean(IIRCContext context, String key, boolean defaultVal) {
		return mainProfile.getBoolean(key, defaultVal) && context != null ? getProfile(context).getBoolean(key, defaultVal) : true;
	}

	@Override
	public boolean isMuted(IIRCContext context) {
		return mainProfile.isMuted() || context != null ? getProfile(context).isMuted() : false;
	}

	@Override
	public boolean isReadOnly(IIRCContext context) {
		return mainProfile.isReadOnly() || context != null ? getProfile(context).isReadOnly() : false;
	}

	@Override
	public boolean isServerSide() {
		return false;
	}

}
