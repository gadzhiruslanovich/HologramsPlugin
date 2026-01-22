package com.holograms.commands;

import com.holograms.commands.subcommands.CreateHologramCommand;
import com.holograms.commands.subcommands.ListHologramCommand;
import com.holograms.commands.subcommands.RemoveHologramCommand;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class HologramCommand extends AbstractAsyncCommand {

    public HologramCommand() {
        super("hologram", "Commands for working with holograms");

        addAliases("hg", "ft", "floatingtext");
        addSubCommand(new CreateHologramCommand());
        addSubCommand(new RemoveHologramCommand());
        addSubCommand(new ListHologramCommand());
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext commandContext) {

        CommandSender commandSender = commandContext.sender();
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(Message.raw("The command can only be used by a player").color(Color.RED));
            return CompletableFuture.completedFuture(null);
        }

        player.sendMessage(Message.raw("/hologram create <text> - Create hologram text"));
        player.sendMessage(Message.raw("/hologram remove <id> - Remove hologram by ID"));
        player.sendMessage(Message.raw("/hologram list - Get a list of holograms"));

        return CompletableFuture.completedFuture(null);
    }
}
