package com.spektrsoyuz.basics;

import com.spektrsoyuz.basics.command.server.BroadcastCommand;
import com.spektrsoyuz.basics.controller.ConfigController;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;

@Getter
@Accessors(fluent = true)
@SuppressWarnings("UnstableApiUsage")
public final class BasicsPlugin extends JavaPlugin {

    private final ConfigController configController = new ConfigController(this);

    @Override
    public void onLoad() {
        // Plugin load logic
        configController.load();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        boolean correctVersion = configController.checkVersion(BasicsUtils.CONFIG_VERSION);
        if (!correctVersion) {
            getComponentLogger().error("Config file is out of date! Version {} is required.", BasicsUtils.CONFIG_VERSION);
            getServer().getPluginManager().disablePlugin(this);
        }

        registerListeners();
        registerCommands();
        registerLinks();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerListeners() {

    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands registrar = event.registrar();

            new BroadcastCommand(this).register(registrar);
        });
    }

    private void registerLinks() {

    }

    private void registerLink(final Component name, final String url) {
        getServer().getServerLinks().addLink(name, URI.create(url));
    }
}
