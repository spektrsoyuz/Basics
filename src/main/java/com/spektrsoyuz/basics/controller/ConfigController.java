package com.spektrsoyuz.basics.controller;

import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.BasicsUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ConfigController {

    private final BasicsPlugin plugin;
    private CommentedConfigurationNode config;
    private CommentedConfigurationNode messages;

    // Load config files
    public boolean reload() {
        config = loadNode("config.conf");
        messages = loadNode("messages.conf");

        return config != null && messages != null;
    }

    // Save config files
    public void save() {
        save(config, "config.conf");
        save(messages, "messages.conf");
    }

    private HoconConfigurationLoader createLoader(final Path file) {
        return HoconConfigurationLoader.builder()
                .prettyPrinting(true)
                .defaultOptions(opts -> opts.shouldCopyDefaults(true))
                .path(file)
                .build();
    }

    private CommentedConfigurationNode loadNode(final String fileName) {
        final Path file = plugin.getDataPath().resolve(fileName);

        if (!file.toFile().exists()) {
            plugin.getComponentLogger().debug("File {} not found, creating it", fileName);
            plugin.saveResource(fileName, false); // save file if not found
        }

        final HoconConfigurationLoader loader = createLoader(file);

        try {
            return loader.load();
        } catch (final ConfigurateException ex) {
            plugin.getComponentLogger().error("Failed to load file {}", fileName, ex);
            return null;
        }
    }

    // Save the configuration
    private void save(final CommentedConfigurationNode node, final String path) {
        final var loader = createLoader(plugin.getDataPath().resolve(path));

        try {
            loader.save(node);
        } catch (final IOException ex) {
            plugin.getComponentLogger().error("Failed to save config file {}", path, ex);
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
}
