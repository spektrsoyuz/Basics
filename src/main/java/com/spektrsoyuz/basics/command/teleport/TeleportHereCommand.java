/*
 * Basics Plugin
 *
 * Copyright (c) 2025 SpektrSoyuz
 * All Rights Reserves
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
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

// Command class for the /teleporthere command
@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class TeleportHereCommand {

    private final BasicsPlugin plugin;

    // Registers the command
    public void register(final Commands registrar) {
        final var command = Commands.literal("teleporthere")
                .requires(stack -> stack.getSender().hasPermission(BasicsUtils.PERMISSION_COMMAND_TELEPORTHERE))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(this::teleportHere))
                .build();

        registrar.register(command, "Teleport a player to your location", List.of("tphere", "tph"));
    }

    // Teleports a target player to the sender
    private int teleportHere(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final CommandSender sender = context.getSource().getSender();

        if (sender instanceof Player player) {
            final Player target = context.getArgument("player", PlayerSelectorArgumentResolver.class)
                    .resolve(context.getSource()).getFirst();

            target.teleportAsync(player.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);

            // Send message to command sender
            this.plugin.getConfigController().sendMessage(player, "command-teleport-player-to-sender");

            // Send message to target player
            if (player != target) {
                this.plugin.getConfigController().sendMessage(target, "command-teleport-recipient");
            }
            return Command.SINGLE_SUCCESS;
        } else {
            this.plugin.getConfigController().sendMessage(sender, "error-sender-not-player");
            return 0;
        }
    }
}
