package com.sxilverr.ftbquestsentityvis.client;

import com.sxilverr.ftbquestsentityvis.duck.OverrideMode;
import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public class CyclingEntityIcon extends Icon {
    private final TagKey<EntityType<?>> tag;
    private final float sizeMultiplier;
    private final float offsetX;
    private final float offsetY;
    private final float rotationOffset;
    private final OverrideMode spinMode;
    private final OverrideMode idleMode;
    private final OverrideMode walkMode;
    private final BooleanSupplier silhouetteCheck;
    private final String nbt;
    private final float cycleSeconds;

    private List<EntityIcon> cachedIcons;
    private Level cachedLevel;
    private boolean cacheValid;

    public CyclingEntityIcon(TagKey<EntityType<?>> tag, float sizeMultiplier, float offsetX, float offsetY,
                             float rotationOffset, OverrideMode spinMode, OverrideMode idleMode, OverrideMode walkMode,
                             BooleanSupplier silhouetteCheck, String nbt, float cycleSeconds) {
        this.tag = tag;
        this.sizeMultiplier = sizeMultiplier;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.rotationOffset = rotationOffset;
        this.spinMode = spinMode;
        this.idleMode = idleMode;
        this.walkMode = walkMode;
        this.silhouetteCheck = silhouetteCheck;
        this.nbt = nbt == null ? "" : nbt.trim();
        this.cycleSeconds = cycleSeconds;
    }

    public static List<ResourceLocation> entitiesIn(TagKey<EntityType<?>> tag) {
        if (tag == null) {
            return Collections.emptyList();
        }
        List<ResourceLocation> ids = new ArrayList<>();
        BuiltInRegistries.ENTITY_TYPE.getTag(tag).ifPresent(set -> set.forEach(holder -> {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(holder.value());
            if (id != null) {
                ids.add(id);
            }
        }));
        return ids;
    }

    private List<EntityIcon> getIcons() {
        Level level = Minecraft.getInstance().level;
        if (cacheValid && cachedLevel == level) {
            return cachedIcons;
        }
        List<EntityIcon> built = new ArrayList<>();
        for (ResourceLocation id : entitiesIn(tag)) {
            built.add(new EntityIcon(id, sizeMultiplier, offsetX, offsetY, rotationOffset,
                    spinMode, idleMode, walkMode, silhouetteCheck, nbt, cycleSeconds));
        }
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
        if (icons.isEmpty()) {
            return;
        }
        icons.get(EntityIcon.cycleIndex(cycleSeconds, icons.size())).draw(graphics, x, y, w, h);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CyclingEntityIcon other
                && other.tag.location().equals(tag.location())
                && Float.compare(other.sizeMultiplier, sizeMultiplier) == 0
                && Float.compare(other.offsetX, offsetX) == 0
                && Float.compare(other.offsetY, offsetY) == 0
                && Float.compare(other.rotationOffset, rotationOffset) == 0
                && Float.compare(other.cycleSeconds, cycleSeconds) == 0
                && other.spinMode == spinMode
                && other.idleMode == idleMode
                && other.walkMode == walkMode
                && other.nbt.equals(nbt);
    }

    @Override
    public String toString() {
        return "entity_tag:" + tag.location() + (nbt.isEmpty() ? "" : nbt);
    }

    @Override
    public int hashCode() {
        int h = tag.location().hashCode();
        h = h * 31 + Float.hashCode(sizeMultiplier);
        h = h * 31 + Float.hashCode(offsetX);
        h = h * 31 + Float.hashCode(offsetY);
        h = h * 31 + Float.hashCode(rotationOffset);
        h = h * 31 + Float.hashCode(cycleSeconds);
        h = h * 31 + spinMode.ordinal();
        h = h * 31 + idleMode.ordinal();
        h = h * 31 + walkMode.ordinal();
        h = h * 31 + nbt.hashCode();
        return h;
    }
}
