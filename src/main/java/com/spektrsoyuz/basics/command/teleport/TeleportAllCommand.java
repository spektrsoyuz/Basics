/*
 * Basics Plugin
 *
 * Copyright (c) 2025 SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.basics.command.teleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.BasicsUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.math.FinePosition;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

// Command class for the /teleporthere command
@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class TeleportAllCommand {

    private final BasicsPlugin plugin;

    // Registers the command
    public void register(final Commands registrar) {
        final var finePositionArgument = Commands.argument("finePosition", ArgumentTypes.finePosition())
                .executes(this::teleportToPosition);

        final var playerArgument = Commands.argument("player", ArgumentTypes.player())
                .executes(this::teleportToPlayer);

        final var command = Commands.literal("teleportall")
                .requires(stack -> stack.getSender().hasPermission(BasicsUtils.PERMISSION_COMMAND_TELEPORTALL))
                .then(finePositionArgument)
                .then(playerArgument)
                .build();

        registrar.register(command, "Teleport all players to a location", List.of("tpall"));
    }

    // Teleports all online players to a fine position
    private int teleportToPosition(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final CommandSender sender = context.getSource().getSender();

        if (sender instanceof Player player) {
            final FinePosition finePosition = context.getArgument("finePosition", FinePositionResolver.class)
                    .resolve(context.getSource());
            final Location location = finePosition.toLocation(player.getWorld());

            // Send message to command sender
            this.plugin.getConfigController().sendMessage(player, "command-teleport-all-to-position",
                    this.plugin.getPlayerController().getFinePositionResolvers(player, location));

            for (final Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
                if (onlinePlayer == player) continue;
                onlinePlayer.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND);

                // Send message to all online players
                this.plugin.getConfigController().sendMessage(onlinePlayer, "command-teleport-recipient");
            }
            return Command.SINGLE_SUCCESS;
        } else {
            this.plugin.getConfigController().sendMessage(sender, "error-sender-not-player");
            return 0;
        }
    }

    // Teleports all online players to a target player
    private int teleportToPlayer(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final CommandSender sender = context.getSource().getSender();

        if (sender instanceof Player player) {
            final Player target = context.getArgument("player", PlayerSelectorArgumentResolver.class)
                    .resolve(context.getSource()).getFirst();
            final Location location = target.getLocation();

            // Send message to command sender
            this.plugin.getConfigController().sendMessage(player, "command-teleport-all-to-player",
                    Placeholder.parsed("target", target.getName()));

            for (final Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
                if (onlinePlayer == player) continue;
                onlinePlayer.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND);

                // Send message to all online players
                this.plugin.getConfigController().sendMessage(onlinePlayer, "command-teleport-recipient");
            }
            return Command.SINGLE_SUCCESS;
        } else {
            this.plugin.getConfigController().sendMessage(sender, "error-sender-not-player");
            return 0;
        }
    }
}
