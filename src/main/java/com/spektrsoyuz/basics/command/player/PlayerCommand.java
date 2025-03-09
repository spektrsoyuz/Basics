package com.spektrsoyuz.basics.command.player;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.spektrsoyuz.basics.BasicsPlugin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public abstract class PlayerCommand {

    protected final BasicsPlugin plugin;
    private static final SimpleCommandExceptionType SENDER_NOT_PLAYER_EX = new SimpleCommandExceptionType(() -> "Specify a player.");

    // Create the command node
    public LiteralCommandNode<CommandSourceStack> create(final String name, final String permission, final String permissionOther) {
        return Commands.literal(name)
                .requires(stack -> stack.getSender().hasPermission(permission))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .requires(stack -> stack.getSender().hasPermission(permissionOther))
                        .executes(context -> {
                            final PlayerSelectorArgumentResolver players = context.getArgument("player", PlayerSelectorArgumentResolver.class);
                            return execute(context.getSource().getSender(), players.resolve(context.getSource()).getFirst());
                        }))
                .executes(context -> execute(context.getSource().getSender()))
                .build();
    }

    // Run the command
    protected int execute(final CommandSender sender) throws CommandSyntaxException {
        if (sender instanceof Player player) return execute(sender, player);
        throw SENDER_NOT_PLAYER_EX.create();
    }

    protected abstract int execute(final CommandSender sender, final Player player);
}