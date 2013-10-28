package blay09.mods.eirairc.config;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;

public class DisplayFormatConfig {

	public static void defaultConfig(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		
		/*
		 * CLASSIC
		 */
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "classic", "name", "Classic");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "classic", "mcChannelMessage", "[{CHANNEL}] <{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "classic", "mcChannelEmote", "[{CHANNEL}] * {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "classic", "mcPrivateMessage", "[Private] <{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "classic", "mcPrivateEmote", "[Private] * {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "classic", "ircChannelMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "classic", "ircChannelEmote", "* {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "classic", "ircPrivateMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "classic", "ircPrivateEmote", "* {NICK} {MESSAGE}");
		
		/*
		 * Light
		 */
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "light", "name", "Light");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "light", "mcChannelMessage", "[ <{NICK}> {MESSAGE} ]");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "light", "mcChannelEmote", "[ * {NICK} {MESSAGE} ]");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "light", "mcPrivateMessage", "[ <{NICK}> {MESSAGE} ]");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "light", "mcPrivateEmote", "[[ {NICK} {MESSAGE} ]]");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "light", "ircChannelMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "light", "ircChannelEmote", "* {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "light", "ircPrivateMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "light", "ircPrivateEmote", "* {NICK} {MESSAGE}");
		
		/*
		 * S-Light
		 */
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "slight", "name", "S-Light");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "slight", "mcChannelMessage", "[{NICK}] {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "slight", "mcChannelEmote", "[ * {NICK} {MESSAGE} ]");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "slight", "mcPrivateMessage", "[[{NICK}]] {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "slight", "mcPrivateEmote", "[[ {NICK} {MESSAGE} ]]");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "slight", "ircChannelMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "slight", "ircChannelEmote", "* {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "slight", "ircPrivateMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "slight", "ircPrivateEmote", "* {NICK} {MESSAGE}");
		
		/*
		 * Minecraft
		 */
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "minecraft", "name", "Minecraft");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "minecraft", "mcChannelMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "minecraft", "mcChannelEmote", "* {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "minecraft", "mcPrivateMessage", "[P] <{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "minecraft", "mcPrivateEmote", "[P] * {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "minecraft", "ircChannelMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "minecraft", "ircChannelEmote", "* {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "minecraft", "ircPrivateMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "minecraft", "ircPrivateEmote", "* {NICK} {MESSAGE}");
		
		/*
		 * Detail
		 */
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "detail", "name", "Detail");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "detail", "mcChannelMessage", "[{SERVER}/{CHANNEL}] <{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "detail", "mcChannelEmote", "[{SERVER}/{CHANNEL}] * {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "detail", "mcPrivateMessage", "[{SERVER}]  <{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "detail", "mcPrivateEmote", "[{SERVER}] * {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "detail", "ircChannelMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "detail", "ircChannelEmote", "* {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "detail", "ircPrivateMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "detail", "ircPrivateEmote", "* {NICK} {MESSAGE}");
		
		/*
		 * CUSTOM
		 */
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "custom", "name", "Classic");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "custom", "mcChannelMessage", "[{CHANNEL}] <{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "custom", "mcChannelEmote", "[{CHANNEL}] * {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "custom", "mcPrivateMessage", "[Private] <{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "custom", "mcPrivateEmote", "[Private] * {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "custom", "ircChannelMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "custom", "ircChannelEmote", "* {NICK} {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "custom", "ircPrivateMessage", "<{NICK}> {MESSAGE}");
		config.get(categoryName + Configuration.CATEGORY_SPLITTER + "custom", "ircPrivateEmote", "* {NICK} {MESSAGE}");
		config.getCategory(categoryName + Configuration.CATEGORY_SPLITTER + "custom").setComment("Use this one if you want to customize the way messages are displayed in minecraft / on IRC.");
		
	}
	
	private String categoryName;
	private String name;
	public String mcChannelMessage;
	public String mcChannelEmote;
	public String mcPrivateMessage;
	public String mcPrivateEmote;
	public String ircChannelMessage;
	public String ircChannelEmote;
	public String ircPrivateMessage;
	public String ircPrivateEmote;
	
	public DisplayFormatConfig(ConfigCategory category) {
		categoryName = category.getQualifiedName();
	}
	
	public void load(Configuration config) {
		name = config.get(categoryName, "name", "").getString();
		mcChannelMessage = config.get(categoryName, "mcChannelMessage", "").getString();
		mcChannelEmote = config.get(categoryName, "mcChannelEmote", "").getString();
		mcPrivateMessage = config.get(categoryName, "mcPrivateMessage", "").getString();
		mcPrivateEmote = config.get(categoryName, "mcPrivateEmote", "").getString();
		
		ircChannelMessage = config.get(categoryName, "ircChannelMessage", "").getString();
		ircChannelEmote = config.get(categoryName, "ircChannelEmote", "").getString();
		ircPrivateMessage = config.get(categoryName, "ircPrivateMessage", "").getString();
		ircPrivateEmote = config.get(categoryName, "ircPrivateEmote", "").getString();
	}
	
	public String getName() {
		return name;
	}
	
}
