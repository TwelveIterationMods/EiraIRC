// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.addon;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.api.config.IConfigProperty;
import net.blay09.mods.eirairc.api.event.FormatNick;
import net.blay09.mods.eirairc.api.event.IRCChannelJoinedEvent;
import net.blay09.mods.eirairc.api.event.InitConfigEvent;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.TwitchUser;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eiramoticons.api.EiraMoticonsAPI;
import net.blay09.mods.eiramoticons.api.IEmoticon;
import net.blay09.mods.eiramoticons.api.IEmoticonLoader;
import net.blay09.mods.eiramoticons.api.ReloadEmoticons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

@Optional.Interface(modid = "eiramoticons", iface = "net.blay09.mods.eiramoticons.api.IEmoticonLoader")
public class EiraMoticonsAddon implements IEmoticonLoader {

    public static final String TWITCH_EMOTES_API = "https://twitchemotes.com/api_cache/v2/subscriber.json";

    private static EiraMoticonsAddon instance;
    public static final Map<IRCChannel, IEmoticon> subscriberBadgeMap = Maps.newHashMap();
    public static IEmoticon casterBadge;
    public static IEmoticon modBadge;
    public static IEmoticon turboBadge;

    public EiraMoticonsAddon() {
        MinecraftForge.EVENT_BUS.register(this);
        reloadEmoticons(new ReloadEmoticons());
        instance = this;
    }

    @SubscribeEvent
    public void initConfig(InitConfigEvent.ThemeSettings event) {
        event.config.registerProperty("eiramoticons", "alwaysShowSubBadge", "eirairc:config.property.eiramoticons_alwaysShowSubBadge", false);
        event.config.registerProperty("eiramoticons", "twitchNameBadges", "eirairc:config.property.eiramoticons_twitchNameBadges", true);
    }

    @Optional.Method(modid = "eiramoticons")
    public static String getSubscriberBadgeString(IRCChannel channel) {
        IEmoticon subBadge = getSubscriberBadge(channel);
        if (subBadge != null) {
            return subBadge.getChatString();
        }
        return "";
    }

    @Optional.Method(modid = "eiramoticons")
    public static IEmoticon getSubscriberBadge(final IRCChannel channel) {
        IEmoticon subBadge = null;
        synchronized (subscriberBadgeMap) {
            if (!subscriberBadgeMap.containsKey(channel)) {
                // Put null so we only start looking up this badge once
                subscriberBadgeMap.put(channel, null);

                // Look up this badge asynchronously
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        try {
                            URL url = new URL(TWITCH_EMOTES_API);
                            InputStream in = url.openStream();
                            Reader reader = new InputStreamReader(in);
                            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                            JsonObject jsonChannels = jsonObject.getAsJsonObject("channels");
                            JsonObject jsonChannel = jsonChannels.getAsJsonObject(channel.getName().substring(1).toLowerCase());
                            if (jsonChannel != null) {
                                IEmoticon subBadge = EiraMoticonsAPI.registerEmoticon("EiraIRC:subBadge" + jsonChannel.get("title").getAsString(), instance);
                                subBadge.setManualOnly(true);
                                subBadge.setLoadData(new URL("http:" + jsonChannel.get("badge").getAsString()));
                                subBadge.setCustomTooltip(new String[]{I19n.format("eirairc:addons.twitch.channelSubscriber")});
                                synchronized (subscriberBadgeMap) {
                                    subscriberBadgeMap.put(channel, subBadge);
                                }
                            }
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                subBadge = subscriberBadgeMap.get(channel);
            }
        }
        return subBadge;
    }

    @SubscribeEvent
    @Optional.Method(modid = "eiramoticons")
    public void onChannelJoined(IRCChannelJoinedEvent event) {
        IConfigProperty<Boolean> twitchNameBadges = event.channel.getThemeSettings().getProperty("eiramoticons", "twitchNameBadges");
        if (twitchNameBadges != null && twitchNameBadges.get() && event.channel.getConnection().isTwitch()) {
            // Pre-load this channels sub badge
            getSubscriberBadge(event.channel);
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = "eiramoticons")
    public void formatNick(FormatNick event) {
        if(event.context != null) {
            IConfigProperty<Boolean> twitchNameBadges = event.context.getThemeSettings().getProperty("eiramoticons", "twitchNameBadges");
            if (twitchNameBadges != null && twitchNameBadges.get() && event.context.getConnection().isTwitch()) {
                String badges = "";
                if (event.user.getName().toLowerCase().equals(event.context.getName().substring(1).toLowerCase())) {
                    badges += casterBadge.getChatString();
                } else if (event.user.isOperator((IRCChannel) event.context)) {
                    badges += modBadge.getChatString();
                }
                if (((TwitchUser) event.user).isTwitchTurbo()) {
                    badges += turboBadge.getChatString();
                }
                IConfigProperty<Boolean> alwaysShowSubBadge = event.context.getThemeSettings().getProperty("eiramoticons", "alwaysShowSubBadge");
                if (((TwitchUser) event.user).isTwitchSubscriber((IRCChannel) event.context) || (alwaysShowSubBadge != null && alwaysShowSubBadge.get())) {
                    String badgeString = getSubscriberBadgeString((IRCChannel) event.context);
                    if (!badgeString.isEmpty()) {
                        badges += badgeString + " ";
                    }
                }
                if (badges.length() > 0) {
                    badges += " ";
                }
                IChatComponent component = new ChatComponentText(badges);
                component.appendSibling(event.component);
                event.component = component;
            }
        }
    }

    @SubscribeEvent
    @Optional.Method(modid = "eiramoticons")
    public void reloadEmoticons(ReloadEmoticons event) {
        subscriberBadgeMap.clear();
        casterBadge = EiraMoticonsAPI.registerEmoticon("EiraIRC:casterBadge", this);
        casterBadge.setManualOnly(true);
        casterBadge.setLoadData(new ResourceLocation("eirairc", "gfx/casterBadge.png"));
        casterBadge.setCustomTooltip(new String[]{I19n.format("eirairc:addons.twitch.channelBroadcaster")});
        modBadge = EiraMoticonsAPI.registerEmoticon("EiraIRC:modBadge", this);
        modBadge.setManualOnly(true);
        modBadge.setLoadData(new ResourceLocation("eirairc", "gfx/modBadge.png"));
        modBadge.setCustomTooltip(new String[]{I19n.format("eirairc:addons.twitch.channelModerator")});
        turboBadge = EiraMoticonsAPI.registerEmoticon("EiraIRC:turboBadge", this);
        turboBadge.setManualOnly(true);
        turboBadge.setLoadData(new ResourceLocation("eirairc", "gfx/turboBadge.png"));
        turboBadge.setCustomTooltip(new String[]{I19n.format("eirairc:addons.twitch.turbo")});
    }

    @Override
    @Optional.Method(modid = "eiramoticons")
    public void loadEmoticonImage(IEmoticon emoticon) {
        if (emoticon.getLoadData() instanceof ResourceLocation) {
            try {
                IResource resource = Minecraft.getMinecraft().getResourceManager().getResource((ResourceLocation) emoticon.getLoadData());
                if (resource != null) {
                    BufferedImage image = ImageIO.read(resource.getInputStream());
                    if (image != null) {
                        emoticon.setImage(image);
                        emoticon.setScale(0.5f, 0.5f);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (emoticon.getLoadData() instanceof URL) {
            try {
                BufferedImage image = ImageIO.read((URL) emoticon.getLoadData());
                if (image != null) {
                    emoticon.setImage(image);
                    emoticon.setScale(0.5f, 0.5f);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
