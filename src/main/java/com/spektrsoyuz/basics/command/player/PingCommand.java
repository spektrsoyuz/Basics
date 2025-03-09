package com.spektrsoyuz.basics.command.player;

import com.mojang.brigadier.Command;
import com.spektrsoyuz.basics.BasicsPlugin;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class PingCommand extends PlayerCommand {

    // Constructor
    public PingCommand(final BasicsPlugin plugin) {
        super(plugin);
    }

    // Register the command
    public void register(final Commands registrar) {
        final var command = create("ping", "basics.command.ping", "basics.command.ping.other");
        registrar.register(command, "Pong!", List.of("latency"));
    }

    @Override
    protected int execute(final CommandSender sender, final Player player) {
        if (sender != player) {
            sender.sendMessage(plugin.configController().message("command-ping-other",
                    Placeholder.parsed("player", player.getName()),
                    Placeholder.parsed("ping", String.valueOf(player.getPing()))));
        } else {
            player.sendMessage(plugin.configController().message("command-ping-self",
                    Placeholder.parsed("ping", String.valueOf(player.getPing()))));
        }
        return Command.SINGLE_SUCCESS;
    }
}
