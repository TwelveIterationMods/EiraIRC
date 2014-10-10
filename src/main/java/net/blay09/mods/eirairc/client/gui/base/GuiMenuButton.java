package net.blay09.mods.eirairc.client.gui.base;

import net.blay09.mods.eirairc.client.gui.GuiEiraIRCMenu;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiMenuButton extends Gui {

	private static final ResourceLocation menuIcons = new ResourceLocation("eirairc", "gfx/menu.png");
	private static final int BUTTON_SIZE = 64;

	private final Minecraft mc;
	private final String title;
	private final int texCoordX;
	private final int texCoordY;
	private final int xPos;
	private final int yPos;
	private boolean playButtonSound = true;

	public GuiMenuButton(String title, int xPos, int yPos, int texCoordX, int texCoordY) {
		this.mc = Minecraft.getMinecraft();
		this.title = title;
		this.xPos = xPos;
		this.yPos = yPos;
		this.texCoordX = texCoordX;
		this.texCoordY = texCoordY;
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(mouseButton == 0) {
			if (mouseX >= xPos && mouseX < xPos + BUTTON_SIZE && mouseY >= yPos && mouseY < yPos + BUTTON_SIZE) {
				if(playButtonSound) {
					mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0f));
				}
				if (mc.currentScreen instanceof GuiEiraIRCMenu) {
					((GuiEiraIRCMenu) mc.currentScreen).actionPerformed(this);
				}
			}
		}
	}

	public void draw(int mouseX, int mouseY) {
		boolean hovered = false;
		if(mouseX >= xPos && mouseX < xPos + BUTTON_SIZE && mouseY >= yPos && mouseY < yPos + BUTTON_SIZE) {
			hovered = true;
		}

		GL11.glColor4f(1f, 1f, 1f, 1f);
		GL11.glEnable(GL11.GL_BLEND);
		if(hovered) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.95f, 0.95f, 0.95f);
		}
		mc.getTextureManager().bindTexture(menuIcons);
		drawTexturedModalRect(xPos, yPos, texCoordX, texCoordY, BUTTON_SIZE, BUTTON_SIZE);
		if(hovered) {
			GL11.glPopMatrix();
		}
		GL11.glDisable(GL11.GL_BLEND);

		drawCenteredString(mc.fontRenderer, (hovered ? "\u00a7n" : "") + title, xPos + BUTTON_SIZE / 2, yPos + BUTTON_SIZE + 5, !hovered ? Globals.TEXT_COLOR : 16777115);
	}

	public void setPlayButtonSound(boolean playButtonSound) {
		this.playButtonSound = playButtonSound;
	}
}
