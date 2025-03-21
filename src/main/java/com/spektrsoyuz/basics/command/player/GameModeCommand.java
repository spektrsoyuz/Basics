package com.spektrsoyuz.basics.command.player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.BasicsUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
                                .executes(context -> {
                                    final var resolver = context.getArgument("players", PlayerSelectorArgumentResolver.class);
                                    return gamemode(context, resolver.resolve(context.getSource()));
                                }))
                        .executes(context -> {
                            final CommandSender sender = context.getSource().getSender();
                            if (sender instanceof Player player) {
                                return gamemode(context, List.of(player));
                            }

                            this.plugin.getConfigController().sendMessage(sender, "error-sender-not-player");
                            return 0;
                        }))
                .build();

        registrar.register(command, "Set your game mode", List.of("gm"));
    }

    // Sets the gamemode of a player
    private int gamemode(final CommandContext<CommandSourceStack> context, final List<Player> players) {
        final CommandSender sender = context.getSource().getSender();
        final GameMode gameMode = context.getArgument("gameMode", GameMode.class);

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
