package com.spektrsoyuz.basics.command.item;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.BasicsUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// Command class for the /rename command
@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class RenameCommand {

    private final BasicsPlugin plugin;

    // Registers the command
    public void register(final Commands registrar) {
        final var command = Commands.literal("rename")
                .requires(stack -> stack.getSender().hasPermission(BasicsUtils.PERMISSION_COMMAND_RENAME))
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(this::rename))
                .build();

        registrar.register(command, "Rename an item");
    }

    // Renames a held item
    private int rename(final CommandContext<CommandSourceStack> context) {
        final CommandSender sender = context.getSource().getSender();

        // Check if sender is a player
        if (sender instanceof Player player) {
            final String name = StringArgumentType.getString(context, "name");
            final ItemStack item = player.getInventory().getItemInMainHand();
            final Component displayName = MiniMessage.miniMessage().deserialize(name);

            // Set the name of the item
            item.setData(DataComponentTypes.CUSTOM_NAME, displayName);
            player.getInventory().setItemInMainHand(item);

            // Send a message to the sender
            this.plugin.getConfigController().sendMessage(sender, "command-rename-success");
            return Command.SINGLE_SUCCESS;
        }

        this.plugin.getConfigController().sendMessage(sender, "error-sender-not-player");
        return 0;
    }
}
