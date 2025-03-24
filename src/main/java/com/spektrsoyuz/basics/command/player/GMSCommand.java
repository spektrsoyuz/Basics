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

// Command class for the /gms command
@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class GMSCommand {

    private final BasicsPlugin plugin;

    // Registers the command
    public void register(final Commands registrar) {
        final var command = Commands.literal("gms")
                .requires(stack -> stack.getSender().hasPermission(BasicsUtils.PERMISSION_COMMAND_GAMEMODE))
                .then(Commands.argument("players", ArgumentTypes.players())
                        .requires(stack -> stack.getSender().hasPermission(BasicsUtils.PERMISSION_COMMAND_GAMEMODE_OTHER))
                        .executes(this::gamemodeOther))
                .executes(this::gamemodeSelf)
                .build();

        registrar.register(command, "Set your game mode to Survival");
    }

    // Sets the gamemode of a target player
    private int gamemodeOther(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final CommandSender sender = context.getSource().getSender();
        final var resolver = context.getArgument("players", PlayerSelectorArgumentResolver.class);

        return this.plugin.getPlayerController().setGameMode(sender, GameMode.SURVIVAL, resolver.resolve(context.getSource()));
    }

    // Sets the gamemode of the sender
    private int gamemodeSelf(final CommandContext<CommandSourceStack> context) {
        final CommandSender sender = context.getSource().getSender();

        if (sender instanceof Player player) {
            return this.plugin.getPlayerController().setGameMode(sender, GameMode.SURVIVAL, List.of(player));
        }

        this.plugin.getConfigController().sendMessage(sender, "error-sender-not-player");
        return 0;
    }
}
