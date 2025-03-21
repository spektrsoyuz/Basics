package com.spektrsoyuz.basics.controller;

import com.mojang.brigadier.Command;
import com.spektrsoyuz.basics.BasicsPlugin;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
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
    public int setGameMode(final CommandSender sender, final GameMode gameMode, final List<Player> players) {
        players.forEach(player -> {
            player.setGameMode(gameMode);

            this.plugin.getConfigController().sendMessage(player, "command-gamemode-changed-self",
                    Placeholder.component("gamemode", Component.translatable(gameMode)));

            if (!sender.equals(player)) {
                this.plugin.getConfigController().sendMessage(sender, "command-gamemode-changed-other",
                        Placeholder.component("gamemode", Component.translatable(gameMode)),
                        Placeholder.parsed("player", player.getName()));
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
