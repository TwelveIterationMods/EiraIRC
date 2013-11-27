// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;

import org.lwjgl.input.Keyboard;

import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.Utils;
import blay09.mods.eirairc.config.Globals;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEiraChat extends GuiChat {

	public static final int COLOR_BACKGROUND = Integer.MIN_VALUE;
	
	private String defaultInputText;
	private GuiButton btnOptions;
	
	public GuiEiraChat() {
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
		int bw = fontRenderer.getStringWidth(s) + 20;
		btnOptions = new GuiButton(0, this.width - bw, 0, bw, 20, s);
		this.buttonList.add(btnOptions);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if(button == btnOptions) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiIRCSettings());
		}
	}
	
	@Override
	protected void keyTyped(char unicode, int keyCode) {
		if(keyCode == Keyboard.KEY_RETURN) {
			String text = this.inputField.getText().trim();
			if(text.length() > 0) {
				this.mc.ingameGUI.getChatGUI().addToSentMessages(text);
				if(!ClientChatHandler.handleClientChat(text)) {
					if(!this.mc.handleClientCommand(text)) {
						this.mc.thePlayer.sendChatMessage(text);
					}
				}
			}
			this.mc.displayGuiScreen(null);
			return;
		} else if(keyCode == Keyboard.KEY_TAB) {
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				// TODO switch between Both, IRC, MC target
				return;
			}
		}
		super.keyTyped(unicode, keyCode);
	}
	
	@Override
	public void drawScreen(int i, int j, float k) {
		super.drawScreen(i, j, k);
		drawRect(0, 0, 200, 15, COLOR_BACKGROUND);
		String target = null;
		switch(EiraIRC.instance.getChatTarget()) {
		case All:
			target = "All";
			break;
		case ChannelOnly:
			String[] ss = EiraIRC.instance.getTargetChannel().split(":");
			target = ss[1] + " (" + ss[0] + ")";
			break;
		case IRCOnly:
			target = "IRC only";
			break;
		case MinecraftOnly:
			target = "Minecraft only";
			break;
		}
		String text = Utils.getLocalizedMessage("irc.gui.chatTarget", target);
		fontRenderer.drawString(text, 5, 5, Globals.TEXT_COLOR);
	}
}
