package net.blay09.mods.eirairc.bot;

import java.util.HashMap;
import java.util.Map;

import net.blay09.mods.eirairc.api.IBot;
import net.blay09.mods.eirairc.api.IBotCommand;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.config.BotProfile;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class EiraIRCBot implements IBot {

	private Map<String, IBotCommand> botCommands = new HashMap<String, IBotCommand>();

	private final IRCConnection connection;
	private final BotProfile profile;
	private final StringBuffer logBuffer = new StringBuffer();
	
	public EiraIRCBot(IRCConnection connection, BotProfile profile) {
		this.connection = connection;
		this.profile = profile;
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
	public boolean getBoolean(String key, boolean defaultVal) {
		return profile.getBoolean(key, defaultVal);
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
	
}
