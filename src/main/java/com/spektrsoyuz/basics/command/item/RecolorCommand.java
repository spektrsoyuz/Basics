package com.spektrsoyuz.basics.command.item;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.BasicsUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

// Command class for the /rename command
@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class RecolorCommand {

    private final BasicsPlugin plugin;

    // Registers the command
    public void register(final Commands registrar) {
        final var command = Commands.literal("recolor")
                .requires(stack -> stack.getSender().hasPermission(BasicsUtils.PERMISSION_COMMAND_RECOLOR))
                .then(Commands.argument("color", StringArgumentType.greedyString())
                        .executes(this::recolor))
                .build();

        registrar.register(command, "Rename an item");
    }

    // Renames a held item
    private int recolor(final CommandContext<CommandSourceStack> context) {
        final CommandSender sender = context.getSource().getSender();

        // Check if sender is a player
        if (sender instanceof Player player) {
            final String colorString = StringArgumentType.getString(context, "color");
            final TextColor textColor = TextColor.fromHexString(colorString);

            // Check if hex color is valid
            if (textColor != null) {
                final Color color = Color.fromRGB(textColor.value());
                final ItemStack item = player.getInventory().getItemInMainHand();

                // Check if item is leather armor
                if (!(item.getItemMeta() instanceof LeatherArmorMeta)) {
                    this.plugin.getConfigController().sendMessage(player, "command-recolor-invalid-item",
                            Placeholder.component("item", item.displayName()));
                    return 0;
                }

                // Set color of item
                item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(color, true));

                // Send a message to the sender
                this.plugin.getConfigController().sendMessage(sender, "command-recolor-success",
                        Placeholder.component("color", Component.text(colorString, TextColor.fromHexString(colorString))));
                return Command.SINGLE_SUCCESS;
            }

            this.plugin.getConfigController().sendMessage(sender, "command-recolor-invalid-color",
                    Placeholder.parsed("color", colorString));
        } else {
            this.plugin.getConfigController().sendMessage(sender, "error-sender-not-player");
        }
        return 0;
    }
}
