package blay09.mods.eirairc.config;

import org.lwjgl.input.Keyboard;

public class KeyConfig {

	public static final int IDX_OPENSETTINGS = 0;
	public static final int IDX_TOGGLETARGET = 1;
	public static final int IDX_SCREENSHOTSHARE = 2;
	public static final int IDX_TOGGLERECORDING = 3;
	public static final int IDX_TOGGLELIVE = 4;
	
	public static int screenshotShare = -1;
	public static int toggleRecording = Keyboard.KEY_F9;
	public static int toggleLive = -1;
	public static int toggleTarget = Keyboard.KEY_TAB;
	public static int openSettings = -1;
	
}
