package com.spektrsoyuz.basics.command.misc;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.BasicsUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class BasicsCommand {

    private final BasicsPlugin plugin;

    public void register(final Commands registrar) {
        final var command = Commands.literal("basics")
                .requires(stack -> stack.getSender().hasPermission(BasicsUtils.PERMISSION_COMMAND_BASICS))
                .then(Commands.literal("reload")
                        .executes(this::reload))
                .build();

        registrar.register(command, "Manage the plugin");
    }

    private int reload(final CommandContext<CommandSourceStack> context) {
        if (plugin.configController().reload()) {
            plugin.configController().message(context.getSource().getSender(), "command-basics-reload");
        } else {
            plugin.configController().message(context.getSource().getSender(), "command-basics-reload-error");
        }

        return Command.SINGLE_SUCCESS;
    }
}
