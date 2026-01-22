package com.holograms.config;

import com.google.gson.*;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HologramsConfig {

    private static final Logger LOGGER = Logger.getLogger("HologramsPlugin");

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    private Settings settings = new Settings();

    private final List<Hologram> holograms = new ArrayList<>();

    public static class Hologram {

        private int hologramId;

        private String worldName;

        private double x;
        private double y;
        private double z;

        private List<HologramLine> lineUuids;

        public static class HologramLine {
            private UUID uuid;
            private String text;

            public void setUuid(UUID uuid) {
                this.uuid = uuid;
            }

            public void setText(String text) {
                this.text = text;
            }

            public UUID getUuid(){
                return uuid;
            }

            public String getText(){
                return text;
            }
        }

        public int getHologramId() {
            return hologramId;
        }

        public List<HologramLine> getLineUuids(){
            return lineUuids;
        }

        public String getWorldName(){ return worldName; }

        public void setHologramId(int hologramId) {
            this.hologramId = hologramId;
        }

        public void setLineUuids(List<HologramLine> lineUuids) { this.lineUuids = lineUuids; }

        public void setWorldName(String world) { this.worldName = world; }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }

    }

    public static class Settings {

        private final int reloadIntervalSeconds;

        public Settings() {
            reloadIntervalSeconds = 600;
        }

        public int getReloadIntervalSeconds() {
            return reloadIntervalSeconds;
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public List<Hologram> getHolograms() {
        return holograms;
    }

    public HologramsConfig load(Path configPath) {
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);

                JsonObject root = JsonParser.parseString(json).getAsJsonObject();

                JsonElement settingsEl = root.get("settings");
                if (settingsEl == null || settingsEl.isJsonNull()) {
                    root.add("settings", GSON.toJsonTree(new Settings()));
                }

                JsonElement hologramsEl = root.get("holograms");
                if (hologramsEl == null || hologramsEl.isJsonNull()) {
                    root.add("holograms", new JsonArray());
                } else if (hologramsEl.isJsonObject()) {
                    JsonObject obj = hologramsEl.getAsJsonObject();
                    JsonArray arr = new JsonArray();
                    if (!obj.isEmpty()) {
                        arr.add(obj);
                    }
                    root.add("holograms", arr);
                } else if (!hologramsEl.isJsonArray()) {
                    root.add("holograms", new JsonArray());
                }

                HologramsConfig cfg = GSON.fromJson(root, HologramsConfig.class);
                if (cfg == null) cfg = new HologramsConfig();

                if (cfg.settings == null) cfg.settings = new Settings();
                if (cfg.getHolograms() == null) {
                    LOGGER.warning("Config holograms list is null; creating empty list.");
                }

                LOGGER.info("Config loaded from " + configPath);

                cfg.save(configPath);
                return cfg;

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load config, using defaults", e);
            }
        }

        HologramsConfig cfg = new HologramsConfig();
        cfg.save(configPath);

        LOGGER.info("Created default config at " + configPath);
        return cfg;
    }

    public void save(Path configPath) {
        try {
            Files.createDirectories(configPath.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
            String json = GSON.toJson(this);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save config", e);
        }
    }
}
