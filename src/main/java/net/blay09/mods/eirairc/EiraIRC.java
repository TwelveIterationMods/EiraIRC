// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.command.base.CommandIRC;
import net.blay09.mods.eirairc.command.base.CommandServIRC;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.command.base.IgnoreCommand;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.handler.IRCConnectionHandler;
import net.blay09.mods.eirairc.handler.IRCEventHandler;
import net.blay09.mods.eirairc.handler.MCEventHandler;
import net.blay09.mods.eirairc.net.EiraNetHandler;
import net.blay09.mods.eirairc.net.PacketPipeline;
import net.blay09.mods.eirairc.net.packet.PacketHello;
import net.blay09.mods.eirairc.net.packet.PacketNotification;
import net.blay09.mods.eirairc.net.packet.PacketRecLiveState;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Localization;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = EiraIRC.MOD_ID, version = "${version}")
public class EiraIRC {

	public static final String MOD_ID = "eirairc";
	
	@Instance(MOD_ID)
	public static EiraIRC instance;
	
	@SidedProxy(serverSide = "net.blay09.mods.eirairc.CommonProxy", clientSide = "net.blay09.mods.eirairc.client.ClientProxy")
	public static CommonProxy proxy;
	
	public PacketPipeline packetPipeline;
	
	private IRCEventHandler ircEventHandler;
	private IRCConnectionHandler ircConnectionHandler;
	private MCEventHandler mcEventHandler;
	private ChatSessionHandler chatSessionHandler;
	private EiraNetHandler netHandler;
	private Map<String, IIRCConnection> connections;
	private boolean ircRunning;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigurationHandler.load(event.getSuggestedConfigurationFile());
		ConfigurationHandler.loadBotProfiles(new File(event.getModConfigurationDirectory(), "eirairc/bots"));
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		chatSessionHandler = new ChatSessionHandler();
		ircEventHandler = new IRCEventHandler();
		ircConnectionHandler = new IRCConnectionHandler();
		mcEventHandler = new MCEventHandler();
		netHandler = new EiraNetHandler();
		proxy.setupClient();
		
		FMLCommonHandler.instance().bus().register(mcEventHandler);
		MinecraftForge.EVENT_BUS.register(mcEventHandler);
		MinecraftForge.EVENT_BUS.register(ircEventHandler);
		MinecraftForge.EVENT_BUS.register(ircConnectionHandler);
		FMLCommonHandler.instance().bus().register(netHandler);
		
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
		connections = new HashMap<String, IIRCConnection>();
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		registerCommands((CommandHandler) event.getServer().getCommandManager(), true);
		
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
		for(IIRCConnection connection : connections.values()) {
			connection.disconnect(ConfigHelper.getQuitMessage(connection));
		}
		connections.clear();
		ircRunning = false;
	}
	
	public boolean isIRCRunning() {
		return ircRunning;
	}
	
	public Collection<IIRCConnection> getConnections() {
		return connections.values();
	}
	
	public void addConnection(IIRCConnection connection) {
		connections.put(connection.getIdentifier(), connection);
	}

	public int getConnectionCount() {
		return connections.size();
	}
	
	public IIRCConnection getDefaultConnection() {
		Iterator<IIRCConnection> it = connections.values().iterator();
		if(it.hasNext()) {
			return it.next();
		}
		return null;
	}

	public IIRCConnection getConnection(String host) {
		return connections.get(host);
	}
	
	public void removeConnection(IIRCConnection connection) {
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

	public EiraNetHandler getNetHandler() {
		return netHandler;
	}
	
	public void registerCommands(CommandHandler handler, boolean serverSide) {
		if(serverSide) {
			handler.registerCommand(new CommandServIRC());
			handler.registerCommand(new IgnoreCommand("irc"));
		} else {
			handler.registerCommand(new CommandIRC());
		}
		IRCCommandHandler.registerCommands();
		if(GlobalConfig.registerShortCommands) {
			IRCCommandHandler.registerQuickCommands(handler);
		}
	}
}
