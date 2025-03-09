package com.spektrsoyuz.basics.controller;

import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.model.PluginConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ConfigController {

    private final BasicsPlugin plugin;
    private PluginConfig config;
    private CommentedConfigurationNode messages;

    // Load config files
    @SneakyThrows
    public void load() {
        final CommentedConfigurationNode configNode = loadNode("config.conf");
        try {
            config = configNode != null
                    ? configNode.get(PluginConfig.class)
                    : null;
        } catch (final SerializationException ex) {
            throw new RuntimeException(ex);
        }
        messages = loadNode("messages.conf");
    }

    // Get a configurate node from a file path
    private CommentedConfigurationNode loadNode(final String path) {
        final File file = new File(plugin.getDataFolder(), path);

        if (!file.exists()) {
            plugin.getComponentLogger().debug("File {} not found, creating it", path);
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
        return config.version() == current;
    }

    // Get the command prefix
    public String getPrefix() {
        return messages.node("prefix").getString();
    }

    // Get an adventure component from a message key and add tag resolvers
    public void message(final Audience audience, final String key, final TagResolver... resolvers) {
        final String message = messages.node(key).getString();

        final TagResolver[] combinedResolvers = Stream.concat(
                Arrays.stream(resolvers),
                Stream.of(Placeholder.parsed("prefix", getPrefix()))
        ).toArray(TagResolver[]::new);

        audience.sendMessage(message != null
                ? MiniMessage.miniMessage().deserialize(message, combinedResolvers)
                : Component.text(key));
    }
}
