// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.overlay;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class OverlayRecLive extends Gui {

	private static final ResourceLocation icons = new ResourceLocation("eirairc", "gfx/icons.png");

	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;
	private static final int SMALL_WIDTH = 8;
	private static final int SMALL_HEIGHT = 8;
	private static final int MARGIN = 5;
	private static final String LIVE = "LIVE";

	private Minecraft theGame;
	private EiraPlayerInfo playerInfo;
	private int windowWidth;
	private int windowHeight;

	public OverlayRecLive() {
		theGame = Minecraft.getMinecraft();
		playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(Utils.getUsername());
	}

	public void updateGuiScale() {
		GL11.glViewport(0, 0, this.theGame.displayWidth, this.theGame.displayHeight);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		windowWidth = this.theGame.displayWidth;
		windowHeight = this.theGame.displayHeight;
		ScaledResolution scaledresolution = new ScaledResolution(this.theGame.gameSettings, this.theGame.displayWidth, this.theGame.displayHeight);
		windowWidth = scaledresolution.getScaledWidth();
		windowHeight = scaledresolution.getScaledHeight();
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, (double) windowWidth, (double) windowHeight, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	}

	public void updateAndRender(float delta) {
		if(!EiraIRC.proxy.isIngame()) {
			return;	
		}
		updateGuiScale();
		int liveWidth = theGame.fontRenderer.getStringWidth(LIVE);
		ScoreObjective scoreobjective = theGame.theWorld.getScoreboard().func_96539_a(0);
		NetHandlerPlayClient handler = theGame.thePlayer.sendQueue;
		if (theGame.gameSettings.keyBindPlayerList.getIsKeyPressed() && (!theGame.isIntegratedServerRunning() || handler.playerInfoList.size() > 1 || scoreobjective != null)) {
			List players = handler.playerInfoList;
			int maxPlayers = handler.currentServerMaxPlayers;
			int rows = maxPlayers;
			int columns = 1;
			for (columns = 1; rows > 20; rows = (maxPlayers + columns - 1) / columns) {
				columns++;
			}
			int columnWidth = 300 / columns;
			if (columnWidth > 150) {
				columnWidth = 150;
			}
			int left = (windowWidth - columns * columnWidth) / 2;
			byte border = 10;
			for (int i = 0; i < maxPlayers; i++) {
				int xPos = left + i % columns * columnWidth;
				int yPos = border + i / columns * 9;
				if (i < players.size()) {
					EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(((GuiPlayerInfo)players.get(i)).name);
					if(playerInfo.isLive) {
						theGame.getTextureManager().bindTexture(icons);
						drawTexturedModalRect(xPos + columnWidth - 12 - SMALL_WIDTH - liveWidth - 4, yPos, WIDTH, 0, SMALL_WIDTH, SMALL_HEIGHT);
						drawString(theGame.fontRenderer, LIVE, xPos + columnWidth - 12 - liveWidth - 2, yPos, Globals.TEXT_COLOR);
					} else if(playerInfo.isRecording) {
						theGame.getTextureManager().bindTexture(icons);
						drawTexturedModalRect(xPos + columnWidth - 12 - SMALL_WIDTH - liveWidth, yPos, WIDTH, 0, SMALL_WIDTH, SMALL_HEIGHT);
					}
				}
			}
		}
		if (DisplayConfig.hudRecState) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			theGame.getTextureManager().bindTexture(icons);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			if (playerInfo.isLive) {
				drawTexturedModalRect(windowWidth - WIDTH - MARGIN * 2 - liveWidth, MARGIN, 0, 0, WIDTH, HEIGHT);
				drawString(theGame.fontRenderer, LIVE, windowWidth - liveWidth - MARGIN, MARGIN + theGame.fontRenderer.FONT_HEIGHT / 2, Globals.TEXT_COLOR);
			} else if (playerInfo.isRecording) {
				drawTexturedModalRect(windowWidth - WIDTH - MARGIN, MARGIN, 0, 0, WIDTH, HEIGHT);
			}
		}
	}

}
