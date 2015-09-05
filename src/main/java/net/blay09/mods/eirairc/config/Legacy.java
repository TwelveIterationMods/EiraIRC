package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class Legacy {

    public static EnumChatFormatting getColorForName(String colorName) {
        if(colorName == null || colorName.isEmpty()) {
            return null;
        }
        colorName = colorName.toLowerCase();
        EnumChatFormatting colorFormatting = null;
        switch (colorName) {
            case "black":
                colorFormatting = EnumChatFormatting.BLACK;
                break;
            case "darkblue":
            case "dark blue":
                colorFormatting = EnumChatFormatting.DARK_BLUE;
                break;
            case "green":
                colorFormatting = EnumChatFormatting.DARK_GREEN;
                break;
            case "cyan":
                colorFormatting = EnumChatFormatting.DARK_AQUA;
                break;
            case "darkred":
            case "dark red":
                colorFormatting = EnumChatFormatting.DARK_RED;
                break;
            case "purple":
                colorFormatting = EnumChatFormatting.DARK_PURPLE;
                break;
            case "gold":
            case "orange":
                colorFormatting = EnumChatFormatting.GOLD;
                break;
            case "gray":
            case "grey":
                colorFormatting = EnumChatFormatting.GRAY;
                break;
            case "darkgray":
            case "darkgrey":
            case "dark gray":
            case "dark grey":
                colorFormatting = EnumChatFormatting.DARK_GRAY;
                break;
            case "blue":
                colorFormatting = EnumChatFormatting.BLUE;
                break;
            case "lime":
                colorFormatting = EnumChatFormatting.GREEN;
                break;
            case "lightblue":
            case "light blue":
                colorFormatting = EnumChatFormatting.AQUA;
                break;
            case "red":
                colorFormatting = EnumChatFormatting.RED;
                break;
            case "magenta":
            case "pink":
                colorFormatting = EnumChatFormatting.LIGHT_PURPLE;
                break;
            case "yellow":
                colorFormatting = EnumChatFormatting.YELLOW;
                break;
            case "white":
                colorFormatting = EnumChatFormatting.WHITE;
                break;
        }
        return colorFormatting;
    }

    public static void loadLegacyServer(ServerConfig serverConfig, Configuration legacyConfig, ConfigCategory category) {
        String categoryName = category.getQualifiedName();
        serverConfig.setNick(Utils.unquote(legacyConfig.get(categoryName, "nick", "").getString()));
        if(serverConfig.getNick().isEmpty()) {
            serverConfig.setNick(Utils.unquote(legacyConfig.get("global", "nick", Globals.DEFAULT_NICK).getString()));
        }
        String nickServName = Utils.unquote(legacyConfig.get(categoryName, "nickServName", "").getString());
        String nickServPassword = Utils.unquote(legacyConfig.get(categoryName, "nickServPassword", "").getString());
        if(nickServName != null && !nickServName.isEmpty() && nickServPassword != null && !nickServPassword.isEmpty()) {
            AuthManager.putNickServData(serverConfig.getIdentifier(), nickServName, nickServPassword);
        }
        String serverPassword = Utils.unquote(legacyConfig.get(categoryName, "serverPassword", "").getString());
        if(serverPassword != null && !serverPassword.isEmpty()) {
            AuthManager.putServerPassword(serverConfig.getIdentifier(), serverPassword);
        }
        serverConfig.setIsSSL(legacyConfig.get(categoryName, "secureConnection", serverConfig.isSSL()).getBoolean(serverConfig.isSSL()));
        serverConfig.setCharset(Utils.unquote(legacyConfig.get("global", "charset", serverConfig.getCharset()).getString()));

        String channelsCategoryName = categoryName + Configuration.CATEGORY_SPLITTER + "channels";
        ConfigCategory channelsCategory = legacyConfig.getCategory(channelsCategoryName);
        for(ConfigCategory channelCategory : channelsCategory.getChildren()) {
            ChannelConfig channelConfig = new ChannelConfig(serverConfig);
            Legacy.loadLegacyChannel(channelConfig, legacyConfig, channelCategory);
            serverConfig.addChannelConfig(channelConfig);
        }

        loadLegacyTheme(serverConfig.getTheme(), legacyConfig, categoryName);
        loadLegacyBot(serverConfig.getBotSettings(), legacyConfig, categoryName);
        loadLegacySettings(serverConfig.getGeneralSettings(), legacyConfig, categoryName);
    }

    public static void loadLegacyChannel(ChannelConfig channelConfig, Configuration config, ConfigCategory category) {
        String categoryName = category.getQualifiedName();
        channelConfig.setName(Utils.unquote(config.get(categoryName, "name", "").getString()));
        String password = Utils.unquote(config.get(categoryName, "password", "").getString());
        if(password != null && !password.isEmpty()) {
            AuthManager.putChannelPassword(channelConfig.getIdentifier(), password);
        }
    }

    public static void loadLegacyTheme(ThemeSettings theme, Configuration legacyConfig, String categoryName) {
        if(categoryName != null) {
            String emoteColor = Utils.unquote(legacyConfig.get(categoryName, "emoteColor", "").getString());
            if(!emoteColor.isEmpty()) {
                theme.emoteTextColor.set(Legacy.getColorForName(emoteColor));
            }
            String ircColor = Utils.unquote(legacyConfig.get(categoryName, "ircColor", "").getString());
            if(!ircColor.isEmpty()) {
                theme.ircNameColor.set(Legacy.getColorForName(ircColor));
            }
        } else {
            theme.emoteTextColor.set(Legacy.getColorForName(Utils.unquote(legacyConfig.get("display", "emoteColor", "gold").getString())));
            theme.mcNameColor.set(Legacy.getColorForName(Utils.unquote(legacyConfig.get("display", "defaultColor", "white").getString())));
            theme.mcOpNameColor.set(Legacy.getColorForName(Utils.unquote(legacyConfig.get("display", "opColor", "red").getString())));
            theme.ircNameColor.set(Legacy.getColorForName(Utils.unquote(legacyConfig.get("display", "ircColor", "gray").getString())));
            theme.ircPrivateNameColor.set(Legacy.getColorForName(Utils.unquote(legacyConfig.get("display", "ircPrivateColor", "gray").getString())));
            theme.ircVoiceNameColor.set(Legacy.getColorForName(Utils.unquote(legacyConfig.get("display", "ircVoiceColor", "gray").getString())));
            theme.ircOpNameColor.set(Legacy.getColorForName(Utils.unquote(legacyConfig.get("display", "ircOpColor", "gold").getString())));
            theme.ircNoticeTextColor.set(Legacy.getColorForName(Utils.unquote(legacyConfig.get("display", "ircNoticeColor", "gray").getString())));
        }
    }

    public static void loadLegacySettings(GeneralSettings settings, Configuration legacyConfig, String category) {
        if(category != null) {
            if (legacyConfig.hasKey(category, "autoConnect")) {
                settings.autoJoin.set(legacyConfig.get(category, "autoConnect", settings.autoJoin.getDefaultValue()).getBoolean());
            } else if (legacyConfig.hasKey(category, "autoJoin")) {
                settings.autoJoin.set(legacyConfig.get(category, "autoJoin", settings.autoJoin.getDefaultValue()).getBoolean());
            }
            if (legacyConfig.hasKey(category, "autoWho")) {
                settings.autoWho.set(legacyConfig.get(category, "autoWho", settings.autoWho.getDefaultValue()).getBoolean());
            }
        }
    }

    public static void loadLegacyBot(BotSettings botSettings, Configuration legacyConfig, String category) {
        if(category != null) {
            botSettings.description.set(Utils.unquote(legacyConfig.get(category, "description", botSettings.description.getDefaultValue()).getString()));
            botSettings.ident.set(Utils.unquote(legacyConfig.get(category, "ident", botSettings.ident.getDefaultValue()).getString()));
            String quitMessageOld = Utils.unquote(legacyConfig.get(category, "quitMessage", "").getString());
            if(!quitMessageOld.isEmpty()) {
                botSettings.quitMessage.set(quitMessageOld);
            }
        } else {
            botSettings.mcNickFormat.set(Utils.unquote(legacyConfig.get("serveronly", "nickPrefix", "").getString()) + "%s" + Utils.unquote(legacyConfig.get("serveronly", "nickSuffix", "").getString()));
            botSettings.hideNotices.set(legacyConfig.get("display", "hideNotices", botSettings.hideNotices.getDefaultValue()).getBoolean());
            botSettings.convertColors.set(legacyConfig.get("display", "enableIRCColors", botSettings.convertColors.getDefaultValue()).getBoolean());
        }
    }
}
