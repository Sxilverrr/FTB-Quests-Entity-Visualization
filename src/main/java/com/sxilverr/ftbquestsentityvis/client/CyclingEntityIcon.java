package com.sxilverr.ftbquestsentityvis.client;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public class CyclingEntityIcon extends Icon {
    private final TagKey<EntityType<?>> tag;
    private final EntityVisSettings settings;
    private final QuestObject owner;

    private List<EntityIcon> cachedIcons;
    private Level cachedLevel;
    private boolean cacheValid;

    public CyclingEntityIcon(TagKey<EntityType<?>> tag, EntityVisSettings settings, QuestObject owner) {
        this.tag = tag;
        this.settings = settings.copy();
        this.owner = owner;
    }

    public static List<ResourceLocation> entitiesIn(TagKey<EntityType<?>> tag) {
        if (tag == null) {
            return List.of();
        }
        return BuiltInRegistries.ENTITY_TYPE.getTag(tag)
                .map(set -> set.stream().map(holder -> BuiltInRegistries.ENTITY_TYPE.getKey(holder.value())).toList())
                .orElse(List.of());
    }

    private List<EntityIcon> getIcons() {
        Level level = Minecraft.getInstance().level;
        if (cacheValid && cachedLevel == level) {
            return cachedIcons;
        }
        List<EntityIcon> built = entitiesIn(tag).stream().map(id -> new EntityIcon(id, settings, owner)).toList();
        if (!built.isEmpty() || cachedIcons == null) {
            cachedIcons = built;
        }
        cachedLevel = level;
        cacheValid = true;
        return cachedIcons;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int w, int h) {
        List<EntityIcon> icons = getIcons();
        if (!icons.isEmpty()) {
            icons.get(EntityIcon.cycleIndex(settings.cycleSeconds, icons.size())).draw(graphics, x, y, w, h);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CyclingEntityIcon other && other.tag.location().equals(tag.location()) && other.settings.equals(settings);
    }

    @Override
    public int hashCode() {
        return tag.location().hashCode() * 31 + settings.hashCode();
    }

    @Override
    public String toString() {
        return "entity_tag:" + tag.location() + settings.nbt.trim();
    }
}
