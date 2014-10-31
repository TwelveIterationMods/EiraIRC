// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.overlay;

import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.NotificationType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class OverlayNotification extends Gui {

	private static final ResourceLocation backgroundTextures = new ResourceLocation("textures/gui/achievement/achievement_background.png");
	private static final int WIDTH = 160;
	private static final int HEIGHT = 32;
	private static final float TIME_ROLLIN = 8f;
	private static final float TIME_ROLLOUT = 200f;
	
    private final Minecraft theGame;
    
    private NotificationType type;
    private String text;
    private float notificationTime;
    private boolean visible;
    
    private int windowWidth;
    private int windowHeight;
    
    public OverlayNotification() {
    	theGame = Minecraft.getMinecraft();
    }
    
    public void showNotification(NotificationType type, String text) {
    	this.type = type;
    	this.text = text;
    	notificationTime = 0f;
    	visible = true;
    }
    
    public void updateGuiScale() {
    	GL11.glViewport(0, 0, this.theGame.displayWidth, this.theGame.displayHeight);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        windowWidth = this.theGame.displayWidth;
        windowHeight = this.theGame.displayHeight;
        ScaledResolution scaledResolution = new ScaledResolution(this.theGame, this.theGame.displayWidth, this.theGame.displayHeight);
        windowWidth = scaledResolution.getScaledWidth();
        windowHeight = scaledResolution.getScaledHeight();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double) windowWidth, (double) windowHeight, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }
    
	public void updateAndRender(float delta) {
		if(!visible) {
			return;
		}
		notificationTime += delta;
		if(notificationTime >= TIME_ROLLOUT) {
			visible = false;
			return;
		}
		updateGuiScale();
		
		int offset = 0;
		if(notificationTime < TIME_ROLLOUT - TIME_ROLLIN) {
			offset = HEIGHT - (int) (HEIGHT * Math.min(1f, (float) notificationTime / (float) TIME_ROLLIN));
		} else {
			offset = HEIGHT * 2 + (int) (HEIGHT * Math.min(1f, (float) (notificationTime - TIME_ROLLOUT - TIME_ROLLIN) / (float) TIME_ROLLIN));
		}
		int x = windowWidth - WIDTH;
		int y = windowHeight - HEIGHT + offset;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Minecraft.getMinecraft().getTextureManager().bindTexture(backgroundTextures);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		// TODO render icon here
		drawTexturedModalRect(x, y, 96, 202, WIDTH, HEIGHT);
		theGame.fontRendererObj.drawSplitString(text, x + 30, y + 32 / 2 - theGame.fontRendererObj.FONT_HEIGHT * 2 / 2, WIDTH - 36, Globals.TEXT_COLOR);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
}
