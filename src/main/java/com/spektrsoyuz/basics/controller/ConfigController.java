package com.spektrsoyuz.basics.controller;

import com.spektrsoyuz.basics.BasicsPlugin;
import lombok.RequiredArgsConstructor;
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
    private CommentedConfigurationNode config;
    private CommentedConfigurationNode messages;

    public void load() {
        config = loadNode("config.conf");
        messages = loadNode("messages.conf");
    }

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

    public boolean checkVersion(final int current) {
        return config.node("version").getInt(0) == current;
    }

    public Component message(final String key, final TagResolver... resolvers) {
        final String message = messages.node(key).getString();

        return message != null
                ? MiniMessage.miniMessage().deserialize(message, resolvers)
                : Component.text(key);
    }
}
