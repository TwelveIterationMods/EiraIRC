// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config;

import com.google.gson.JsonObject;

public class TrustedServer {

    private final String address;
    private boolean allowRedirect;
    private boolean redirectSolo;

    public TrustedServer(String address) {
        this.address = address;
    }

    public static TrustedServer loadFromJson(JsonObject object) {
        TrustedServer server = new TrustedServer(object.get("address").getAsString());
        if(object.has("allowRedirect")) {
            server.allowRedirect = object.get("allowRedirect").getAsBoolean();
        }
        if(object.has("redirectSolo")) {
            server.redirectSolo = object.get("redirectSolo").getAsBoolean();
        }
        return server;
    }

    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        object.addProperty("address", address);
        object.addProperty("allowRedirect", allowRedirect);
        object.addProperty("redirectSolo", redirectSolo);
        return object;
    }

    public String getAddress() {
        return address;
    }

    public boolean isAllowRedirect() {
        return allowRedirect;
    }

    public boolean isRedirectSolo() {
        return redirectSolo;
    }

    public void setAllowRedirect(boolean allowRedirect) {
        this.allowRedirect = allowRedirect;
    }
}
