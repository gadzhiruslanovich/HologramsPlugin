package com.holograms.storages;

import com.holograms.config.HologramsConfig;
import com.holograms.config.HologramsConfig.Hologram;
import com.holograms.config.HologramsConfig.Settings;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;

import java.nio.file.Path;
import java.util.*;

public final class HologramsStorage {

    private static HologramsConfig CONFIG;
    private static Path CONFIG_PATH;

    public static void init(Path configPath) {
        CONFIG = new HologramsConfig().load(configPath);
        CONFIG_PATH = configPath;

        if (CONFIG == null || CONFIG.getHolograms() == null) {
            throw new IllegalStateException("Failed to load holograms config");
        }
    }

    public static void save() {
        ensureInitialized();
        CONFIG.save(CONFIG_PATH);
    }

    public static Settings getSettings() {
        return CONFIG.getSettings();
    }

    public static List<Hologram> getAll() {
        ensureInitialized();
        return CONFIG.getHolograms();
    }

    public static Hologram getById(int hologramId) {
        ensureInitialized();

        for (Hologram hologram : CONFIG.getHolograms()) {
            if (hologram.getHologramId() == hologramId) {
                return hologram;
            }
        }
        return null;
    }

    public static int getNextHologramId() {
        ensureInitialized();

        int maxId = 0;
        for (Hologram hologram : CONFIG.getHolograms()) {
            int id = hologram.getHologramId();
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId + 1;
    }

    public static void setLineUuids(int hologramId, String world, List<Hologram.HologramLine> lines, Transform transform) {
        ensureInitialized();

        Hologram hologram = getOrCreate(hologramId);

        hologram.setWorldName(world);

        Vector3d position = transform.getPosition();
        hologram.setX(position.x);
        hologram.setY(position.y);
        hologram.setZ(position.z);

        if (lines == null || lines.isEmpty()) {
            hologram.setLineUuids(new ArrayList<>());
            return;
        }

        List<Hologram.HologramLine> hologramLines = new ArrayList<>();

        for (Hologram.HologramLine entry : lines) {
            UUID uuid = entry.getUuid();
            String text = entry.getText();

            if (uuid == null) continue;
            if (text == null) continue;

            var hologramLine = new Hologram.HologramLine();
            hologramLine.setUuid(uuid);
            hologramLine.setText(text);

            hologramLines.add(hologramLine);
        }

        hologram.setLineUuids(hologramLines);
    }

    public static void remove(int hologramId) {
        ensureInitialized();

        Iterator<Hologram> it = CONFIG.getHolograms().iterator();
        while (it.hasNext()) {
            if (it.next().getHologramId() == hologramId) {
                it.remove();
                return;
            }
        }
    }

    private static void ensureInitialized() {
        if (CONFIG == null) {
            throw new IllegalStateException("HologramsStorage not initialized");
        }
    }

    private static Hologram getOrCreate(int hologramId) {
        Hologram hologram = getById(hologramId);
        if (hologram != null) {
            return hologram;
        }

        hologram = new Hologram();
        hologram.setHologramId(hologramId);
        hologram.setLineUuids(new ArrayList<>(0));

        CONFIG.getHolograms().add(hologram);
        return hologram;
    }
}
