// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.chat;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.gui.settings.GuiSettings;
import net.blay09.mods.eirairc.config2.ClientGlobalConfig;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.ClientCommandHandler;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiChatExtended extends GuiChat {

	public static final int COLOR_BACKGROUND = Integer.MIN_VALUE;
	
	private ChatSessionHandler chatSession;
	private String defaultInputText;
	private GuiButton btnOptions;
	
	private long lastToggleTarget;
	
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
		if(keyCode == ClientGlobalConfig.keyToggleTarget && !ClientGlobalConfig.disableChatToggle && !ClientGlobalConfig.clientBridge && !inputField.getText().startsWith("/")) {
			if(Keyboard.isRepeatEvent()) {
				if(System.currentTimeMillis() - lastToggleTarget >= 1000) {
					chatSession.setChatTarget((String) null);
				}
			} else {
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
		} else if(keyCode == 28 || keyCode == 156) {
			String s = inputField.getText().trim();
			if(s.length() > 0) {
				if(!EiraIRC.instance.getMCEventHandler().onClientChat(s)) {
					if(ClientCommandHandler.instance.executeCommand(mc.thePlayer, s) != 1) {
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
	public void drawScreen(int i, int j, float k) {
		super.drawScreen(i, j, k);
		if(!ClientGlobalConfig.disableChatToggle && !ClientGlobalConfig.clientBridge) {
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
}
