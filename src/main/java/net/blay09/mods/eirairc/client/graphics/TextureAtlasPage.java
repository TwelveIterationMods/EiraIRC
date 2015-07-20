// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.graphics;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class TextureAtlasPage {

	private final Map<String, TextureRegion> regionMap = new HashMap<String, TextureRegion>();
	public final String fileName;
	public final ResourceLocation texture;

	public TextureAtlasPage(String fileName, ResourceLocation texture) {
		this.fileName = fileName;
		this.texture = texture;
	}

	public void addRegion(TextureRegion region) {
		regionMap.put(region.name, region);
	}

	public TextureRegion getRegion(String name) {
		return regionMap.get(name);
	}

}
