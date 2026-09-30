package com.sxilverr.ftbquestsentityvis;

import com.sxilverr.ftbquestsentityvis.duck.OverrideMode;
import com.sxilverr.ftbquestsentityvis.duck.SilhouetteMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class EntityVisSettings {
    public static final ResourceLocation DEFAULT_ENTITY = ResourceLocation.tryParse("minecraft:pig");
    public static final String PREFIX = "entity_vis_";
    public static final String ICON_KEY = "ftbquestsentityvis_icon";

    public boolean enabled;
    public ResourceLocation entityId;
    public float size = 1.0F;
    public float offsetX;
    public float offsetY;
    public float rotation;
    public OverrideMode spinMode = OverrideMode.USE_GLOBAL;
    public OverrideMode idleMode = OverrideMode.USE_GLOBAL;
    public OverrideMode walkMode = OverrideMode.USE_GLOBAL;
    public SilhouetteMode silhouetteMode = SilhouetteMode.NONE;
    public boolean useAsQuestIcon;
    public String nbt = "";
    public OverrideMode tagCycleMode = OverrideMode.USE_GLOBAL;
    public float cycleSeconds;
    public String skin = "";
    public boolean slimArms;

    public EntityVisSettings copy() {
        EntityVisSettings c = new EntityVisSettings();
        c.set(this);
        return c;
    }

    public void set(EntityVisSettings o) {
        enabled = o.enabled;
        entityId = o.entityId;
        size = o.size;
        offsetX = o.offsetX;
        offsetY = o.offsetY;
        rotation = o.rotation;
        spinMode = o.spinMode;
        idleMode = o.idleMode;
        walkMode = o.walkMode;
        silhouetteMode = o.silhouetteMode;
        useAsQuestIcon = o.useAsQuestIcon;
        nbt = o.nbt;
        tagCycleMode = o.tagCycleMode;
        cycleSeconds = o.cycleSeconds;
        skin = o.skin;
        slimArms = o.slimArms;
    }

    public CompoundTag write(CompoundTag tag, String p) {
        tag.putBoolean(p + "enabled", enabled);
        if (entityId != null) {
            tag.putString(p + "entity", entityId.toString());
        }
        tag.putFloat(p + "size", size);
        tag.putFloat(p + "offset_x", offsetX);
        tag.putFloat(p + "offset_y", offsetY);
        tag.putFloat(p + "rotation", rotation);
        tag.putString(p + "spin_mode", spinMode.name());
        tag.putString(p + "idle_mode", idleMode.name());
        tag.putString(p + "walk_mode", walkMode.name());
        tag.putString(p + "silhouette_mode", silhouetteMode.name());
        tag.putBoolean(p + "use_as_quest_icon", useAsQuestIcon);
        if (!nbt.isEmpty()) {
            tag.putString(p + "nbt", nbt);
        }
        tag.putString(p + "tag_cycle_mode", tagCycleMode.name());
        tag.putFloat(p + "cycle_seconds", cycleSeconds);
        if (!skin.isEmpty()) {
            tag.putString(p + "skin", skin);
        }
        tag.putBoolean(p + "slim_arms", slimArms);
        return tag;
    }

    public void read(CompoundTag tag, String p) {
        enabled = tag.getBoolean(p + "enabled");
        entityId = tag.contains(p + "entity") ? ResourceLocation.tryParse(tag.getString(p + "entity")) : null;
        size = tag.contains(p + "size") ? tag.getFloat(p + "size") : 1.0F;
        offsetX = tag.getFloat(p + "offset_x");
        offsetY = tag.getFloat(p + "offset_y");
        rotation = tag.getFloat(p + "rotation");
        spinMode = enumOr(OverrideMode.USE_GLOBAL, tag.getString(p + "spin_mode"));
        idleMode = enumOr(OverrideMode.USE_GLOBAL, tag.getString(p + "idle_mode"));
        walkMode = enumOr(OverrideMode.USE_GLOBAL, tag.getString(p + "walk_mode"));
        silhouetteMode = tag.contains(p + "silhouette_mode")
                ? enumOr(SilhouetteMode.NONE, tag.getString(p + "silhouette_mode"))
                : tag.getBoolean(p + "silhouette") ? SilhouetteMode.ALWAYS : SilhouetteMode.NONE;
        useAsQuestIcon = tag.getBoolean(p + "use_as_quest_icon");
        nbt = tag.getString(p + "nbt");
        tagCycleMode = enumOr(OverrideMode.USE_GLOBAL, tag.getString(p + "tag_cycle_mode"));
        cycleSeconds = tag.getFloat(tag.contains(p + "cycle_seconds") ? p + "cycle_seconds" : p + "tag_cycle_seconds");
        skin = tag.getString(p + "skin");
        slimArms = tag.getBoolean(p + "slim_arms");
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
        buf.writeUtf(entityId == null ? "" : entityId.toString());
        buf.writeFloat(size);
        buf.writeFloat(offsetX);
        buf.writeFloat(offsetY);
        buf.writeFloat(rotation);
        buf.writeEnum(spinMode);
        buf.writeEnum(idleMode);
        buf.writeEnum(walkMode);
        buf.writeEnum(silhouetteMode);
        buf.writeBoolean(useAsQuestIcon);
        buf.writeUtf(nbt);
        buf.writeEnum(tagCycleMode);
        buf.writeFloat(cycleSeconds);
        buf.writeUtf(skin);
        buf.writeBoolean(slimArms);
    }

    public void read(FriendlyByteBuf buf) {
        enabled = buf.readBoolean();
        String id = buf.readUtf();
        entityId = id.isEmpty() ? null : ResourceLocation.tryParse(id);
        size = buf.readFloat();
        offsetX = buf.readFloat();
        offsetY = buf.readFloat();
        rotation = buf.readFloat();
        spinMode = buf.readEnum(OverrideMode.class);
        idleMode = buf.readEnum(OverrideMode.class);
        walkMode = buf.readEnum(OverrideMode.class);
        silhouetteMode = buf.readEnum(SilhouetteMode.class);
        useAsQuestIcon = buf.readBoolean();
        nbt = buf.readUtf();
        tagCycleMode = buf.readEnum(OverrideMode.class);
        cycleSeconds = buf.readFloat();
        skin = buf.readUtf();
        slimArms = buf.readBoolean();
    }

    public static <E extends Enum<E>> E enumOr(E fallback, String name) {
        try {
            return Enum.valueOf(fallback.getDeclaringClass(), name);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EntityVisSettings s
                && enabled == s.enabled && Objects.equals(entityId, s.entityId)
                && size == s.size && offsetX == s.offsetX && offsetY == s.offsetY && rotation == s.rotation
                && spinMode == s.spinMode && idleMode == s.idleMode && walkMode == s.walkMode
                && silhouetteMode == s.silhouetteMode && useAsQuestIcon == s.useAsQuestIcon
                && nbt.equals(s.nbt) && tagCycleMode == s.tagCycleMode && cycleSeconds == s.cycleSeconds
                && skin.equals(s.skin) && slimArms == s.slimArms;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, entityId, size, offsetX, offsetY, rotation, spinMode, idleMode, walkMode,
                silhouetteMode, useAsQuestIcon, nbt, tagCycleMode, cycleSeconds, skin, slimArms);
    }
}
