package com.sxilverr.ftbquestsentityvis.duck;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public interface IEntityVis {
    EntityVisSettings ftbquestsentityvis$vis();

    default ResourceLocation ftbquestsentityvis$visEntity() {
        return null;
    }

    default TagKey<EntityType<?>> ftbquestsentityvis$visTag() {
        return null;
    }
}
