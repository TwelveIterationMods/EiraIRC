package net.blay09.mods.eirairc.client.gui.base;

import net.blay09.mods.eirairc.client.graphics.TextureRegion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;


public class GuiImageButton extends GuiButton {

	private static final float FADE_PER_FRAME = 0.05f;

	private TextureRegion region;
	private float alphaFade = 1f;
	private int fadeMode;
	private String tooltipText;

	public GuiImageButton(int id, int xPos, int yPos, TextureRegion region) {
		super(id, xPos, yPos, "");
		setTextureRegion(region);
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

	public void setTextureRegion(TextureRegion region) {
		this.region = region;
		width = region.getRegionWidth();
		height = region.getRegionHeight();
	}

	public boolean isAlphaVisible() {
		return alphaFade > 0;
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
			region.draw(xPosition, yPosition);
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
