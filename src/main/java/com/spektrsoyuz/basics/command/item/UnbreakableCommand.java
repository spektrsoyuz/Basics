package com.spektrsoyuz.basics.command.item;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.spektrsoyuz.basics.BasicsPlugin;
import com.spektrsoyuz.basics.BasicsUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// Command class for the /unbreakable command
@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class UnbreakableCommand {

    private final BasicsPlugin plugin;

    // Registers the command
    public void register(final Commands registrar) {
        final var command = Commands.literal("unbreakable")
                .requires(stack -> stack.getSender().hasPermission(BasicsUtils.PERMISSION_COMMAND_UNBREAKABLE))
                .then(Commands.argument("showInToolTip", BoolArgumentType.bool())
                        .executes(context -> unbreakable(context, context.getArgument("showInToolTip", Boolean.class))))
                .executes(context -> unbreakable(context, true))
                .build();

        registrar.register(command, "Toggle unbreakable for an item");
    }

    // Toggles unbreakable for a held item
    private int unbreakable(final CommandContext<CommandSourceStack> context, boolean showInToolTip) {
        final CommandSender sender = context.getSource().getSender();

        // Check if sender is a player
        if (sender instanceof Player player) {
            final ItemStack item = player.getInventory().getItemInMainHand();

            // Check if the item is unbreakable
            if (item.hasData(DataComponentTypes.UNBREAKABLE)) {
                item.unsetData(DataComponentTypes.UNBREAKABLE);
                this.plugin.getConfigController().sendMessage(sender, "command-unbreakable-remove",
                        Placeholder.component("item", item.displayName()));
            } else {
                item.setData(DataComponentTypes.UNBREAKABLE);

                final TooltipDisplay tooltipDisplay = item.getData(DataComponentTypes.TOOLTIP_DISPLAY);
                if (tooltipDisplay != null && showInToolTip) {
                    tooltipDisplay.hiddenComponents().add(DataComponentTypes.UNBREAKABLE);
                }
                this.plugin.getConfigController().sendMessage(sender, "command-unbreakable-add",
                        Placeholder.component("item", item.displayName()));
            }

            player.getInventory().setItemInMainHand(item);
            return Command.SINGLE_SUCCESS;
        } else {
            this.plugin.getConfigController().sendMessage(sender, "error-sender-not-player");
        }
        return 0;
    }
}
