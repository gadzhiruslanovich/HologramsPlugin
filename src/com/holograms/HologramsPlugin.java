package com.holograms;

import com.holograms.commands.HologramCommand;
import com.holograms.storages.HologramsStorage;
import com.holograms.systems.ReloadHologramsSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.nio.file.Path;
import java.util.logging.Level;

public class HologramsPlugin extends JavaPlugin {

    private HytaleLogger.Api _logger;

    public HologramsPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();

        Path configPath = Path.of("mods/HologramsPlugin/config.json");
        HologramsStorage.init(configPath);

        this.getCommandRegistry().registerCommand(new HologramCommand());

        this.getEntityStoreRegistry().registerSystem(new ReloadHologramsSystem());

        _logger = this.getLogger().at(Level.INFO);
        _logger.log("Plugin loaded successfully!");
    }

    @Override
    protected void shutdown() {
        super.shutdown();

        HologramsStorage.save();
        _logger.log("Plugin disabled successfully!");
    }
}