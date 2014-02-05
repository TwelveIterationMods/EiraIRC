// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.blay09.mods.eirairc.command.CommandConnect;
import net.blay09.mods.eirairc.command.CommandDisconnect;
import net.blay09.mods.eirairc.command.CommandIRC;
import net.blay09.mods.eirairc.command.CommandJoin;
import net.blay09.mods.eirairc.command.CommandNick;
import net.blay09.mods.eirairc.command.CommandPart;
import net.blay09.mods.eirairc.command.CommandServIRC;
import net.blay09.mods.eirairc.command.CommandWho;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.handler.IRCConnectionHandler;
import net.blay09.mods.eirairc.handler.IRCEventHandler;
import net.blay09.mods.eirairc.handler.MCEventHandler;
import net.blay09.mods.eirairc.irc.IIRCConnectionHandler;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.net.EiraNetHandler;
import net.blay09.mods.eirairc.net.PacketPipeline;
import net.blay09.mods.eirairc.net.packet.PacketHello;
import net.blay09.mods.eirairc.net.packet.PacketNotification;
import net.blay09.mods.eirairc.net.packet.PacketRecLiveState;
import net.blay09.mods.eirairc.util.Localization;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = EiraIRC.MOD_ID)
public class EiraIRC {

	public static final String MOD_ID = "eirairc";
	
	@Instance(MOD_ID)
	public static EiraIRC instance;
	
	@SidedProxy(serverSide = "blay09.mods.eirairc.CommonProxy", clientSide = "blay09.mods.eirairc.client.ClientProxy")
	public static CommonProxy proxy;
	
	public PacketPipeline packetPipeline;
	
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
	public void init(FMLInitializationEvent event) {
		chatSessionHandler = new ChatSessionHandler();
		ircEventHandler = new IRCEventHandler();
		ircConnectionHandler = new IRCConnectionHandler();
		mcEventHandler = new MCEventHandler();
		netHandler = new EiraNetHandler();
		proxy.setupClient();
		MinecraftForge.EVENT_BUS.register(mcEventHandler);
		MinecraftForge.EVENT_BUS.register(netHandler);
		
		Localization.init();
		packetPipeline = new PacketPipeline();
		packetPipeline.initialize();
		packetPipeline.registerPacket(PacketHello.class);
		packetPipeline.registerPacket(PacketNotification.class);
		packetPipeline.registerPacket(PacketRecLiveState.class);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		packetPipeline.postInitialize();
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
