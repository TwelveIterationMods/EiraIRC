// Copyright (c) 2015 Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.client.gui.chat;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

@SideOnly(Side.CLIENT)
public class GuiChatExtended extends GuiChat implements GuiYesNoCallback {

	public static final int COLOR_BACKGROUND = Integer.MIN_VALUE;
	public static final int SHOW_HELP_TIME = 100;

	private final List<String> foundIRCNames = new ArrayList<String>();
	private ChatSessionHandler chatSession;
	private String defaultInputText;

	private GuiButton btnOptions;
	private URL clickedURL;
	private IRCContext ircNamesFound;
	private int autoCompleteIndex;
	private int showHelpTime;

	public GuiChatExtended() {
		this("");
	}
	
	public GuiChatExtended(String defaultInputText) {
		chatSession = EiraIRC.instance.getChatSessionHandler();
		this.defaultInputText = defaultInputText;
	}

	@Override
	public void initGui() {
		super.initGui();
		inputField.setText(defaultInputText);
		String s = Utils.getLocalizedMessage("irc.gui.options");
		int bw = fontRendererObj.getStringWidth(s) + 20;
		btnOptions = new GuiButton(0, this.width - bw, 0, bw, 20, s);
		buttonList.add(btnOptions);

		showHelpTime = SHOW_HELP_TIME;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

		if(ClientGlobalConfig.autoResetChat) {
			if(SharedGlobalConfig.defaultChat.equals("Minecraft")) {
				EiraIRC.instance.getChatSessionHandler().setChatTarget(null);
			} else {
				IRCContext chatTarget = EiraIRCAPI.parseContext(null, SharedGlobalConfig.defaultChat, IRCContext.ContextType.IRCChannel);
				if(chatTarget.getContextType() != IRCContext.ContextType.Error) {
					EiraIRC.instance.getChatSessionHandler().setChatTarget(chatTarget);
				} else {
					EiraIRC.instance.getChatSessionHandler().setChatTarget(null);
				}
			}
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if(button == btnOptions) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCMenu());
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(id == 1) {
			if(result) {
				Utils.openWebpage(clickedURL);
				clickedURL = null;
			}
			mc.displayGuiScreen(this);
		} else {
			super.confirmClicked(result, id);
		}
	}

	@Override
	protected void keyTyped(char unicode, int keyCode) {
		if(keyCode == 28 || keyCode == 156) {
			String s = inputField.getText().trim();
			if(s.length() > 0) {
				if(!FMLCommonHandler.instance().bus().post(new ClientChatEvent(s))) {
					if(s.charAt(0) != '/' || ClientCommandHandler.instance.executeCommand(mc.thePlayer, s) != 1) {
						this.mc.thePlayer.sendChatMessage(s);
					}
				}
				mc.ingameGUI.getChatGUI().addToSentMessages(s);
			}
			mc.displayGuiScreen(null);
			return;
		}
		super.keyTyped(unicode, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if(button == 0 && mc.gameSettings.chatLinks) {
			IChatComponent clickedComponent = mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
			if(clickedComponent != null) {
				ClickEvent clickEvent = clickedComponent.getChatStyle().getChatClickEvent();
				if(clickEvent != null) {
					// TODO try to replace eirairc:// by EiraClickEvent
					if(clickEvent.getValue().startsWith("eirairc://")) {
						String[] params = clickEvent.getValue().substring(10).split(";");
						if(params.length > 0) {
							if(params[0].equals("screenshot")) {
								try {
									if(ClientGlobalConfig.imageLinkPreview && params[2].length() > 0) {
										mc.displayGuiScreen(new GuiImagePreview(new URL(params[2]), new URL(params[1])));
									} else {
										if(mc.gameSettings.chatLinksPrompt) {
											clickedURL = new URL(params[2]);
											mc.displayGuiScreen(new GuiConfirmOpenLink(this, params[1], 0, false));
										} else {
											Utils.openWebpage(params[1]);
										}
									}
								} catch (MalformedURLException e) {
									e.printStackTrace();
								}
							}
						}
						return;
					} else {
						// If this is an image link and imageLinkPreview is enabled, open the preview GUI. Otherwise, leave it to the super method.
						if(ClientGlobalConfig.imageLinkPreview && clickEvent.getValue().endsWith(".png") || clickEvent.getValue().endsWith(".jpg")) {
							try {
								mc.displayGuiScreen(new GuiImagePreview(new URL(clickEvent.getValue()), null));
								return;
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void drawScreen(int i, int j, float k) {
		boolean terminalStyleInput = ClientGlobalConfig.terminalStyleInput;
		String terminalChannel = chatSession.getChatTarget() != null ? (chatSession.getChatTarget().getName() + ": ") : null;
		if(terminalStyleInput && terminalChannel != null) {
			int terminalChannelWidth = fontRendererObj.getStringWidth(terminalChannel);
			inputField.xPosition = 4 + terminalChannelWidth;
			inputField.width = this.width - 4 - terminalChannelWidth;
		} else {
			inputField.xPosition = 4;
			inputField.width = this.width - 4;
		}
		super.drawScreen(i, j, k);
		if(terminalStyleInput && terminalChannel != null) {
			fontRendererObj.drawString(terminalChannel, 4, inputField.yPosition, 14737632);
		}
		if(!ClientGlobalConfig.disableChatToggle && !ClientGlobalConfig.clientBridge) {
			IRCContext target = chatSession.getChatTarget();
			String targetName;
			if(target == null) {
				targetName = "Minecraft";
			} else {
				targetName = target.getName() + " (" + target.getConnection().getHost() + ")";
			}
			String helpText = showHelpTime > 0 ? I19n.format("eirairc:gui.shiftToSwitch", Keyboard.getKeyName(ClientGlobalConfig.keyToggleTarget.getKeyCode())) : "";
			String text = I19n.format("eirairc:gui.chatTarget", targetName);
			int rectWidth = Math.max(200, Math.max(fontRendererObj.getStringWidth(helpText), fontRendererObj.getStringWidth(text)) + 10);
			int rectHeight = showHelpTime > 0 ? fontRendererObj.FONT_HEIGHT * 2 + 12 : fontRendererObj.FONT_HEIGHT + 6;
			drawRect(0, 0, rectWidth, rectHeight, COLOR_BACKGROUND);
			fontRendererObj.drawString(text, 5, 5, Globals.TEXT_COLOR);

			if(showHelpTime > 0) {
				showHelpTime--;
				fontRendererObj.drawString(helpText, 5, 20, Globals.TEXT_COLOR);
			}
		}
	}

	@Override
	public void autocompletePlayerNames() {
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			return;
		}
		if(chatSession.getChatTarget() == null) {
			super.autocompletePlayerNames();
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
					return;
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
				mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(sb.toString()), 1);
			}
			if(!foundIRCNames.isEmpty()) {
				inputField.writeText(EnumChatFormatting.getTextWithoutFormattingCodes(foundIRCNames.get(autoCompleteIndex++)));
			}
		}
	}
}
