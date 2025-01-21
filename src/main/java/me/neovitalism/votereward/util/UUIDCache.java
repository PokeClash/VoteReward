package me.neovitalism.votereward.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDCache {
    private static final Map<String, UUID> PLAYER_UUID_CACHE = new HashMap<>();

    public static UUID getUUIDFromUsername(String username) {
        return PLAYER_UUID_CACHE.computeIfAbsent(username, name -> {
            try (InputStream is = URI.create("https://api.mojang.com/users/profiles/minecraft/" + name).toURL().openStream()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                JsonObject json = JsonParser.parseReader(rd).getAsJsonObject();
                return UUID.fromString(json.get("id").getAsString().replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                        "$1-$2-$3-$4-$5"));
            } catch (IOException ignored) {}
            return null;
        });
    }

    public static void cacheUUID(String playerName, UUID playerUUID) {
        UUIDCache.PLAYER_UUID_CACHE.put(playerName, playerUUID);
    }
}
