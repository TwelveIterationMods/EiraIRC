// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.ClientChatHandler;
import net.blay09.mods.eirairc.config.KeyConfig;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEiraChat extends GuiChat {

	public static final int COLOR_BACKGROUND = Integer.MIN_VALUE;
	
	private ChatSessionHandler chatSession;
	private String defaultInputText;
	private GuiButton btnOptions;
	
	private long lastToggleTarget;
	
	public GuiEiraChat() {
		chatSession = EiraIRC.instance.getChatSessionHandler();
		defaultInputText = "";
	}
	
	public GuiEiraChat(String defaultInputText) {
		this.defaultInputText = defaultInputText;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		inputField.setText(defaultInputText);
		String s = Utils.getLocalizedMessage("irc.gui.options");
		int bw = fontRendererObj.getStringWidth(s) + 20;
		btnOptions = new GuiButton(0, this.width - bw, 0, bw, 20, s);
		this.buttonList.add(btnOptions);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if(button == btnOptions) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiSettings());
		}
	}
	
	@Override
	protected void keyTyped(char unicode, int keyCode) {
		if(keyCode == KeyConfig.toggleTarget) {
			if(Keyboard.isRepeatEvent()) {
				if(System.currentTimeMillis() - lastToggleTarget >= 1000) {
					chatSession.setChatTarget((String) null);
				}
			} else if(!inputField.getText().startsWith("/")) {
				boolean users = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
				String newTarget = chatSession.getNextTarget(users);
				if(!users) {
					lastToggleTarget = System.currentTimeMillis();
				}
				if(!users || newTarget != null) {
					chatSession.setChatTarget(newTarget);
				}
			}
			return;
		}
		super.keyTyped(unicode, keyCode);
	}
	
	@Override
	public void drawScreen(int i, int j, float k) {
		super.drawScreen(i, j, k);
		String target = chatSession.getChatTarget();
		if(target == null) {
			target = "Minecraft";
		} else {
			int sepIdx = target.indexOf("/");
			target = target.substring(sepIdx + 1) + " (" + target.substring(0, sepIdx) + ")";
		}
		String text = Utils.getLocalizedMessage("irc.gui.chatTarget", target);
		int rectWidth = Math.max(200, fontRendererObj.getStringWidth(text) + 10);
		drawRect(0, 0, rectWidth, fontRendererObj.FONT_HEIGHT + 6, COLOR_BACKGROUND);
		fontRendererObj.drawString(text, 5, 5, Globals.TEXT_COLOR);
	}
}
