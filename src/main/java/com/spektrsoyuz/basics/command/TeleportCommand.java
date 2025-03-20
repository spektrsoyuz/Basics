package com.spektrsoyuz.basics.command;

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
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

// Command class for the /teleport command
@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class TeleportCommand {

    private final BasicsPlugin plugin;

    // Registers the command
    public void register(final Commands registrar) {
        final var finePositionArgument = Commands.argument("finePosition", ArgumentTypes.finePosition())
                .executes(this::teleportToPosition);

        final var playerArgument = Commands.argument("player", ArgumentTypes.player())
                .then(Commands.argument("finePosition", ArgumentTypes.finePosition())
                        .executes(this::teleportPlayerToPosition))
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes(this::teleportPlayerToPlayer))
                .executes(this::teleportToPlayer);

        final var command = Commands.literal("teleport")
                .requires(stack -> stack.getSender().hasPermission(BasicsUtils.PERMISSION_COMMAND_TELEPORT))
                .then(finePositionArgument)
                .then(playerArgument)
                .build();

        registrar.register(command, "Teleport to a location", List.of("tp"));
    }

    // Teleports the sender to a fine position
    public int teleportToPosition(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final CommandSender sender = context.getSource().getSender();

        if (sender instanceof Player player) {
            final FinePosition finePosition = context.getArgument("finePosition", FinePositionResolver.class)
                    .resolve(context.getSource());
            final Location location = finePosition.toLocation(player.getWorld());

            player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND);

            // Send message to command sender
            this.plugin.getConfigController().sendMessage(player, "command-teleport-position",
                    getFinePositionResolvers(player, location));
            return Command.SINGLE_SUCCESS;
        } else {
            this.plugin.getConfigController().sendMessage(sender, "error-player-not-sender");
            return 0;
        }
    }

    // Teleports the sender to a player
    public int teleportToPlayer(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final CommandSender sender = context.getSource().getSender();

        if (sender instanceof Player player) {
            final Player target = context.getArgument("player", PlayerSelectorArgumentResolver.class)
                    .resolve(context.getSource()).getFirst();

            player.teleportAsync(target.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);

            // Send message to command sender
            this.plugin.getConfigController().sendMessage(sender, "command-teleport-player",
                    Placeholder.parsed("target", target.getName()));

            // Send message to target player
            if (!player.equals(target)) {
                this.plugin.getConfigController().sendMessage(target, "command-teleport-observer",
                        Placeholder.parsed("player", player.getName()));
            }
            return Command.SINGLE_SUCCESS;
        } else {
            this.plugin.getConfigController().sendMessage(sender, "error-player-not-sender");
            return 0;
        }
    }

    // Teleports a player to a fine position
    public int teleportPlayerToPosition(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final CommandSender sender = context.getSource().getSender();

        final FinePosition finePosition = context.getArgument("finePosition", FinePositionResolver.class)
                .resolve(context.getSource());
        final Player target = context.getArgument("player", PlayerSelectorArgumentResolver.class)
                .resolve(context.getSource()).getFirst();
        final Location location = finePosition.toLocation(target.getWorld());

        target.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND);

        // Send message to command sender
        this.plugin.getConfigController().sendMessage(sender, "command-teleport-player-to-position",
                getFinePositionResolvers(target, location));

        // Send message to teleport recipient player
        if (!(sender instanceof Player player && player.equals(target))) {
            this.plugin.getConfigController().sendMessage(target, "command-teleport-recipient");
        }
        return Command.SINGLE_SUCCESS;
    }

    // Teleports a player to a target player
    public int teleportPlayerToPlayer(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final CommandSender sender = context.getSource().getSender();

        final Player player = context.getArgument("player", PlayerSelectorArgumentResolver.class)
                .resolve(context.getSource()).getFirst();
        final Player target = context.getArgument("target", PlayerSelectorArgumentResolver.class)
                .resolve(context.getSource()).getFirst();

        player.teleportAsync(target.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        this.plugin.getConfigController().sendMessage(sender, "command-teleport-player-to-player",
                Placeholder.parsed("player", player.getName()),
                Placeholder.parsed("target", target.getName()));

        // Send message to teleport recipient player
        if (!(sender instanceof Player senderPlayer && senderPlayer.equals(target))) {
            this.plugin.getConfigController().sendMessage(player, "command-teleport-recipient");

            // Send message to target player
            if (!player.equals(target)) {
                this.plugin.getConfigController().sendMessage(target, "command-teleport-observer",
                        Placeholder.parsed("player", player.getName()));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    // Get the tag resolvers for a fine position teleport message
    private TagResolver[] getFinePositionResolvers(final Player player, final Location location) {
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
