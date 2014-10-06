// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.blay09.mods.eirairc.api.upload.UploadManager;
import net.blay09.mods.eirairc.client.upload.DirectUploadHoster;
import net.blay09.mods.eirairc.client.upload.ImgurHoster;
import net.blay09.mods.eirairc.command.base.CommandIRC;
import net.blay09.mods.eirairc.command.base.CommandServIRC;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.command.base.IgnoreCommand;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.handler.IRCConnectionHandler;
import net.blay09.mods.eirairc.handler.IRCEventHandler;
import net.blay09.mods.eirairc.handler.MCEventHandler;
import net.blay09.mods.eirairc.net.EiraNetHandler;
import net.blay09.mods.eirairc.net.PacketHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Localization;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;

@Mod(modid = EiraIRC.MOD_ID, acceptableRemoteVersions="*", guiFactory = "net.blay09.mods.eirairc.client.gui.EiraIRCGuiFactory")
public class EiraIRC {

	public static final String MOD_ID = "eirairc";
	
	@Instance(MOD_ID)
	public static EiraIRC instance;
	
	@SidedProxy(serverSide = "net.blay09.mods.eirairc.CommonProxy", clientSide = "net.blay09.mods.eirairc.client.ClientProxy")
	public static CommonProxy proxy;

	private ConnectionManager connectionManager;
	private ChatSessionHandler chatSessionHandler;
	private EiraNetHandler netHandler;
	private IRCEventHandler ircEventHandler;
	private IRCConnectionHandler ircConnectionHandler;
	private MCEventHandler mcEventHandler;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		UploadManager.registerUploadHoster(new DirectUploadHoster());
		UploadManager.registerUploadHoster(new ImgurHoster());

		ConfigurationHandler.load(event.getModConfigurationDirectory());

		FMLInterModComms.sendRuntimeMessage(this, "VersionChecker", "addVersionCheck", Globals.UPDATE_URL);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		connectionManager = new ConnectionManager();
		chatSessionHandler = new ChatSessionHandler();
		netHandler = new EiraNetHandler();

		ircEventHandler = new IRCEventHandler();
		ircConnectionHandler = new IRCConnectionHandler();
		mcEventHandler = new MCEventHandler();

		proxy.setupClient();

		FMLCommonHandler.instance().bus().register(this);
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
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		registerCommands((CommandHandler) event.getServer().getCommandManager(), true);
		
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			connectionManager.startIRC();
		}
	}
	
	@EventHandler
	public void serverStop(FMLServerStoppingEvent event) {
		if(!MinecraftServer.getServer().isSinglePlayer()) {
			connectionManager.stopIRC();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.modID.equals(Globals.MOD_ID)) {
			if(event.configID.equals("global")) {
				ConfigurationHandler.lightReload();

				if (SharedGlobalConfig.thisConfig.hasChanged()) {
					SharedGlobalConfig.thisConfig.save();
				}
				if (ClientGlobalConfig.thisConfig.hasChanged()) {
					ClientGlobalConfig.thisConfig.save();
				}
			} else if(event.configID.startsWith("server:")) {
				ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(event.configID.substring(7));
				serverConfig.getTheme().pushDummyConfig();
				ConfigurationHandler.saveServers();
			}
		}
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

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}
}
