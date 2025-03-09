package com.spektrsoyuz.basics.controller;

import com.spektrsoyuz.basics.BasicsPlugin;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ConfigController {

    private final BasicsPlugin plugin;
    private CommentedConfigurationNode config;
    private CommentedConfigurationNode messages;

    // Load config files
    @SneakyThrows
    public void load() {
        config = loadNode("config.conf");
        messages = loadNode("messages.conf");
    }

    // Get a configurate node from a config file path
    private CommentedConfigurationNode loadNode(final String path) {
        final File file = new File(plugin.getDataFolder(), path);

        if (!file.exists()) {
            plugin.saveResource(path, false); // save file if not found
        }

        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(file.toPath())
                .build();

        try {
            return loader.load();
        } catch (final IOException ex) {
            plugin.getComponentLogger().error("Failed to load config file: {}", ex.getMessage());
            return null;
        }
    }

    // Get the config version
    public boolean checkVersion(final int current) {
        return config.node("version").getInt(0) == current;
    }

    public String getPrefix() {
        return messages.node("prefix").getString();
    }

    // Get an adventure component from a message key and add tag resolvers
    public Component message(final String key, final TagResolver... resolvers) {
        final String message = messages.node(key).getString();

        final TagResolver[] combinedResolvers = Stream.concat(
                Arrays.stream(resolvers),
                Stream.of(Placeholder.parsed("prefix", getPrefix()))
        ).toArray(TagResolver[]::new);

        return message != null
                ? MiniMessage.miniMessage().deserialize(message, combinedResolvers)
                : Component.text(key);
    }
}
