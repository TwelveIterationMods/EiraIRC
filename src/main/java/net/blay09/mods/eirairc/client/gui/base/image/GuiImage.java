package net.blay09.mods.eirairc.client.gui.base.image;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Blay09 on 10.10.2014.
 */
public abstract class GuiImage extends AbstractTexture {

	private final Minecraft mc;

	public GuiImage() {
		this.mc = Minecraft.getMinecraft();
	}

	public void loadTexture() {
		try {
			loadTexture(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		try {
			BufferedImage bufferedImage = loadImage();
			if(bufferedImage != null) {
				TextureUtil.uploadTextureImage(getGlTextureId(), bufferedImage);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract BufferedImage loadImage() throws IOException;

	public void draw(int xPos, int yPos, int width, int height, float zLevel) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getGlTextureId());
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(xPos, yPos + height, zLevel, 0, 1);
		tessellator.addVertexWithUV(xPos + width, yPos + height, zLevel, 1, 1);
		tessellator.addVertexWithUV(xPos + width, yPos, zLevel, 1, 0);
		tessellator.addVertexWithUV(xPos, yPos, zLevel, 0, 0);
		tessellator.draw();
	}

	public void dispose() {
		TextureUtil.deleteTexture(getGlTextureId());
	}
}
