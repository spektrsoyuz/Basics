package com.spektrsoyuz.basics.controller;

import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.BasicsUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
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
    private HoconConfigurationLoader configLoader;
    private HoconConfigurationLoader messagesLoader;
    private CommentedConfigurationNode config;
    private CommentedConfigurationNode messages;

    // Load config files
    public boolean load() {
        configLoader = createLoader("config.conf");
        messagesLoader = createLoader("messages.conf");

        config = loadNode(configLoader);
        messages = loadNode(messagesLoader);

        return config != null && messages != null;
    }

    // Create a configurate loader from a file path
    private HoconConfigurationLoader createLoader(final String path) {
        final File file = new File(plugin.getDataFolder(), path);

        if (!file.exists()) {
            plugin.getComponentLogger().debug("File {} not found, creating it", path);
            plugin.saveResource(path, false); // save file if not found
        }

        return HoconConfigurationLoader.builder()
                .path(file.toPath())
                .prettyPrinting(true)
                .build();
    }

    // Get a configurate node from a file path
    private CommentedConfigurationNode loadNode(final HoconConfigurationLoader loader) {
        try {
            return loader.load();
        } catch (final IOException ex) {
            plugin.getComponentLogger().error("Failed to load config file: {}", ex.getMessage());
            return null;
        }
    }

    // Get the config version
    public boolean checkVersion(final int current) {
        return config.node("version").getInt(BasicsUtils.CONFIG_VERSION) == current;
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

    // Save the configuration
    public void save() {
        try {
            configLoader.save(config);
            messagesLoader.save(messages);
        } catch (final IOException ex) {
            plugin.getComponentLogger().error("Failed to save config file: {}", ex.getMessage());
        }
    }
}
