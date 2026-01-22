package com.holograms.commands.subcommands;

import com.holograms.config.HologramsConfig;
import com.holograms.storages.HologramsStorage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Set;
import java.util.UUID;

public class RemoveHologramCommand extends AbstractPlayerCommand {

    @Nonnull
    private final RequiredArg<Integer> idArg =
            withRequiredArg("id", "Hologram id", ArgTypes.INTEGER);

    public RemoveHologramCommand() {
        super("remove", "Remove hologram by ID");
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> refEntityStore,
            @NonNullDecl PlayerRef playerRef,
            @NonNullDecl World world
    ) {
        CommandSender commandSender = commandContext.sender();
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(Message.raw("The command can only be used by a player").color(Color.RED));
            return;
        }

        Integer hologramId = idArg.get(commandContext);
        var hologram = HologramsStorage.getById(hologramId);
        if(hologram == null){
            player.sendMessage(Message.raw("Hologram not found").color(Color.RED));
            return;
        }

        for (HologramsConfig.Hologram.HologramLine line : hologram.getLineUuids()) {

            EntityStore entityStore = world.getEntityStore();

            Ref<EntityStore> ref = entityStore.getRefFromUUID(line.getUuid());
            if (ref == null) {
                continue;
            }

            store.removeEntity(ref, RemoveReason.REMOVE);
        }

        HologramsStorage.remove(hologramId);
        HologramsStorage.save();

        player.sendMessage(Message.raw("Hologram successfully deleted").color(Color.GREEN));
    }
}
