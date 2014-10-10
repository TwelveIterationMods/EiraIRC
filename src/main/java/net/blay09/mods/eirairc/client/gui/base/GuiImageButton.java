package net.blay09.mods.eirairc.client.gui.base;

import cpw.mods.fml.client.config.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Blay09 on 10.10.2014.
 */
public class GuiImageButton extends GuiButton {

	private final ResourceLocation res;
	private final int texCoordX;
	private final int texCoordY;

	public GuiImageButton(int id, int xPos, int yPos, ResourceLocation res, int texCoordX, int texCoordY, int texWidth, int texHeight) {
		super(id, xPos, yPos, "");
		this.res = res;
		this.texCoordX = texCoordX;
		this.texCoordY = texCoordY;
		width = texWidth;
		height = texHeight;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(this.visible) {
			mc.renderEngine.bindTexture(res);
			if(enabled) {
				if (mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height) {
					GL11.glColor4f(1f, 1f, 1f, 1f);
				} else {
					GL11.glColor4f(1f, 1f, 1f, 0.5f);
				}
			} else {
				GL11.glColor4f(1f, 1f, 1f, 0.25f);
			}
			GuiUtils.drawTexturedModalRect(xPosition, yPosition, texCoordX, texCoordY, width, height, this.zLevel);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return (enabled && visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height);
	}
}
