// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

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
