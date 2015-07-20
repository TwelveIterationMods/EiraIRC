// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.graphics;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextureAtlas {

	private final List<TextureAtlasPage> pages = new ArrayList<TextureAtlasPage>();

	public TextureAtlas(IResourceManager resourceManager, ResourceLocation resourceLocation) throws IOException {
		String resourcePath = resourceLocation.getResourcePath();
		resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf('/') + 1);
		InputStream in = resourceManager.getResource(resourceLocation).getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		TextureAtlasPage currentPage = null;
		TextureRegion currentRegion = null;
		while((line = reader.readLine()) != null) {
			line = line.trim();
			if(line.isEmpty()) {
				currentPage = null;
				continue;
			}
			if(currentPage == null) {
				currentPage = new TextureAtlasPage(line, new ResourceLocation(resourceLocation.getResourceDomain(), resourcePath + line));
				pages.add(currentPage);
			} else {
				int sepIdx = line.indexOf(':');
				if(sepIdx == -1) {
					currentRegion = new TextureRegion(currentPage.texture, line);
					currentPage.addRegion(currentRegion);
				} else {
					String key = line.substring(0, sepIdx);
					String value = line.substring(sepIdx + 1);
					if(currentRegion != null) {
						if(key.equals("xy")) {
							String[] xy = value.split(",");
							currentRegion.setRegionX(Integer.parseInt(xy[0].trim()));
							currentRegion.setRegionY(Integer.parseInt(xy[1].trim()));
						} else if(key.equals("size")) {
							String[] size = value.split(",");
							currentRegion.setRegionWidth(Integer.parseInt(size[0].trim()));
							currentRegion.setRegionHeight(Integer.parseInt(size[1].trim()));
						}
					}
				}
			}
		}
		reader.close();
		in.close();
	}

	public TextureRegion findRegion(String name) {
		for(TextureAtlasPage page : pages) {
			TextureRegion region = page.getRegion(name);
			if(region != null) {
				return region;
			}
		}
		throw new RuntimeException("Could not find EiraIRC atlas region '" + name + "'");
	}

}
