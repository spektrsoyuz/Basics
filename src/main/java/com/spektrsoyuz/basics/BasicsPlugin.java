package com.spektrsoyuz.basics;

import com.spektrsoyuz.basics.command.item.RecolorCommand;
import com.spektrsoyuz.basics.command.item.RenameCommand;
import com.spektrsoyuz.basics.command.player.*;
import com.spektrsoyuz.basics.command.teleport.TeleportAllCommand;
import com.spektrsoyuz.basics.command.teleport.TeleportCommand;
import com.spektrsoyuz.basics.command.teleport.TeleportHereCommand;
import com.spektrsoyuz.basics.controller.ConfigController;
import com.spektrsoyuz.basics.controller.DataController;
import com.spektrsoyuz.basics.controller.PlayerController;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@SuppressWarnings("UnstableApiUsage")
public final class BasicsPlugin extends JavaPlugin {

    private final ConfigController configController = new ConfigController(this);
    private final DataController dataController = new DataController(this);
    private final PlayerController playerController = new PlayerController(this);

    @Override
    public void onLoad() {
        // Plugin load logic
        this.configController.initialize();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        final int configVersion = this.configController.getVersion();

        // Verify config version
        if (configVersion != BasicsUtils.CONFIG_VERSION) {
            getComponentLogger().error("Config version {} does not match required version {}, disabling plugin", configVersion, BasicsUtils.CONFIG_VERSION);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize controllers
        this.dataController.initialize();
        this.playerController.initialize();

        // Register features
        registerCommands();
        registerListeners();
        registerTasks();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        // Register Paper commands
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands registrar = event.registrar();

            // Item commands
            new RecolorCommand(this).register(registrar);
            new RenameCommand(this).register(registrar);

            // Player commands
            new GameModeCommand(this).register(registrar);
            new GMACommand(this).register(registrar);
            new GMCCommand(this).register(registrar);
            new GMSCommand(this).register(registrar);
            new GMSPCommand(this).register(registrar);

            // Teleport commands
            new TeleportAllCommand(this).register(registrar);
            new TeleportCommand(this).register(registrar);
            new TeleportHereCommand(this).register(registrar);
        });
    }

    private void registerListeners() {
        // Register listener classes
    }

    private void registerTasks() {
        // Register Bukkit tasks
    }
}
