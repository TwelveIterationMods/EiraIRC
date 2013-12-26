// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import blay09.mods.eirairc.client.EiraTickHandler;
import blay09.mods.eirairc.command.CommandConnect;
import blay09.mods.eirairc.command.CommandDisconnect;
import blay09.mods.eirairc.command.CommandIRC;
import blay09.mods.eirairc.command.CommandJoin;
import blay09.mods.eirairc.command.CommandNick;
import blay09.mods.eirairc.command.CommandPart;
import blay09.mods.eirairc.command.CommandServIRC;
import blay09.mods.eirairc.command.CommandWho;
import blay09.mods.eirairc.config.GlobalConfig;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.handler.ChatSessionHandler;
import blay09.mods.eirairc.handler.ConfigurationHandler;
import blay09.mods.eirairc.handler.IRCConnectionHandler;
import blay09.mods.eirairc.handler.IRCEventHandler;
import blay09.mods.eirairc.handler.MCEventHandler;
import blay09.mods.eirairc.irc.IIRCConnectionHandler;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.net.EiraNetHandler;
import blay09.mods.eirairc.net.PacketHandler;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Localization;
import blay09.mods.eirairc.util.Utils;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = Globals.MOD_ID, name = Globals.MOD_NAME, version = Globals.MOD_VERSION)
@NetworkMod(channels = { Globals.MOD_ID }, packetHandler = PacketHandler.class)
public class EiraIRC {

	@Instance(Globals.MOD_ID)
	public static EiraIRC instance;
	
	@SidedProxy(serverSide = "blay09.mods.eirairc.CommonProxy", clientSide = "blay09.mods.eirairc.client.ClientProxy")
	public static CommonProxy proxy;
	
	private IRCEventHandler ircEventHandler;
	private IRCConnectionHandler ircConnectionHandler;
	private MCEventHandler mcEventHandler;
	private ChatSessionHandler chatSessionHandler;
	private EiraNetHandler netHandler;
	private Map<String, IRCConnection> connections;
	private boolean ircRunning;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigurationHandler.load(event.getSuggestedConfigurationFile());
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		chatSessionHandler = new ChatSessionHandler();
		ircEventHandler = new IRCEventHandler();
		ircConnectionHandler = new IRCConnectionHandler();
		mcEventHandler = new MCEventHandler();
		netHandler = new EiraNetHandler();
		proxy.setupClient();
		GameRegistry.registerPlayerTracker(mcEventHandler);
		GameRegistry.registerPlayerTracker(netHandler);
		NetworkRegistry.instance().registerConnectionHandler(mcEventHandler);
		MinecraftForge.EVENT_BUS.register(mcEventHandler);
		
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
	
	public IRCEventHandler getIRCEventHandler() {
		return ircEventHandler;
	}
	
	public MCEventHandler getMCEventHandler() {
		return mcEventHandler;
	}
	
	public ChatSessionHandler getChatSessionHandler() {
		return chatSessionHandler;
	}

	public IIRCConnectionHandler getIRCConnectionHandler() {
		return ircConnectionHandler;
	}
	
	public EiraNetHandler getNetHandler() {
		return netHandler;
	}
}
