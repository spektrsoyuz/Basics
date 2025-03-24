package com.spektrsoyuz.basics.command.player;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.BasicsUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

// Command class for the /gamemode command
@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class GameModeCommand {

    private final BasicsPlugin plugin;

    // Registers the command
    public void register(final Commands registrar) {
        final var command = Commands.literal("gamemode")
                .requires(stack -> stack.getSender().hasPermission(BasicsUtils.PERMISSION_COMMAND_GAMEMODE))
                .then(Commands.argument("gamemode", ArgumentTypes.gameMode())
                        .then(Commands.argument("players", ArgumentTypes.players())
                                .requires(stack -> stack.getSender().hasPermission(BasicsUtils.PERMISSION_COMMAND_GAMEMODE_OTHER))
                                .executes(this::gamemodeOther))
                        .executes(this::gamemodeSelf))
                .build();

        registrar.register(command, "Set your gamemode", List.of("gm"));
    }

    // Sets the gamemode of a target player
    private int gamemodeOther(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final CommandSender sender = context.getSource().getSender();
        final var resolver = context.getArgument("players", PlayerSelectorArgumentResolver.class);
        final GameMode gameMode = context.getArgument("gamemode", GameMode.class);

        return this.plugin.getPlayerController().setGameMode(sender, gameMode, resolver.resolve(context.getSource()));
    }

    // Sets the gamemode of the sender
    private int gamemodeSelf(final CommandContext<CommandSourceStack> context) {
        final CommandSender sender = context.getSource().getSender();
        final GameMode gameMode = context.getArgument("gamemode", GameMode.class);

        if (sender instanceof Player player) {
            return this.plugin.getPlayerController().setGameMode(sender, gameMode, List.of(player));
        }

        this.plugin.getConfigController().sendMessage(sender, "error-sender-not-player");
        return 0;
    }
}
