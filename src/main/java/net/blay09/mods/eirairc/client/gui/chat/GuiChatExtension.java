package net.blay09.mods.eirairc.client.gui.chat;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.event.ClientChatEvent;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCMenu;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiImagePreview;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class GuiChatExtension {

    public static final int COLOR_BACKGROUND = Integer.MIN_VALUE;
    public static final int SHOW_HELP_TIME = 100;

    private final GuiChat parentScreen;
    private FontRenderer fontRenderer;

    private final List<String> foundIRCNames = Lists.newArrayList();
    private GuiTextField inputField;
    private ChatSessionHandler chatSession;
    private GuiButton btnOptions;

    private URL clickedURL;
    private IRCContext ircNamesFound;
    private int autoCompleteIndex;
    private int showHelpTime;

    public GuiChatExtension(GuiChat parentScreen, GuiTextField inputField) {
        this.parentScreen = parentScreen;
        this.inputField = inputField;
        chatSession = EiraIRC.instance.getChatSessionHandler();
    }

    public boolean autocompletePlayernames() {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            return false;
        }
        if(chatSession.getChatTarget() == null) {
            return true;
        } else {
            int i = inputField.func_146197_a(-1, inputField.getCursorPosition(), false);
            String searchName = inputField.getText().substring(i).toLowerCase();
            if (ircNamesFound == chatSession.getChatTarget()) {
                inputField.deleteFromCursor(inputField.func_146197_a(-1, inputField.getCursorPosition(), false) - inputField.getCursorPosition());
                if(autoCompleteIndex >= foundIRCNames.size()) {
                    autoCompleteIndex = 0;
                }
            } else {
                foundIRCNames.clear();
                autoCompleteIndex = 0;

                if(chatSession.getChatTarget() instanceof IRCChannel) {
                    for(IRCUser user : ((IRCChannel) chatSession.getChatTarget()).getUserList()) {
                        if(user.getName().toLowerCase().startsWith(searchName.toLowerCase())) {
                            foundIRCNames.add(user.getName());
                        }
                    }
                }

                if(this.foundIRCNames.isEmpty()) {
                    return false;
                }

                ircNamesFound = chatSession.getChatTarget();
                inputField.deleteFromCursor(i - inputField.getCursorPosition());
            }

            if(foundIRCNames.size() > 1) {
                StringBuilder sb = new StringBuilder();
                for(String s : foundIRCNames) {
                    if(sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(s);
                }
                parentScreen.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(sb.toString()), 1);
            }
            if(!foundIRCNames.isEmpty()) {
                inputField.writeText(EnumChatFormatting.getTextWithoutFormattingCodes(foundIRCNames.get(autoCompleteIndex++)));
            }
        }
        return false;
    }

    public void initGui(List buttonList) {
        fontRenderer = parentScreen.mc.fontRendererObj;

        String s = I19n.format("eirairc:gui.options");
        int bw = parentScreen.mc.fontRendererObj.getStringWidth(s) + 20;
        btnOptions = new GuiButton(0, parentScreen.width - bw, 0, bw, 20, s);
        buttonList.add(btnOptions);

        showHelpTime = SHOW_HELP_TIME;
    }

    public void onGuiClosed() {
        if(ClientGlobalConfig.autoResetChat.get()) {
            if(SharedGlobalConfig.defaultChat.get().equals("Minecraft")) {
                chatSession.setChatTarget(null);
            } else {
                IRCContext chatTarget = EiraIRCAPI.parseContext(null, SharedGlobalConfig.defaultChat.get(), IRCContext.ContextType.IRCChannel);
                if(chatTarget.getContextType() != IRCContext.ContextType.Error) {
                    chatSession.setChatTarget(chatTarget);
                } else {
                    chatSession.setChatTarget(null);
                }
            }
        }
    }

    public void actionPerformed(GuiButton button) {
        if(button == btnOptions) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCMenu());
        }
    }

    public boolean confirmClicked(boolean result, int id) {
        if(id == 1) {
            if(result) {
                Utils.openWebpage(clickedURL);
                clickedURL = null;
            }
            parentScreen.mc.displayGuiScreen(parentScreen);
            return false;
        }
        return true;
    }

    public void preRender() {
        boolean terminalStyleInput = ClientGlobalConfig.terminalStyleInput.get();
        String terminalChannel = chatSession.getChatTarget() != null ? (chatSession.getChatTarget().getName() + ": ") : null;
        if(terminalStyleInput && terminalChannel != null) {
            int terminalChannelWidth = parentScreen.mc.fontRendererObj.getStringWidth(terminalChannel);
            inputField.xPosition = 4 + terminalChannelWidth;
            inputField.width = parentScreen.width - 4 - terminalChannelWidth;
        } else {
            inputField.xPosition = 4;
            inputField.width = parentScreen.width - 4;
        }
    }

    public void postRender() {
        boolean terminalStyleInput = ClientGlobalConfig.terminalStyleInput.get();
        String terminalChannel = chatSession.getChatTarget() != null ? (chatSession.getChatTarget().getName() + ": ") : null;
        if(terminalStyleInput && terminalChannel != null) {
            parentScreen.mc.fontRendererObj.drawString(terminalChannel, 4, inputField.yPosition, 14737632);
        }
        if(!ClientGlobalConfig.disableChatToggle.get() && !ClientGlobalConfig.clientBridge.get()) {
            IRCContext target = chatSession.getChatTarget();
            String targetName;
            if(target == null) {
                targetName = "Minecraft";
            } else {
                targetName = target.getName() + " (" + target.getConnection().getHost() + ")";
            }
            String helpText = showHelpTime > 0 ? I19n.format("eirairc:gui.shiftToSwitch", Keyboard.getKeyName(ClientGlobalConfig.keyToggleTarget.getKeyCode())) : "";
            String text = I19n.format("eirairc:gui.chatTarget", targetName);
            int rectWidth = Math.max(200, Math.max(fontRenderer.getStringWidth(helpText), fontRenderer.getStringWidth(text)) + 10);
            int rectHeight = showHelpTime > 0 ? fontRenderer.FONT_HEIGHT * 2 + 12 : fontRenderer.FONT_HEIGHT + 6;
            GuiScreen.drawRect(0, 0, rectWidth, rectHeight, COLOR_BACKGROUND);
            fontRenderer.drawString(text, 5, 5, Globals.TEXT_COLOR);

            if(showHelpTime > 0) {
                showHelpTime--;
                fontRenderer.drawString(helpText, 5, 20, Globals.TEXT_COLOR);
            }
        }
    }

    public boolean keyTyped(char unicode, int keyCode) {
        if(keyCode == 28 || keyCode == 156) {
            String s = inputField.getText().trim();
            if(s.length() > 0) {
                if(!FMLCommonHandler.instance().bus().post(new ClientChatEvent(s))) {
                    if(s.charAt(0) != '/' || ClientCommandHandler.instance.executeCommand(parentScreen.mc.thePlayer, s) != 1) {
                        parentScreen.mc.thePlayer.sendChatMessage(s);
                    }
                }
                parentScreen.mc.ingameGUI.getChatGUI().addToSentMessages(s);
            }
            parentScreen.mc.displayGuiScreen(null);
            return false;
        }
        return true;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if(button == 0 && parentScreen.mc.gameSettings.chatLinks) {
            IChatComponent clickedComponent = parentScreen.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
            if(clickedComponent != null) {
                ClickEvent clickEvent = clickedComponent.getChatStyle().getChatClickEvent();
                if(clickEvent != null) {
                    if(clickEvent.getValue().startsWith("eirairc://")) {
                        String[] params = clickEvent.getValue().substring(10).split(";");
                        if(params.length > 0) {
                            if(params[0].equals("screenshot")) {
                                try {
                                    if(ClientGlobalConfig.imageLinkPreview.get() && params[2].length() > 0) {
                                        parentScreen.mc.displayGuiScreen(new GuiImagePreview(new URL(params[2]), new URL(params[1])));
                                    } else {
                                        if(parentScreen.mc.gameSettings.chatLinksPrompt) {
                                            clickedURL = new URL(params[2]);
                                            parentScreen.mc.displayGuiScreen(new GuiConfirmOpenLink(parentScreen, params[1], 0, false));
                                        } else {
                                            Utils.openWebpage(params[1]);
                                        }
                                    }
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        return false;
                    } else {
                        // If this is an image link and imageLinkPreview is enabled, open the preview GUI. Otherwise, leave it to the super method.
                        if(ClientGlobalConfig.imageLinkPreview.get() && clickEvent.getValue().endsWith(".png") || clickEvent.getValue().endsWith(".jpg")) {
                            try {
                                parentScreen.mc.displayGuiScreen(new GuiImagePreview(new URL(clickEvent.getValue()), null));
                                return false;
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
