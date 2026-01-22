package com.holograms.commands.subcommands;

import com.holograms.config.HologramsConfig;
import com.holograms.consts.HologramConstants;
import com.holograms.spawner.HologramSpawner;
import com.holograms.storages.HologramsStorage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CreateHologramCommand extends AbstractPlayerCommand {

    @Nonnull
    private final RequiredArg<String> textArg =
            withRequiredArg("text", "Hologram text", ArgTypes.STRING);

    public CreateHologramCommand() {
        super("create", "Create hologram text");
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext,
            @NonNullDecl Store<EntityStore> entityStore,
            @NonNullDecl Ref<EntityStore> refEntityStore,
            @NonNullDecl PlayerRef playerRef,
            @NonNullDecl World world
    ) {
        CommandSender commandSender = commandContext.sender();
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(Message.raw("The command can only be used by a player").color(Color.RED));
            return;
        }

        String text = textArg.get(commandContext).trim();
        if (text.isEmpty()) {
            player.sendMessage(Message.raw("Hologram text cannot be empty").color(Color.RED));
            return;
        }

        String[] lines = text.split("#");
        Transform transform = playerRef.getTransform();

        int hologramId = HologramsStorage.getNextHologramId();

        world.execute(() -> {
            List<HologramsConfig.Hologram.HologramLine> lineUuids = new ArrayList<>(lines.length);

            for (int i = 0; i < lines.length; i++) {

                var lineText = lines[i].replace("'", "");
                UUID lineUuid = HologramSpawner.spawnLine(
                        world,
                        transform,
                        lineText,
                        HologramConstants.BASE_Y - (i * HologramConstants.LINE_STEP)
                );

                if (lineUuid != null) {
                    var hologramLine = new HologramsConfig.Hologram.HologramLine();
                    hologramLine.setUuid(lineUuid);
                    hologramLine.setText(lineText);

                    lineUuids.add(hologramLine);
                }
            }

            HologramsStorage.setLineUuids(hologramId, world.getName(), lineUuids, transform);
            HologramsStorage.save();

            player.sendMessage(Message.raw("Hologram successfully created with Id: " + hologramId).color(Color.GREEN));
        });
    }

}
