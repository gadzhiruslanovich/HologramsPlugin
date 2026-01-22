package com.holograms.systems;

import com.holograms.config.HologramsConfig;
import com.holograms.config.HologramsConfig.Settings;
import com.holograms.config.HologramsConfig.Hologram;
import com.holograms.consts.HologramConstants;
import com.holograms.spawner.HologramSpawner;
import com.holograms.storages.HologramsStorage;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.*;

public class ReloadHologramsSystem extends EntityTickingSystem<EntityStore> {

    private long nextReloadAtNanos = 0L;

    @Override
    public void tick(
            float deltaSeconds,
            int tickIndex,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> entityStore,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        Settings settings = HologramsStorage.getSettings();
        int reloadIntervalSeconds = settings.getReloadIntervalSeconds();

        if (reloadIntervalSeconds <= 0) {
            return;
        }

        long now = System.nanoTime();

        if (nextReloadAtNanos == 0L) {
            nextReloadAtNanos = now + secondsToNanos(reloadIntervalSeconds);
            return;
        }

        if (now < nextReloadAtNanos) {
            return;
        }

        long interval = secondsToNanos(reloadIntervalSeconds);
        do {
            nextReloadAtNanos += interval;
        } while (now >= nextReloadAtNanos);

        reloadAllHolograms(commandBuffer);
    }

    private static long secondsToNanos(int seconds) {
        return (long) seconds * 1_000_000_000L;
    }

    private void reloadAllHolograms(CommandBuffer<EntityStore> commandBuffer) {
        Universe universe = Universe.get();

        for (Hologram hologram : HologramsStorage.getAll()) {

            var oldUuids = hologram.getLineUuids();
            if (oldUuids == null || oldUuids.isEmpty()) {
                continue;
            }

            World world = universe.getWorld(hologram.getWorldName());
            if (world == null) {
                continue;
            }

            for (Hologram.HologramLine hologramLine : oldUuids) {
                if (hologramLine == null) continue;

                Ref<EntityStore> ref = world.getEntityRef(hologramLine.getUuid());
                if (ref != null) {
                    commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
                }
            }

            List<Hologram.HologramLine> newHologramLines = new ArrayList<>();

            Transform baseTransform =
                    new Transform(hologram.getX(), hologram.getY(), hologram.getZ());

            int lineIndex = 0;

            for (Hologram.HologramLine hologramLine : oldUuids) {

                float yOffset = HologramConstants.BASE_Y - (lineIndex * HologramConstants.LINE_STEP);

                UUID spawned = HologramSpawner.spawnLine(commandBuffer, world, baseTransform, hologramLine.getText(), yOffset);
                if (spawned != null) {
                    var newHologramLine = new Hologram.HologramLine();
                    newHologramLine.setUuid(spawned);
                    newHologramLine.setText(hologramLine.getText());
                    newHologramLines.add(newHologramLine);
                }

                lineIndex++;
            }

            hologram.setLineUuids(newHologramLines);
            HologramsStorage.save();
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Nameplate.getComponentType());
    }
}