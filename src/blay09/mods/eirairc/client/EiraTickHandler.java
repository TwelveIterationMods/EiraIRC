// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import java.util.EnumSet;

import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.client.gui.GuiEiraChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class EiraTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(type.contains(TickType.CLIENT)) {
			if(Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen.getClass() == GuiChat.class) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiEiraChat());
			}
		}
		if(type.contains(TickType.RENDER)) {
			float delta = (Float) tickData[0];
			GuiNotification.instance.updateAndRender(delta);
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT, TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "EIRC-CE";
	}

}
