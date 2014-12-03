package net.blay09.mods.eirairc.client.gui.base;

import net.minecraft.util.IIcon;

/**
 * Created by Blay09 on 02.11.2014.
 */
public class GuiTextureIcon implements IIcon {

	private final String name;
	private int width;
	private int height;
	private float u;
	private float u2;
	private float v;
	private float v2;

	public GuiTextureIcon(String name) {
		this.name = name;
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	@Override
	public float getMinU() {
		return u;
	}

	@Override
	public float getMaxU() {
		return u2;
	}

	@Override
	public float getMinV() {
		return v;
	}

	@Override
	public float getMaxV() {
		return v2;
	}

	@Override
	public float getInterpolatedU(double p_94214_1_) {
		return 0;
	}

	@Override
	public float getInterpolatedV(double p_94207_1_) {
		return 0;
	}

	@Override
	public String getIconName() {
		return name;
	}

}
