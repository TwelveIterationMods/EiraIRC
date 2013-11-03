// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import blay09.mods.eirairc.command.CommandConnect;
import blay09.mods.eirairc.command.CommandDisconnect;
import blay09.mods.eirairc.command.CommandIRC;
import blay09.mods.eirairc.command.CommandJoin;
import blay09.mods.eirairc.command.CommandNick;
import blay09.mods.eirairc.command.CommandPart;
import blay09.mods.eirairc.command.CommandServIRC;
import blay09.mods.eirairc.command.CommandWho;
import blay09.mods.eirairc.config.ConfigurationHandler;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.Globals;
import blay09.mods.eirairc.config.Localization;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IRCChannel;
import blay09.mods.eirairc.irc.IRCConnection;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
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
	
	@SidedProxy(serverSide = "blay09.mods.eirairc.CommonProxy", clientSide = "blay09.mods.eirairc.client.ClientProxy")
	public static CommonProxy proxy;
	
	private IRCEventHandler eventHandler;
	private Map<String, IRCConnection> connections;
	private boolean ircRunning;
	private EnumChatTarget chatTarget = EnumChatTarget.All;
	private final List<String> validTargetChannels = new ArrayList<String>();
	private final List<String> privateTargets = new ArrayList<String>();
	private int targetChannelIndex;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigurationHandler.load(event.getSuggestedConfigurationFile());
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerKeybindings();
		
		eventHandler = new IRCEventHandler();
		GameRegistry.registerPlayerTracker(eventHandler);
		NetworkRegistry.instance().registerConnectionHandler(eventHandler);
		MinecraftForge.EVENT_BUS.register(eventHandler);
		
		Localization.init();
	}
	
	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent event) {
		connections = new HashMap<String, IRCConnection>();
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandServIRC());
		event.registerServerCommand(new CommandIRC());
		if(GlobalConfig.registerShortCommands) {
			event.registerServerCommand(new CommandJoin());
			event.registerServerCommand(new CommandPart());
			event.registerServerCommand(new CommandConnect());
			event.registerServerCommand(new CommandDisconnect());
			event.registerServerCommand(new CommandNick());
			event.registerServerCommand(new CommandWho());
		}
		
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			startIRC();
		}
	}
	
	@EventHandler
	public void serverStop(FMLServerStoppingEvent event) {
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			stopIRC();
		}
	}
	
	public void startIRC() {
		for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
			if(serverConfig.isAutoConnect()) {
				Utils.connectTo(serverConfig);
			}
		}
		ircRunning = true;
	}
	
	public void stopIRC() {
		for(IRCConnection connection : connections.values()) {
			connection.disconnect(Utils.getQuitMessage(connection));
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
		Iterator<IRCConnection> it = connections.values().iterator();
		if(it.hasNext()) {
			return it.next();
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
		return eventHandler;
	}

	public String getTargetChannel() {
		if(chatTarget == EnumChatTarget.ChannelOnly) {
			return validTargetChannels.get(targetChannelIndex);
		}
		return null;
	}
	
	public void addPrivateTarget(IRCConnection connection, String privateTarget) {
		privateTargets.add(connection.getHost() + ":" + privateTarget);
	}
	
	public void setChatTarget(EnumChatTarget chatTarget) {
		if(chatTarget == EnumChatTarget.ChannelOnly) {
			if(chatTarget != this.chatTarget) {
				targetChannelIndex = -1;
			}
			validTargetChannels.clear();
			for(IRCConnection connection : connections.values()) {
				ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
				for(IRCChannel channel : connection.getChannels()) {
					validTargetChannels.add(connection.getHost() + ":" + channel.getName());
				}
			}
			for(String pchannel : privateTargets) {
				validTargetChannels.add(pchannel);
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
