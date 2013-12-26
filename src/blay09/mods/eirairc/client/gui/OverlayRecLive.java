package blay09.mods.eirairc.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.DisplayConfig;
import blay09.mods.eirairc.net.EiraPlayerInfo;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

public class OverlayRecLive extends Gui {

	private static final ResourceLocation icons = new ResourceLocation("eirairc", "gfx/icons.png");

	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;
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
		if(!DisplayConfig.hudRecState) {
			return;
		}
		updateGuiScale();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		theGame.getTextureManager().bindTexture(icons);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		if(playerInfo.isLive) {
			int stringWidth = theGame.fontRenderer.getStringWidth(LIVE);
			drawTexturedModalRect(windowWidth - WIDTH - MARGIN * 2 - stringWidth, MARGIN, 0, 0, WIDTH, HEIGHT);
			drawString(theGame.fontRenderer, LIVE, windowWidth - stringWidth - MARGIN, MARGIN + theGame.fontRenderer.FONT_HEIGHT / 2, Globals.TEXT_COLOR);
		} else if(playerInfo.isRecording) {
			drawTexturedModalRect(windowWidth - WIDTH - MARGIN, MARGIN, 0, 0, WIDTH, HEIGHT);
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
}
