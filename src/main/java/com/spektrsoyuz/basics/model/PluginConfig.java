package com.spektrsoyuz.basics.model;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
@Accessors(fluent = true)
@ConfigSerializable
public class PluginConfig {

    @Comment("Config version. Do not change.")
    private final int version = 1;
}
