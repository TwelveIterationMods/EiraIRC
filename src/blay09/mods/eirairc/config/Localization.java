// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import java.io.InputStream;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringTranslate;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Localization {
	
	public static void init() {
        InputStream inputStream = StringTranslate.class.getResourceAsStream("/assets/eirairc/lang/en_US.lang");
		StringTranslate.inject(inputStream);
	}
	
}
