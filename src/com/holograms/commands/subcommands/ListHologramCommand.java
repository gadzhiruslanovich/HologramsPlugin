package com.holograms.commands.subcommands;

import com.holograms.storages.HologramsStorage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;
import java.util.stream.Collectors;

public class ListHologramCommand extends AbstractPlayerCommand {

    public ListHologramCommand() {
        super("list", "Get a list of holograms");
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

        var holograms = HologramsStorage.getAll();

        String ids = holograms.stream()
                .map(h -> String.valueOf(h.getHologramId()))
                .collect(Collectors.joining(", ", "[", "]"));

        player.sendMessage(Message.raw("Holograms: " + ids) );
    }
}