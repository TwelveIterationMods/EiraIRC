// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.client;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import blay09.mods.irc.EiraIRC;
import blay09.mods.irc.EnumChatTarget;
import blay09.mods.irc.config.GlobalConfig;
import blay09.mods.irc.config.Globals;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyBindingHandler extends KeyHandler {

	private static KeyBinding toggleChatTarget = new KeyBinding("ToggleChatTarget", Keyboard.KEY_TAB);
	private static KeyBinding ircSettings = new KeyBinding("IRCSettings", Keyboard.KEY_I);
	
	
	public KeyBindingHandler() {
		super(new KeyBinding[] { toggleChatTarget, ircSettings }, new boolean[] { false, false });
	}

	@Override
	public String getLabel() {
		return Globals.MOD_ID + ":" + this.getClass().getSimpleName();
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if(!tickEnd) {
			return;
		}
		if(kb == toggleChatTarget) {
			if(!(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
				return;
			}
			if(EiraIRC.instance.getConnectionCount() == 0) {
				return;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				EiraIRC.instance.setChatTarget(EnumChatTarget.ChannelOnly);
			} else {
				switch(EiraIRC.instance.getChatTarget()) {
				case All:
					EiraIRC.instance.setChatTarget(EnumChatTarget.MinecraftOnly);
					break;
				case ChannelOnly:
					EiraIRC.instance.setChatTarget(EnumChatTarget.All);
					break;
				case IRCOnly:
					EiraIRC.instance.setChatTarget(EnumChatTarget.All);
					break;
				case MinecraftOnly:
					EiraIRC.instance.setChatTarget(EnumChatTarget.IRCOnly);
					break;
				}
			}
		} else if(kb == ircSettings) {
			if(Minecraft.getMinecraft().currentScreen == null) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiIRCSettings());
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
