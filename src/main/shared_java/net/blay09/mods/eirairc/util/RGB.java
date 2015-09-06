package net.blay09.mods.eirairc.util;

public class RGB {
	public float r;
    public float g;
	public float b;

    public RGB(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

	public static RGB fromHex(String hexColor) {
		return new RGB(
				(float) Integer.valueOf(hexColor.substring(1, 3), 16) / 255f,
				(float) Integer.valueOf(hexColor.substring(3, 5), 16) / 255f,
				(float) Integer.valueOf(hexColor.substring(5, 7), 16) / 255f);
	}
}
