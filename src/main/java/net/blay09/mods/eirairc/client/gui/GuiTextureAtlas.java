package net.blay09.mods.eirairc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Blay09 on 02.11.2014.
 */
public class GuiTextureAtlas extends AbstractTexture implements IIconRegister {

	private static final Logger logger = LogManager.getLogger();
	private final Map<String, GuiTextureIcon> registeredSprites = new HashMap<String, GuiTextureIcon>();

	@Override
	public IIcon registerIcon(String name) {
		GuiTextureIcon icon = new GuiTextureIcon(name);
		registeredSprites.put(name, icon);
		return icon;
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		deleteGlTexture();

		int maxTexSize = Minecraft.getGLMaximumTextureSize();
		Stitcher stitcher = new Stitcher(maxTexSize, maxTexSize, true, 0, 0);
		for(Map.Entry<String, GuiTextureIcon> entry : registeredSprites.entrySet()) {
			ResourceLocation resourceLocation = new ResourceLocation("eirairc", entry.getKey());
			GuiTextureIcon sprite = entry.getValue();
			try {
				IResource resource = resourceManager.getResource(resourceLocation);
				BufferedImage[] image = new BufferedImage[1];
				image[0] = ImageIO.read(resource.getInputStream());
//				sprite.loadSprite(image, null, false);
			} catch (IOException e) {
				logger.error("Using missing texture, unable to load " + resourceLocation, e);
				continue;
			}
//			stitcher.addSprite(sprite);
		}

		logger.info("Created: {}x{} EiraIRC-atlas", stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
		TextureUtil.allocateTextureImpl(getGlTextureId(), 0, stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), 0);

		for(Object object : stitcher.getStichSlots()) {
			TextureAtlasSprite sprite = (TextureAtlasSprite) object;
			TextureUtil.uploadTextureMipmap(sprite.getFrameTextureData(0), sprite.getIconWidth(), sprite.getIconHeight(), sprite.getOriginX(), sprite.getOriginY(), false, false);
			sprite.clearFramesTextureData();
		}
	}

}
