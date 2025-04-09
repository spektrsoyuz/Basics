/*
 * Basics Plugin
 *
 * Copyright (c) 2025 SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.basics.controller;

import com.mojang.brigadier.Command;
import com.spektrsoyuz.basics.BasicsPlugin;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

// Controller class for managing player data
@RequiredArgsConstructor
public final class PlayerController {

    private final BasicsPlugin plugin;

    // Initializes the controller
    public void initialize() {

    }

    // Sets the gamemode of a player
    public int setGameMode(final Audience audience, final GameMode gameMode, final List<Player> players) {
        players.forEach(player -> {
            player.setGameMode(gameMode);

            this.plugin.getConfigController().sendMessage(player, "command-gamemode-self",
                    Placeholder.component("gamemode", Component.translatable(gameMode)));

            if (!audience.equals(player)) {
                this.plugin.getConfigController().sendMessage(audience, "command-gamemode-other",
                        Placeholder.component("gamemode", Component.translatable(gameMode)),
                        Placeholder.parsed("player", player.getName()));
            }
        });

        return Command.SINGLE_SUCCESS;
    }

    // Get the tag resolvers for a fine position teleport message
    public TagResolver[] getFinePositionResolvers(final Player player, final Location location) {
        return new TagResolver[]{
                Placeholder.parsed("player", player.getName()),
                Placeholder.parsed("x", String.valueOf(location.getX())),
                Placeholder.parsed("y", String.valueOf(location.getY())),
                Placeholder.parsed("z", String.valueOf(location.getZ())),
                Placeholder.parsed("yaw", String.valueOf(location.getYaw())),
                Placeholder.parsed("pitch", String.valueOf(location.getPitch())),
                Placeholder.parsed("world", location.getWorld().getName())
        };
    }
}
