// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import blay09.mods.irc.command.CommandIRC;
import blay09.mods.irc.command.CommandServIRC;
import blay09.mods.irc.config.ConfigurationHandler;
import blay09.mods.irc.config.Globals;
import blay09.mods.irc.config.Localization;
import blay09.mods.irc.config.ServerConfig;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Globals.MOD_ID, name = Globals.MOD_NAME, version = Globals.MOD_VERSION)
public class EiraIRC {

	@Instance(Globals.MOD_ID)
	public static EiraIRC instance;
	
	@SidedProxy(serverSide = "blay09.mods.irc.CommonProxy", clientSide = "blay09.mods.irc.client.ClientProxy")
	public static CommonProxy proxy;
	
	private IRCEventHandler eventListener;
	private Map<String, IRCConnection> connections;
	private boolean ircRunning;
	private EnumChatTarget chatTarget = EnumChatTarget.All;
	private final List<String> validTargetChannels = new ArrayList<String>();
	private int targetChannelIndex;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		ConfigurationHandler.load(event.getSuggestedConfigurationFile());
	}
	
	@Init
	public void load(FMLInitializationEvent event) {
		proxy.registerKeybindings();
		
		eventListener = new IRCEventHandler();
		GameRegistry.registerPlayerTracker(eventListener);
		NetworkRegistry.instance().registerConnectionHandler(eventListener);
		MinecraftForge.EVENT_BUS.register(eventListener);
		
		Localization.init();
	}
	
	@PostInit
	public void modsLoaded(FMLPostInitializationEvent event) {
		connections = new HashMap<String, IRCConnection>();
	}
	
	@ServerStarting
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandServIRC());
		event.registerServerCommand(new CommandIRC());
		
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			startIRC(false);
		}
	}
	
	@ServerStopping
	public void serverStop(FMLServerStoppingEvent event) {
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			stopIRC();
		}
	}
	
	public void startIRC(boolean clientSide) {
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			IRCConnection connection = new IRCConnection(serverConfig.host, clientSide);
			if(connection.getConfig().autoConnect && connection.connect()) {
				addConnection(connection);
			}
		}
		ircRunning = true;
	}
	
	public void stopIRC() {
		for(IRCConnection connection : connections.values()) {
			connection.disconnect();
		}
		connections.clear();
		ircRunning = false;
	}
	
	public boolean isIRCRunning() {
		return ircRunning;
	}
	
	public Collection<IRCConnection> getConnections() {
		return connections.values();
	}
	
	public void addConnection(IRCConnection connection) {
		connections.put(connection.getHost(), connection);
	}

	public int getConnectionCount() {
		return connections.size();
	}
	
	public IRCConnection getDefaultConnection() {
		for(IRCConnection connection : connections.values()) {
			return connection;
		}
		return null;
	}

	public IRCConnection getConnection(String host) {
		return connections.get(host);
	}
	
	public void removeConnection(IRCConnection connection) {
		connections.remove(connection.getHost());
	}

	public boolean isConnectedTo(String host) {
		return connections.containsKey(host);
	}

	public void clearConnections() {
		connections.clear();
	}
	
	public IRCEventHandler getEventHandler() {
		return eventListener;
	}

	public String getTargetChannel() {
		if(chatTarget == EnumChatTarget.ChannelOnly) {
			return validTargetChannels.get(targetChannelIndex);
		}
		return null;
	}
	
	public void setChatTarget(EnumChatTarget chatTarget) {
		if(chatTarget == EnumChatTarget.ChannelOnly) {
			if(chatTarget != this.chatTarget) {
				targetChannelIndex = -1;
			}
			validTargetChannels.clear();
			for(IRCConnection connection : connections.values()) {
				for(String channel : connection.getConfig().channels) {
					if(connection.getConfig().hasChannelFlag(channel, 'w')) {
						validTargetChannels.add(connection.getHost() + ":" + channel);
					}
				}
				for(String pchannel : connection.getPrivateChannels()) {
					validTargetChannels.add(connection.getHost() + ":" + pchannel);
				}
			}
			if(validTargetChannels.isEmpty()) {
				chatTarget = EnumChatTarget.IRCOnly;
			} else {
				targetChannelIndex++;
				if(targetChannelIndex >= validTargetChannels.size()) {
					targetChannelIndex = 0;
				}
			}
		}
		this.chatTarget = chatTarget;
	}
	
	public EnumChatTarget getChatTarget() {
		return chatTarget;
	}
}
