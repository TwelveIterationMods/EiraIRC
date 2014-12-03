package net.blay09.mods.eirairc.client.gui.base;

import cpw.mods.fml.client.config.GuiUtils;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Blay09 on 10.10.2014.
 */
public class GuiImageButton extends GuiButton {

	private static final float FADE_PER_FRAME = 0.05f;

	private final ResourceLocation res;
	private final int texCoordX;
	private final int texCoordY;
	private float alphaFade = 1f;
	private int fadeMode;
	private String tooltipText;

	public GuiImageButton(int id, int xPos, int yPos, ResourceLocation res, int texCoordX, int texCoordY, int texWidth, int texHeight) {
		super(id, xPos, yPos, "");
		this.res = res;
		this.texCoordX = texCoordX;
		this.texCoordY = texCoordY;
		width = texWidth;
		height = texHeight;
	}

	public String getTooltipText() {
		return tooltipText;
	}

	public void setTooltipText(String tooltipText) {
		this.tooltipText = tooltipText;
	}

	public void setFadeMode(int fadeMode) {
		this.fadeMode = fadeMode;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(this.visible) {
			// Fade button alpha in or out
			if(fadeMode > 0) {
				alphaFade = Math.min(1f, alphaFade + FADE_PER_FRAME);
				if(alphaFade == 1f) {
					fadeMode = 0;
				}
			} else if(fadeMode < 0) {
				alphaFade = Math.max(0f, alphaFade - FADE_PER_FRAME);
				if(alphaFade == 0f) {
					fadeMode = 0;
				}
			}
			// Render the button with fade alpha and hover effect
			mc.renderEngine.bindTexture(res);
			boolean hovered = false;
			if(enabled) {
				if (mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height) {
					GL11.glColor4f(1f, 1f, 1f, 1f * alphaFade);
					hovered = true;
				} else {
					GL11.glColor4f(1f, 1f, 1f, 0.75f * alphaFade);
				}
			} else {
				GL11.glColor4f(1f, 1f, 1f, 0.25f * alphaFade);
			}
			if(hovered) {
				GL11.glPushMatrix();
				GL11.glTranslatef(0.5f, 0.5f, 0.5f);
			}
			GuiUtils.drawTexturedModalRect(xPosition, yPosition, texCoordX, texCoordY, width, height, this.zLevel);
			if(hovered) {
				GL11.glPopMatrix();
			}
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return (enabled && visible && isInside(mouseX, mouseY));
	}

	public boolean isInside(int x, int y) {
		return (x >= xPosition && y >= yPosition && x < xPosition + width && y < yPosition + height);
	}
}
