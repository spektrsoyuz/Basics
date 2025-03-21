package com.spektrsoyuz.basics;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class BasicsUtils {

    // Constants
    public static final int CONFIG_VERSION = 1;

    // Permission nodes
    public static final String PERMISSION_COMMAND_TELEPORTALL = "basics.command.teleportall";
    public static final String PERMISSION_COMMAND_TELEPORT = "basics.command.teleport";
    public static final String PERMISSION_COMMAND_TELEPORTHERE = "basics.command.teleporthere";

    // Get the tag resolvers for a fine position teleport message
    public static TagResolver[] getFinePositionResolvers(final Player player, final Location location) {
        return new TagResolver[]{
                Placeholder.parsed("player", player.getName()),
                Placeholder.parsed("x", String.valueOf(location.getX())),
                Placeholder.parsed("y", String.valueOf(location.getY())),
                Placeholder.parsed("z", String.valueOf(location.getZ())),
                Placeholder.parsed("yaw", String.valueOf(location.getYaw())),
                Placeholder.parsed("pitch", String.valueOf(location.getPitch())),
                Placeholder.parsed("world", location.getWorld().getName())
        };
    }
}
