package com.spektrsoyuz.basics.controller;

import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.model.PluginConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
public class ConfigController {

    private final BasicsPlugin plugin;
    private PluginConfig config;
    private CommentedConfigurationNode messages;

    // Load config files
    public void load() {
        config = loadConfig();
        messages = loadNode("messages.conf");
    }

    // Populate PluginConfig object from configurate node
    @SneakyThrows
    private PluginConfig loadConfig() {
        final var node = loadNode("config.conf");
        if (node != null) {
            return node.get(PluginConfig.class);
        }
        return null;
    }

    // Get a configurate node from a config file path
    private CommentedConfigurationNode loadNode(final String path) {
        final File file = new File(plugin.getDataFolder(), path);
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder().path(file.toPath()).build();

        if (!file.exists()) {
            plugin.saveResource(path, false); // save file if not found
        }

        try {
            return loader.load();
        } catch (final IOException ex) {
            plugin.getComponentLogger().error("Failed to load config file: {}", ex.getMessage());
            return null;
        }
    }

    // Get the config version
    public boolean checkVersion(final int current) {
        return config.version() == current;
    }

    // Get an adventure component from a message key and add tag resolvers
    public Component message(final String key, final TagResolver... resolvers) {
        final String message = messages.node(key).getString();

        return message != null
                ? MiniMessage.miniMessage().deserialize(message, resolvers)
                : Component.text(key);
    }
}
