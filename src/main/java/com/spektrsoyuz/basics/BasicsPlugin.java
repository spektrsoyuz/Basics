package com.spektrsoyuz.basics;

import com.spektrsoyuz.basics.command.TeleportCommand;
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

        if (configVersion != BasicsUtils.CONFIG_VERSION) {
            getComponentLogger().error("Config version {} does not match required version {}, disabling plugin", configVersion, BasicsUtils.CONFIG_VERSION);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.dataController.initialize();
        this.playerController.initialize();

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

            new TeleportCommand(this).register(registrar);
        });
    }

    private void registerListeners() {
        // Register listener classes
    }

    private void registerTasks() {
        // Register Bukkit tasks
    }
}
