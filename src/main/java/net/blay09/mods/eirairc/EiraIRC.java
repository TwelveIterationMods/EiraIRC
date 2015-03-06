// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.blay09.mods.eirairc.addon.DirectUploadHoster;
import net.blay09.mods.eirairc.addon.ImgurHoster;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.command.base.CommandIRC;
import net.blay09.mods.eirairc.command.base.CommandServIRC;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.command.base.IgnoreCommand;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.handler.IRCConnectionHandler;
import net.blay09.mods.eirairc.handler.IRCEventHandler;
import net.blay09.mods.eirairc.handler.MCEventHandler;
import net.blay09.mods.eirairc.net.EiraNetHandler;
import net.blay09.mods.eirairc.net.PacketHandler;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;

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

		proxy.init();

		FMLCommonHandler.instance().bus().register(this);
		FMLCommonHandler.instance().bus().register(mcEventHandler);
		MinecraftForge.EVENT_BUS.register(mcEventHandler);
		MinecraftForge.EVENT_BUS.register(ircEventHandler);
		MinecraftForge.EVENT_BUS.register(ircConnectionHandler);
		FMLCommonHandler.instance().bus().register(netHandler);
		
		I19n.init();
		PacketHandler.init();

		EiraIRCAPI.internalSetupAPI(new InternalMethodsImpl());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		event.buildSoftDependProxy("Dynmap", "net.blay09.mods.eirairc.addon.DynmapWebChatAddon");
		EiraIRCAPI.registerUploadHoster(new DirectUploadHoster());
		EiraIRCAPI.registerUploadHoster(new ImgurHoster());

		proxy.postInit();
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
				proxy.saveConfig();
			} else if(event.configID.startsWith("server:")) {
				ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(event.configID.substring(7));
				serverConfig.getTheme().pushDummyConfig();
				serverConfig.getBotSettings().pushDummyConfig();
				serverConfig.getGeneralSettings().pushDummyConfig();
				ConfigurationHandler.saveServers();
			} else if(event.configID.startsWith("channel:")) {
				ChannelConfig channelConfig = ConfigHelper.resolveChannelConfig(event.configID.substring(8));
				channelConfig.getTheme().pushDummyConfig();
				channelConfig.getBotSettings().pushDummyConfig();
				channelConfig.getGeneralSettings().pushDummyConfig();
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
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}
}
