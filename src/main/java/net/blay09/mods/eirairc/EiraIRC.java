// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.event.*;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.command.base.CommandIRC;
import net.blay09.mods.eirairc.command.base.CommandServIRC;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.command.base.IgnoreCommand;
import net.blay09.mods.eirairc.config2.ServerConfig;
import net.blay09.mods.eirairc.config2.SharedGlobalConfig;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.handler.IRCConnectionHandler;
import net.blay09.mods.eirairc.handler.IRCEventHandler;
import net.blay09.mods.eirairc.handler.MCEventHandler;
import net.blay09.mods.eirairc.net.EiraNetHandler;
import net.blay09.mods.eirairc.net.PacketHandler;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
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

@Mod(modid = EiraIRC.MOD_ID, acceptableRemoteVersions="*")
public class EiraIRC {

	public static final String MOD_ID = "eirairc";
	
	@Instance(MOD_ID)
	public static EiraIRC instance;
	
	@SidedProxy(serverSide = "net.blay09.mods.eirairc.CommonProxy", clientSide = "net.blay09.mods.eirairc.client.ClientProxy")
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
		ConfigurationHandler.loadServices(new File(event.getModConfigurationDirectory(), "eirairc"));
		ConfigurationHandler.load(event.getSuggestedConfigurationFile());
		ConfigurationHandler.loadDisplayFormats(new File(event.getModConfigurationDirectory(), "eirairc/formats"));
		ConfigurationHandler.loadBotProfiles(new File(event.getModConfigurationDirectory(), "eirairc/bots"));

		FMLInterModComms.sendRuntimeMessage(this, "VersionChecker", "addVersionCheck", Globals.UPDATE_URL);
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
		PacketHandler.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		connections = new HashMap<String, IRCConnection>();
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
		List<IRCConnection> dcList = new ArrayList<IRCConnection>();
		for(IRCConnection connection : connections.values()) {
			dcList.add(connection);
		}
		for(int i = 0; i < dcList.size(); i++) {
			dcList.get(i).disconnect(ConfigHelper.getQuitMessage(dcList.get(i)));
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
		connections.put(connection.getIdentifier(), connection);
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

	public IRCConnection getConnection(String identifier) {
		return connections.get(identifier);
	}
	
	public void removeConnection(IRCConnection connection) {
		connections.remove(connection.getHost());
	}

	public boolean isConnectedTo(String identifier) {
		return connections.containsKey(identifier);
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
		if(SharedGlobalConfig.registerShortCommands) {
			IRCCommandHandler.registerQuickCommands(handler);
		}
	}
}
