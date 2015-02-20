package net.blay09.mods.eirairc.client.graphics;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Blay09 on 20.02.2015.
 */
public class TextureAtlasPage {

	private final Map<String, AtlasRegion> regionMap = new HashMap<String, AtlasRegion>();
	public final String fileName;
	public final ResourceLocation texture;

	public TextureAtlasPage(String fileName, ResourceLocation texture) {
		this.fileName = fileName;
		this.texture = texture;
	}

	public void addRegion(AtlasRegion region) {
		regionMap.put(region.name, region);
	}

	public AtlasRegion getRegion(String name) {
		return regionMap.get(name);
	}

}
