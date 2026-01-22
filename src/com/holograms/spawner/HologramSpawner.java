package com.holograms.spawner;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.UUID;
import java.util.function.Consumer;

public class HologramSpawner {

    public static UUID spawnLine(World world, Transform playerTransform, String text, float yOffset) {
        return buildLine(
                world,
                playerTransform,
                text,
                yOffset,
                holder -> world.getEntityStore()
                        .getStore()
                        .addEntity(holder, AddReason.SPAWN)
        );
    }


    public static UUID spawnLine(CommandBuffer<EntityStore> commandBuffer, World world, Transform playerTransform, String text, float yOffset
    ) {
        return buildLine(
                world,
                playerTransform,
                text,
                yOffset,
                holder -> commandBuffer.addEntity(holder, AddReason.SPAWN)
        );
    }


    private static UUID buildLine(World world, Transform playerTransform, String text, float yOffset, Consumer<Holder<EntityStore>> adder
    ) {
        Holder<EntityStore> holder = createBaseHolder();

        applyTransform(holder, playerTransform, yOffset);

        if (!initProjectile(holder)) {
            return null;
        }

        addNetworkAndNameplate(world, holder, text);

        adder.accept(holder);

        UUIDComponent uuidComponent =
                holder.getComponent(UUIDComponent.getComponentType());

        assert uuidComponent != null;
        return uuidComponent.getUuid();
    }


    private static Holder<EntityStore> createBaseHolder() {
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();

        holder.putComponent(
                ProjectileComponent.getComponentType(),
                new ProjectileComponent("Projectile")
        );

        holder.ensureComponent(UUIDComponent.getComponentType());


        return holder;
    }

    private static void applyTransform(Holder<EntityStore> holder, Transform playerTransform, float yOffset
    ) {
        var pos = playerTransform.getPosition().clone();
        pos.y += yOffset;

        holder.putComponent(
                TransformComponent.getComponentType(),
                new TransformComponent(pos, playerTransform.getRotation().clone())
        );
    }

    private static boolean initProjectile(Holder<EntityStore> holder) {
        ProjectileComponent projectile = holder.getComponent(ProjectileComponent.getComponentType());
        if (projectile == null) return false;

        if (projectile.getProjectile() == null) {
            projectile.initialize();
        }

        return projectile.getProjectile() != null;
    }

    private static void addNetworkAndNameplate(World world, Holder<EntityStore> holder, String text
    ) {
        int networkId = world.getEntityStore()
                .getStore()
                .getExternalData()
                .takeNextNetworkId();

        holder.addComponent(NetworkId.getComponentType(), new NetworkId(networkId));
        holder.addComponent(Nameplate.getComponentType(), new Nameplate(text));
    }
}
