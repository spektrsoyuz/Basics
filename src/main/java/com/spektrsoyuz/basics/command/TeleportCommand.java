package com.spektrsoyuz.basics.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.spektrsoyuz.basics.BasicsPlugin;
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

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class TeleportCommand {

    private final BasicsPlugin plugin;

    // Registers the command
    public void register(final Commands registrar) {
        final var command = Commands.literal("teleport")
                .requires(stack -> stack.getSender().hasPermission("basics.command.teleport"))
                .then(Commands.argument("finePosition", ArgumentTypes.finePosition())
                        .executes(this::teleportToPosition))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .then(Commands.argument("finePosition", ArgumentTypes.finePosition())
                                .executes(this::teleportPlayerToPosition))
                        .then(Commands.argument("target", ArgumentTypes.player())
                                .executes(this::teleportPlayerToPlayer))
                        .executes(this::teleportToPlayer))
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

            this.plugin.getConfigController().sendMessage(player, "command-teleport-position",
                    Placeholder.parsed("x", String.valueOf(location.getX())),
                    Placeholder.parsed("y", String.valueOf(location.getY())),
                    Placeholder.parsed("z", String.valueOf(location.getZ())));
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

            this.plugin.getConfigController().sendMessage(sender, "command-teleport-player",
                    Placeholder.parsed("target", target.getName()));
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
        final Player player = context.getArgument("player", PlayerSelectorArgumentResolver.class)
                .resolve(context.getSource()).getFirst();
        final Location location = finePosition.toLocation(player.getWorld());

        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND);
        this.plugin.getConfigController().sendMessage(sender, "command-teleport-player-to-position",
                Placeholder.parsed("x", String.valueOf(location.getX())),
                Placeholder.parsed("y", String.valueOf(location.getY())),
                Placeholder.parsed("z", String.valueOf(location.getZ())),
                Placeholder.parsed("yaw", String.valueOf(location.getYaw())),
                Placeholder.parsed("pitch", String.valueOf(location.getPitch())),
                Placeholder.parsed("world", location.getWorld().getName()));
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
        return Command.SINGLE_SUCCESS;
    }
}
