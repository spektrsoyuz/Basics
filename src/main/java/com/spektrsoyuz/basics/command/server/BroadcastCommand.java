package com.spektrsoyuz.basics.command.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.spektrsoyuz.basics.BasicsPlugin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class BroadcastCommand {

    private final BasicsPlugin plugin;

    // Register the command
    public void register(final Commands registrar) {
        final var command = Commands.literal("broadcast")
                .requires(stack -> stack.getSender().hasPermission("basics.command.broadcast"))
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(this::broadcast))
                .build();

        registrar.register(command, "Broadcast a message to the server", List.of("say", "announce", "bc"));
    }

    // Broadcast a message
    private int broadcast(final CommandContext<CommandSourceStack> context) {
        final String message = context.getArgument("message", String.class)
                .replace("\\t", "   ");

        plugin.getServer().broadcast(plugin.configController().message("command-broadcast",
                Placeholder.parsed("message", message)));

        return Command.SINGLE_SUCCESS;
    }
}
