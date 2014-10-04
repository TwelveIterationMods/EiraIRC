// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.chat;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCMenu;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.client.ClientCommandHandler;

import org.lwjgl.input.Keyboard;

public class GuiEiraChatInput extends GuiScreen {

	private GuiEiraChat parentChat;
	private String defaultInputText;
	private GuiButton btnOptions;
	private GuiTextField txtInput;
	
	private long lastToggleTarget;
	private int sentHistoryCursor = 0;

	public GuiEiraChatInput(GuiEiraChat eiraChat) {
		this(eiraChat, "");
	}
	
	public GuiEiraChatInput(GuiEiraChat parentChat, String defaultInputText) {
		this.parentChat = parentChat;
		this.defaultInputText = defaultInputText;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		
		sentHistoryCursor = parentChat.getSentHistory().size();
		
        buildInputField();
		buildOptionsButton();
	}
	
	private void buildInputField() {
		txtInput = new GuiTextField(this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
		txtInput.setMaxStringLength(100);
        txtInput.setEnableBackgroundDrawing(false);
        txtInput.setFocused(true);
        txtInput.setText(defaultInputText);
        txtInput.setCanLoseFocus(false);
	}
	
	private void buildOptionsButton() {
		String s = Utils.getLocalizedMessage("irc.gui.options");
		int bw = fontRendererObj.getStringWidth(s) + 20;
		btnOptions = new GuiButton(0, this.width - bw, 0, bw, 20, s);
		this.buttonList.add(btnOptions);
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
    }
	
	@Override
	public void updateScreen() {
		txtInput.updateCursorCounter();
	}
	
	@Override
	public boolean doesGuiPauseGame(){
        return false;
    }
	
	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if(button == btnOptions) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCMenu());
		}
	}
	
	@Override
	protected void keyTyped(char unicode, int keyCode) {
		if(keyCode == ClientGlobalConfig.keyToggleTarget.getKeyCode() && !ClientGlobalConfig.disableChatToggle) {
			if(Keyboard.isRepeatEvent()) {
				if(System.currentTimeMillis() - lastToggleTarget >= 1000) {
					parentChat.getChatSession().setChatTarget((String) null);
				}
			} else if(!txtInput.getText().startsWith("/")) {
				boolean users = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
				String newTarget = parentChat.getChatSession().getNextTarget(users);
				if(!users) {
					lastToggleTarget = System.currentTimeMillis();
				}
				if(!users || newTarget != null) {
					parentChat.getChatSession().setChatTarget(newTarget);
				}
			}
			return;
		} else if(keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
			String s = txtInput.getText().trim();
			if(s.length() > 0) {
				parentChat.addToSentMessages(s);
				if(!EiraIRC.instance.getMCEventHandler().onClientChat(s)) {
					if(ClientCommandHandler.instance.executeCommand(mc.thePlayer, s) != 1) {
						this.mc.thePlayer.sendChatMessage(s);
					}
				}
			}
			this.mc.displayGuiScreen(null);
		} else {
			if (keyCode == Keyboard.KEY_UP) {
				// TODO sent history up
            }
            else if (keyCode == Keyboard.KEY_DOWN) {
            	// TODO sent history down
            }
            else if (keyCode == Keyboard.KEY_PRIOR) {
            	// TODO scroll chat up
            }
            else if (keyCode == Keyboard.KEY_NEXT) {
            	// TODO scroll chat down
            } else {
                txtInput.textboxKeyTyped(unicode, keyCode);
            }
		}
	}
	
	@Override
	public void drawScreen(int i, int j, float k) {
		super.drawScreen(i, j, k);
		
		drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
		txtInput.drawTextBox();
		
		if(!ClientGlobalConfig.disableChatToggle) {
			drawTargetOverlay();
		}
	}
	
	private void drawTargetOverlay() {
		String target = parentChat.getChatSession().getChatTarget();
		if(target == null) {
			target = "Minecraft";
		} else {
			int sepIdx = target.indexOf("/");
			target = target.substring(sepIdx + 1) + " (" + target.substring(0, sepIdx) + ")";
		}
		String text = Utils.getLocalizedMessage("irc.gui.chatTarget", target);
		int rectWidth = Math.max(200, fontRendererObj.getStringWidth(text) + 10);
		drawRect(0, 0, rectWidth, fontRendererObj.FONT_HEIGHT + 6, GuiEiraChat.COLOR_BACKGROUND);
		fontRendererObj.drawString(text, 5, 5, Globals.TEXT_COLOR);
	}
}
