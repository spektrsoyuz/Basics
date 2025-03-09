package com.spektrsoyuz.basics.command.player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.spektrsoyuz.basics.BasicsPlugin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class PingCommand {

    private final BasicsPlugin plugin;

    // Register the command
    public void register(final Commands registrar) {
        final var command = Commands.literal("ping")
                .requires(stack -> stack.getSender().hasPermission("basics.command.ping"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .requires(stack -> stack.getSender().hasPermission("basics.command.ping.other"))
                        .executes(this::pingOther))
                .executes(this::ping)
                .build();

        registrar.register(command, "View your latency", List.of("latency", "pong"));
    }

    private int ping(final CommandContext<CommandSourceStack> context) {
        final CommandSender sender = context.getSource().getSender();

        if (sender instanceof Player player) {
            sender.sendMessage(plugin.configController().message("command-ping-self",
                    Placeholder.parsed("ping", String.valueOf(player.getPing()))));
        } else {
            sender.sendMessage(plugin.configController().message("sender-not-player"));
        }

        return Command.SINGLE_SUCCESS;
    }

    private int pingOther(final CommandContext<CommandSourceStack> context) {
        final var resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);

        try {
            final Player player = resolver.resolve(context.getSource()).getFirst();

            context.getSource().getSender().sendMessage(plugin.configController().message("command-ping-other",
                    Placeholder.parsed("player", player.getName()),
                    Placeholder.parsed("ping", String.valueOf(player.getPing()))));
        } catch (final CommandSyntaxException ex) {
            context.getSource().getSender().sendMessage(Component.text(ex.getMessage(), NamedTextColor.RED));
        }

        return Command.SINGLE_SUCCESS;
    }
}
