/*
 * Basics Plugin
 *
 * Copyright (c) 2025 SpektrSoyuz
 * All Rights Reserves
 */
package com.spektrsoyuz.basics.controller;

import com.spektrsoyuz.basics.BasicsPlugin;
import io.github.miniplaceholders.api.MiniPlaceholders;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Controller class for managing configuration files
@RequiredArgsConstructor
public final class ConfigController {

    private final BasicsPlugin plugin;
    private CommentedConfigurationNode configNode;
    private CommentedConfigurationNode messagesNode;

    // Initializes the controller
    public void initialize() {
        this.configNode = createNode("config.conf");
        this.messagesNode = createNode("messages.conf");
    }

    // Creates a HoconConfigurationLoader for the given file path
    private HoconConfigurationLoader createLoader(final Path path) {
        return HoconConfigurationLoader.builder()
                .path(path)
                .prettyPrinting(true)
                .build();
    }

    // Creates a CommentedConfigurationNode for the given file
    private CommentedConfigurationNode createNode(final String file) {
        final Path path = this.plugin.getDataPath().resolve(file);

        if (!Files.exists(path)) {
            this.plugin.getComponentLogger().info("Config file {} not found, creating it", file);
            this.plugin.saveResource(file, false);
        }
        final var loader = createLoader(path);

        try {
            return loader.load();
        } catch (final ConfigurateException ex) {
            this.plugin.getComponentLogger().error("Failed to load config file: {}", file, ex);
            return null;
        }
    }

    // Retrieves the version number from the config
    public int getVersion() {
        return this.configNode.node("version").getInt();
    }

    // Retrieves the command prefix from the message config
    public @NotNull String getPrefix() {
        final String prefix = this.messagesNode.node("prefix").getString();
        return prefix != null ? prefix : "";
    }

    // Retrieves a message from the message config
    public @NotNull Component getMessage(final String key, final TagResolver... resolvers) {
        final String message = this.messagesNode.node(key).getString();

        final List<TagResolver> tagResolvers = new ArrayList<>(List.of(resolvers));
        tagResolvers.add(Placeholder.parsed("prefix", getPrefix()));

        if (this.plugin.getServer().getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            tagResolvers.add(MiniPlaceholders.getGlobalPlaceholders());
        }

        return message != null
                ? MiniMessage.miniMessage().deserialize(message, tagResolvers.toArray(TagResolver[]::new))
                : Component.text(key);
    }

    // Sends a message to an audience from the message config
    public void sendMessage(final Audience audience, final String key, final TagResolver... resolvers) {
        final String message = this.messagesNode.node(key).getString();

        final List<TagResolver> tagResolvers = new ArrayList<>(List.of(resolvers));
        tagResolvers.add(Placeholder.parsed("prefix", getPrefix()));

        if (this.plugin.getServer().getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            tagResolvers.add(MiniPlaceholders.getGlobalPlaceholders());
            tagResolvers.add(MiniPlaceholders.getAudiencePlaceholders(audience));
        }

        audience.sendMessage(message != null
                ? MiniMessage.miniMessage().deserialize(message, tagResolvers.toArray(new TagResolver[0]))
                : Component.text(key));
    }
}
