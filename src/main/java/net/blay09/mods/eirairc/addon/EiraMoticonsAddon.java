package net.blay09.mods.eirairc.addon;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eiramoticons.api.EiraMoticonsAPI;
import net.blay09.mods.eiramoticons.api.IEmoticon;
import net.blay09.mods.eiramoticons.api.IEmoticonLoader;
import net.blay09.mods.eiramoticons.api.ReloadEmoticons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Optional.Interface(modid = "eiramoticons", iface = "net.blay09.mods.eiramoticons.api.IEmoticonLoader")
public class EiraMoticonsAddon implements IEmoticonLoader {

	public static final String TWITCH_EMOTES_API = "https://twitchemotes.com/api_cache/v2/subscriber.json";

	private static EiraMoticonsAddon instance;
	public static final Map<IRCChannel, IEmoticon> subscriberBadgeMap = new HashMap<IRCChannel, IEmoticon>();
	public static IEmoticon casterBadge;
	public static IEmoticon modBadge;

	public EiraMoticonsAddon() {
		Compatibility.eiraMoticonsInstalled = true;
		MinecraftForge.EVENT_BUS.register(this);
		instance = this;
	}

	public static String getSubscriberBadgeString(IRCChannel channel) {
		IEmoticon subBadge = getSubscriberBadge(channel);
		if(subBadge != null) {
			return subBadge.getChatString();
		}
		return "";
	}

	@Optional.Method(modid = "eiramoticons")
	public static IEmoticon getSubscriberBadge(IRCChannel channel) {
		IEmoticon subBadge = null;
		if(!subscriberBadgeMap.containsKey(channel)) {
			Gson gson = new Gson();
			try {
				URL url = new URL(TWITCH_EMOTES_API);
				InputStream in = url.openStream();
				Reader reader = new InputStreamReader(in);
				JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
				JsonObject jsonChannels = jsonObject.getAsJsonObject("channels");
				JsonObject jsonChannel = jsonChannels.getAsJsonObject(channel.getName().substring(1).toLowerCase());
				if(jsonChannel != null) {
					subBadge = EiraMoticonsAPI.registerEmoticon("EiraIRC:subBadge" + jsonChannel.get("title").getAsString(), instance);
					subBadge.setManualOnly(true);
					subBadge.setLoadData(new URL("http:" + jsonChannel.get("badge").getAsString()));
					subBadge.setCustomTooltip(new String[] { I19n.format("eirairc:twitch.channelSubscriber") });
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			subscriberBadgeMap.put(channel, subBadge);
		} else {
			subBadge = subscriberBadgeMap.get(channel);
		}
		return subBadge;
	}

	@SubscribeEvent
	@Optional.Method(modid = "eiramoticons")
	public void reloadEmoticons(ReloadEmoticons event) {
		subscriberBadgeMap.clear();
		casterBadge = EiraMoticonsAPI.registerEmoticon("EiraIRC:casterBadge", this);
		casterBadge.setManualOnly(true);
		casterBadge.setLoadData(new ResourceLocation("eirairc", "gfx/casterBadge.png"));
		casterBadge.setCustomTooltip(new String[] {I19n.format("eirairc:twitch.channelBroadcaster") });
		modBadge = EiraMoticonsAPI.registerEmoticon("EiraIRC:modBadge", this);
		modBadge.setManualOnly(true);
		modBadge.setLoadData(new ResourceLocation("eirairc", "gfx/modBadge.png"));
		modBadge.setCustomTooltip(new String[] {I19n.format("eirairc:twitch.channelModerator") });
	}

	@Override
	@Optional.Method(modid = "eiramoticons")
	public void loadEmoticonImage(IEmoticon emoticon) {
		if(emoticon.getLoadData() instanceof ResourceLocation) {
			try {
				IResource resource = Minecraft.getMinecraft().getResourceManager().getResource((ResourceLocation) emoticon.getLoadData());
				if(resource != null) {
					BufferedImage image = ImageIO.read(resource.getInputStream());
					if(image != null) {
						emoticon.setImage(image);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(emoticon.getLoadData() instanceof URL) {
			try {
				BufferedImage image = ImageIO.read((URL) emoticon.getLoadData());
				if(image != null) {
					emoticon.setImage(image);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
