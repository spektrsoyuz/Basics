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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
