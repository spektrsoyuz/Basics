package com.spektrsoyuz.basics.command.player;

import com.mojang.brigadier.Command;
import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.BasicsUtils;
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
        final var command = create("ping", BasicsUtils.PERMISSION_COMMAND_PING, BasicsUtils.PERMISSION_COMMAND_PING_OTHER);
        registrar.register(command, "Pong!", List.of("latency"));
    }

    @Override
    protected int execute(final CommandSender sender, final Player player) {
        if (sender != player) {
            plugin.configController().message(sender, "command-ping-other",
                    Placeholder.parsed("player", player.getName()),
                    Placeholder.parsed("ping", String.valueOf(player.getPing())));
        } else {
            plugin.configController().message(player, "command-ping-self",
                    Placeholder.parsed("ping", String.valueOf(player.getPing())));
        }
        return Command.SINGLE_SUCCESS;
    }
}
