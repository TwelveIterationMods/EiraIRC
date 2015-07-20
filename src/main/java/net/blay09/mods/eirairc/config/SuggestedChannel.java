// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config;

import com.google.gson.JsonObject;

public class SuggestedChannel {

	private String server;
	private String channel;
	private String description;
	private String modpackId;
	private String modpackName;
	private boolean modpackExclusive;
	private boolean recommended;
	private int score;

	public boolean isRecommended() {
		return recommended;
	}

	public int getScore() {
		return score;
	}

	public boolean isModpackExclusive() {
		return modpackExclusive;
	}

	public String getModpackId() {
		return modpackId;
	}

	public void calculateScore(String modpackId) {
		score = 0;
		if(recommended) { // Recommended channels get bonus points
			score += 10;
		}
		if(!this.modpackId.isEmpty()) {
			// Modpack-specific channels get a bonus point over general ones
			score++;
			if(!this.modpackId.equals(modpackId)) {
				if (modpackExclusive) {
					// If this channel is targeting a different modpack, and is modpack-exclusive, put it at the very bottom
					score = Integer.MIN_VALUE;
				} else {
					// The modpack does not match, but they welcome outsiders, so only take one point
					score--;
				}
			} else {
				// Modpack matches, add a point
				score++;
			}
		}
	}

	public static SuggestedChannel loadFromJson(JsonObject object) {
		SuggestedChannel channel = new SuggestedChannel();
		channel.server = object.get("server").getAsString();
		channel.channel = object.get("channel").getAsString();
		channel.description = object.has("description") ? object.get("description").getAsString() : "(no description set)";
		channel.modpackId = object.has("modpack-id") ? object.get("modpack-id").getAsString() : "";
		channel.modpackName = object.has("modpack-display") ? object.get("modpack-display").getAsString() : "";
		channel.modpackExclusive = object.has("modpack-exclusive") && object.get("modpack-exclusive").getAsBoolean();
		channel.recommended = object.has("recommended") && object.get("recommended").getAsBoolean();
		return channel;
	}

	public String getChannelName() {
		return channel;
	}

	public String getServerName() {
		return server;
	}

	public String getModpackName() {
		return modpackName;
	}

	public String getDescription() {
		return description;
	}
}
