package com.spektrsoyuz.basics;

import com.spektrsoyuz.basics.controller.ConfigController;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
