package net.blay09.mods.eirairc.client.gui.base;

import net.blay09.mods.eirairc.client.graphics.AtlasRegion;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCMenu;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiMenuButton extends GuiButton {

	private final Minecraft mc;
	private final AtlasRegion region;
	private final int xPos;
	private final int yPos;
	private boolean playButtonSound = true;

	public GuiMenuButton(int id, String title, int xPos, int yPos, int width, int height, AtlasRegion region) {
		super(id, xPos, yPos, title);
		this.mc = Minecraft.getMinecraft();
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		this.region = region;
	}

	@Override
	public void func_146113_a(SoundHandler soundHandler) { // playButtonSound
		if(playButtonSound) {
			super.func_146113_a(soundHandler);
		}
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		boolean hovered = false;
		if(mouseX >= xPos && mouseX < xPos + width && mouseY >= yPos && mouseY < yPos + height) {
			hovered = true;
		}

		GL11.glColor4f(1f, 1f, 1f, 1f);
		GL11.glEnable(GL11.GL_BLEND);
		if(hovered) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.95f, 0.95f, 0.95f);
		}
		region.draw(xPos, yPos);
		if(hovered) {
			GL11.glPopMatrix();
		}
		GL11.glDisable(GL11.GL_BLEND);

		drawCenteredString(mc.fontRenderer, (hovered ? "\u00a7n" : "") + displayString, xPos + width / 2, yPos + height + 5, !hovered ? Globals.TEXT_COLOR : 16777115);
	}

	public void setPlayButtonSound(boolean playButtonSound) {
		this.playButtonSound = playButtonSound;
	}
}
