// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client;

import net.blay09.mods.eirairc.CommonProxy;
import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.addon.Compatibility;
import net.blay09.mods.eirairc.addon.DirectUploadHoster;
import net.blay09.mods.eirairc.addon.ImgurHoster;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.config.IConfigManager;
import net.blay09.mods.eirairc.api.event.IRCChannelMessageEvent;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.client.gui.*;
import net.blay09.mods.eirairc.client.gui.chat.GuiChatExtended;
import net.blay09.mods.eirairc.client.gui.chat.GuiSleepExtended;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayNotification;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshots;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.config.*;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.util.ChatComponentBuilder;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClientProxy extends CommonProxy {

    private static final KeyBinding[] keyBindings = new KeyBinding[]{
            ClientGlobalConfig.keyScreenshotShare,
            ClientGlobalConfig.keyOpenScreenshots,
            ClientGlobalConfig.keyToggleTarget,
            ClientGlobalConfig.keyOpenMenu
    };

    private ChatSessionHandler chatSession;
    private OverlayNotification notificationGUI;
    private int screenshotCheck;
    private int keyChat;
    private int keyCommand;
    private int openWelcomeScreen;
    private long lastToggleTarget;
    private boolean wasToggleTargetDown;
    private boolean isFirstMainMenu = true;

    @Override
    public void init() {
        this.chatSession = EiraIRC.instance.getChatSessionHandler();
        notificationGUI = new OverlayNotification();

        Minecraft mc = Minecraft.getMinecraft();

        keyChat = mc.gameSettings.keyBindChat.getKeyCode();
        keyCommand = mc.gameSettings.keyBindCommand.getKeyCode();

        ScreenshotManager.create();

        MinecraftForge.EVENT_BUS.register(this);

        for (KeyBinding keyBinding : keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }

        // Dirty hack to stop toggle target overshadowing player list key when they share the same key code (they do by default)
        KeyBinding keyBindPlayerList = mc.gameSettings.keyBindPlayerList;
        if (ClientGlobalConfig.keyToggleTarget.getKeyCode() == keyBindPlayerList.getKeyCode()) {
            mc.gameSettings.keyBindPlayerList = new KeyBinding(keyBindPlayerList.getKeyDescription(), keyBindPlayerList.getKeyCodeDefault(), keyBindPlayerList.getKeyCategory());
            mc.gameSettings.keyBindPlayerList.setKeyCode(keyBindPlayerList.getKeyCode());
        }

        EiraIRC.instance.registerCommands(ClientCommandHandler.instance, false);
        if (ClientGlobalConfig.registerShortCommands.get()) {
            IRCCommandHandler.registerQuickCommands(ClientCommandHandler.instance);
        }

        EiraGui.init(mc.getResourceManager());

        try {
            ConfigurationHandler.loadSuggestedChannels(mc.getResourceManager());
        } catch (IOException ignored) {
        }
    }

    @Override
    public void postInit() {
        super.postInit();

        EiraIRCAPI.registerUploadHoster(new DirectUploadHoster());
        EiraIRCAPI.registerUploadHoster(new ImgurHoster());
    }

    @Override
    public void publishNotification(NotificationType type, String text) {
        NotificationStyle config = NotificationStyle.None;
        switch (type) {
            case FriendJoined:
                config = ClientGlobalConfig.ntfyFriendJoined.get();
                break;
            case PlayerMentioned:
                config = ClientGlobalConfig.ntfyNameMentioned.get();
                break;
            case PrivateMessage:
                config = ClientGlobalConfig.ntfyPrivateMessage.get();
                break;
            default:
        }
        if (config != NotificationStyle.None && config != NotificationStyle.SoundOnly) {
            notificationGUI.showNotification(type, text);
        }
        if (config == NotificationStyle.TextAndSound || config == NotificationStyle.SoundOnly) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation(ClientGlobalConfig.notificationSound.get()), ClientGlobalConfig.notificationSoundPitch.get()));
        }
    }

    @Override
    public String getUsername() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    @Override
    public void loadConfig(File configDir, boolean reloadFile) {
        super.loadConfig(configDir, reloadFile);
        ClientGlobalConfig.load(configDir, reloadFile);
    }

    @Override
    public void handleRedirect(ServerConfig serverConfig) {
        TrustedServer server = ConfigurationHandler.getOrCreateTrustedServer(Utils.getServerAddress());
        if (server.isAllowRedirect()) {
            ConnectionManager.redirectTo(serverConfig, server.isRedirectSolo());
        } else {
            Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCRedirect(serverConfig));
        }
    }

    @Override
    public boolean handleConfigCommand(ICommandSender sender, String key, String value) {
        return super.handleConfigCommand(sender, key, value) || ClientGlobalConfig.handleConfigCommand(sender, key, value);
    }

    @Override
    public String handleConfigCommand(ICommandSender sender, String key) {
        String result = super.handleConfigCommand(sender, key);
        if (result == null) {
            return ClientGlobalConfig.handleConfigCommand(sender, key);
        } else {
            return result;
        }
    }

    @Override
    public void addConfigOptionsToList(List<String> list, String option, boolean autoCompleteOption) {
        super.addConfigOptionsToList(list, option, autoCompleteOption);
        ClientGlobalConfig.addOptionsToList(list, option, autoCompleteOption);
    }

    @Override
    public boolean checkClientBridge(IRCChannelMessageEvent event) {
        if (event.sender != null && ClientGlobalConfig.clientBridge.get()) {
            for (NetworkPlayerInfo playerInfo : FMLClientHandler.instance().getClientPlayerEntity().sendQueue.getPlayerInfoMap()) {
                if (event.sender.getName().equalsIgnoreCase(playerInfo.getGameProfile().getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        if (ClientGlobalConfig.thisConfig.hasChanged()) {
            ClientGlobalConfig.thisConfig.save();
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiMainMenu) {
            if (isFirstMainMenu && ClientGlobalConfig.showModpackConfirmation.get() && !LocalConfig.disableModpackConfirmation.get()) {
                event.gui = new GuiModpackConfirmation();
                isFirstMainMenu = false;
            }
        }
    }

    @SubscribeEvent
    public void keyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiControls) && ClientGlobalConfig.keyScreenshotShare.getKeyCode() != 0 && Keyboard.getEventKey() == ClientGlobalConfig.keyScreenshotShare.getKeyCode()) {
            Screenshot screenshot = ScreenshotManager.getInstance().takeScreenshot();
            if (screenshot != null) {
                ScreenshotManager.getInstance().uploadScreenshot(screenshot, ScreenshotAction.UploadShare);
            }
        }
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            int keyCode = Keyboard.getEventKey();
            if (ClientGlobalConfig.keyOpenMenu.getKeyCode() != 0 && keyCode == ClientGlobalConfig.keyOpenMenu.getKeyCode()) {
                if (Minecraft.getMinecraft().currentScreen == null) {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCMenu());
                }
            } else if (ClientGlobalConfig.keyOpenScreenshots.getKeyCode() != 0 && keyCode == ClientGlobalConfig.keyOpenScreenshots.getKeyCode()) {
                if (Minecraft.getMinecraft().currentScreen == null) {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshots(null));
                }
            } else if (ClientGlobalConfig.keyScreenshotShare.getKeyCode() != 0 && keyCode == ClientGlobalConfig.keyScreenshotShare.getKeyCode()) {
                Screenshot screenshot = ScreenshotManager.getInstance().takeScreenshot();
                if (screenshot != null) {
                    ScreenshotManager.getInstance().uploadScreenshot(screenshot, ScreenshotAction.UploadShare);
                }
            } else {
                if (!ClientGlobalConfig.chatNoOverride.get() && !Compatibility.isTabbyChat2Installed()) {
                    GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
                    if (currentScreen == null || currentScreen.getClass() == GuiChat.class) {
                        if (Keyboard.getEventKey() == keyChat) {
                            Minecraft.getMinecraft().gameSettings.keyBindChat.isPressed();
                            Minecraft.getMinecraft().displayGuiScreen(new GuiChatExtended());
                        } else if (Keyboard.getEventKey() == keyCommand) {
                            Minecraft.getMinecraft().gameSettings.keyBindCommand.isPressed();
                            Minecraft.getMinecraft().displayGuiScreen(new GuiChatExtended("/"));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void worldJoined(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!ConnectionManager.isIRCRunning()) {
            ConnectionManager.startIRC();
        }
        if (ClientGlobalConfig.showWelcomeScreen.get() && !LocalConfig.disableWelcomeScreen.get() && !LocalConfig.disableModpackIRC.get()) {
            openWelcomeScreen = 20;
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        ConnectionManager.tickConnections();

        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        if (currentScreen == null && openWelcomeScreen > 0) {
            openWelcomeScreen--;
            if (openWelcomeScreen <= 0) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiWelcome());
            }
        }

        if (currentScreen instanceof GuiChat && !ClientGlobalConfig.disableChatToggle.get() && !ClientGlobalConfig.clientBridge.get() && !Compatibility.isTabbyChat2Installed()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(ClientGlobalConfig.keyToggleTarget.getKeyCode())) {
                if (!wasToggleTargetDown) {
                    boolean users = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
                    IRCContext newTarget = chatSession.getNextTarget(users);
                    if (!users) {
                        lastToggleTarget = System.currentTimeMillis();
                    }
                    if (!users || newTarget != null) {
                        chatSession.setChatTarget(newTarget);
                        if (ClientGlobalConfig.chatNoOverride.get()) {
                            ChatComponentBuilder ccb = new ChatComponentBuilder();
                            ccb.text(">> ").lang("eirairc:general.chattingTo", ccb.push().color('6').text(newTarget == null ? "Minecraft" : newTarget.getName()).pop()).text(" <<").send();
                        }
                    }
                    wasToggleTargetDown = true;
                } else {
                    if (System.currentTimeMillis() - lastToggleTarget >= 1000) {
                        chatSession.setChatTarget(null);
                        if (ClientGlobalConfig.chatNoOverride.get()) {
                            ChatComponentBuilder ccb = new ChatComponentBuilder();
                            ccb.text(">> ").lang("eirairc:general.chattingTo", ccb.push().color('6').text("Minecraft").pop()).text(" <<").send();
                        }
                        lastToggleTarget = System.currentTimeMillis();
                    }
                }
            } else {
                wasToggleTargetDown = false;
            }
        }

        if (currentScreen != null && currentScreen.getClass() == GuiSleepMP.class) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiSleepExtended());
        }

        if (Minecraft.getMinecraft().gameSettings.keyBindScreenshot.getKeyCode() > 0 && Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindScreenshot.getKeyCode())) {
            screenshotCheck = 10;
        } else if (screenshotCheck > 0) {
            screenshotCheck--;
            if (screenshotCheck == 0) {
                ScreenshotManager.getInstance().findNewScreenshots(true);
            }
        }

        ScreenshotManager.getInstance().clientTick(event);
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        notificationGUI.updateAndRender(event.renderTickTime);
    }

    @Override
    public IConfigManager getClientGlobalConfig() {
        return ClientGlobalConfig.manager;
    }
}
