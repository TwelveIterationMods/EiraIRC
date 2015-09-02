// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.util;

import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;

import java.io.InputStream;

public class I19n {
	
	public static void init() {
        InputStream inputStream = StringTranslate.class.getResourceAsStream("/assets/eirairc/lang/en_US.lang");
		StringTranslate.inject(inputStream);
	}

	public static String format(String key, Object... params) {
		return StatCollector.translateToLocalFormatted(key, params);
	}

}
