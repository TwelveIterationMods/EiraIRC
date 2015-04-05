package net.blay09.mods.eirairc.config.settings;

/**
 * Created by Blay09 on 02.10.2014.
 */
public enum BotStringListComponent {
	InterOpAuthList("interOpAuthList", new String[0], "eirairc:config.property.interOpAuthList", false),
	DisabledNativeCommands("disabledNativeCommands", new String[0], "eirairc:config.property.disabledNativeCommands", true),
	DisabledInterOpCommands("disabledInterOpCommands", new String[0], "eirairc:config.property.disabledInterOpCommands", true);

	public static final BotStringListComponent[] values = values();

	public final String name;
	public final String[] defaultValue;
	public final String langKey;
	public final boolean allowWildcard;

	BotStringListComponent(String name, String[] defaultValue, String langKey, boolean allowWildcard) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.langKey = langKey;
		this.allowWildcard = allowWildcard;
	}

	public static BotStringListComponent fromName(String name) {
		for(BotStringListComponent value : values) {
			if (value.name.equals(name)) {
				return value;
			}
		}
		return null;
	}
}
