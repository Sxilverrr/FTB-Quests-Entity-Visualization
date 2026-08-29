package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.duck.IQuestVisOptions;
import com.sxilverr.ftbquestsentityvis.duck.OverrideMode;
import com.sxilverr.ftbquestsentityvis.duck.SilhouetteMode;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Quest.class)
public abstract class QuestMixin implements IQuestVisOptions {
    @Unique private static final String ftbquestsentityvis$KEY_QUEST_SIZE = "entity_vis_size";
    @Unique private static final String ftbquestsentityvis$KEY_ROOT = "ftbquestsentityvis_icon";
    @Unique private static final String ftbquestsentityvis$KEY_ENABLED = "enabled";
    @Unique private static final String ftbquestsentityvis$KEY_ENTITY = "entity";
    @Unique private static final String ftbquestsentityvis$KEY_SIZE = "size";
    @Unique private static final String ftbquestsentityvis$KEY_OFFSET_X = "offset_x";
    @Unique private static final String ftbquestsentityvis$KEY_OFFSET_Y = "offset_y";
    @Unique private static final String ftbquestsentityvis$KEY_ROTATION = "rotation";
    @Unique private static final String ftbquestsentityvis$KEY_SPIN_MODE = "spin_mode";
    @Unique private static final String ftbquestsentityvis$KEY_IDLE_MODE = "idle_mode";
    @Unique private static final String ftbquestsentityvis$KEY_WALK_MODE = "walk_mode";
    @Unique private static final String ftbquestsentityvis$KEY_SILHOUETTE_MODE = "silhouette_mode";
    @Unique private static final String ftbquestsentityvis$KEY_NBT = "nbt";

    @Unique private float ftbquestsentityvis$questVisSize = 1.0F;

    @Unique private boolean ftbquestsentityvis$iconEnabled = false;
    @Unique private ResourceLocation ftbquestsentityvis$iconEntity = null;
    @Unique private float ftbquestsentityvis$iconSize = 1.0F;
    @Unique private float ftbquestsentityvis$iconOffsetX = 0.0F;
    @Unique private float ftbquestsentityvis$iconOffsetY = 0.0F;
    @Unique private float ftbquestsentityvis$iconRotation = 0.0F;
    @Unique private OverrideMode ftbquestsentityvis$iconSpinMode = OverrideMode.USE_GLOBAL;
    @Unique private OverrideMode ftbquestsentityvis$iconIdleMode = OverrideMode.USE_GLOBAL;
    @Unique private OverrideMode ftbquestsentityvis$iconWalkMode = OverrideMode.USE_GLOBAL;
    @Unique private SilhouetteMode ftbquestsentityvis$iconSilhouetteMode = SilhouetteMode.NONE;
    @Unique private String ftbquestsentityvis$iconNbt = "";
    @Unique private boolean ftbquestsentityvis$iconDirty = false;

    @Override public float ftbquestsentityvis$getQuestVisSize() { return ftbquestsentityvis$questVisSize; }
    @Override public void ftbquestsentityvis$setQuestVisSize(float size) { this.ftbquestsentityvis$questVisSize = size; }

    @Override public boolean ftbquestsentityvis$getQuestIconEntityEnabled() { return ftbquestsentityvis$iconEnabled; }
    @Override public void ftbquestsentityvis$setQuestIconEntityEnabled(boolean enabled) { this.ftbquestsentityvis$iconEnabled = enabled; this.ftbquestsentityvis$iconDirty = true; }

    @Override public ResourceLocation ftbquestsentityvis$getQuestIconEntityId() { return ftbquestsentityvis$iconEntity; }
    @Override public void ftbquestsentityvis$setQuestIconEntityId(ResourceLocation id) { this.ftbquestsentityvis$iconEntity = id; this.ftbquestsentityvis$iconDirty = true; }

    @Override public float ftbquestsentityvis$getQuestIconVisSize() { return ftbquestsentityvis$iconSize; }
    @Override public void ftbquestsentityvis$setQuestIconVisSize(float size) { this.ftbquestsentityvis$iconSize = size; this.ftbquestsentityvis$iconDirty = true; }

    @Override public float ftbquestsentityvis$getQuestIconVisOffsetX() { return ftbquestsentityvis$iconOffsetX; }
    @Override public void ftbquestsentityvis$setQuestIconVisOffsetX(float offset) { this.ftbquestsentityvis$iconOffsetX = offset; this.ftbquestsentityvis$iconDirty = true; }

    @Override public float ftbquestsentityvis$getQuestIconVisOffsetY() { return ftbquestsentityvis$iconOffsetY; }
    @Override public void ftbquestsentityvis$setQuestIconVisOffsetY(float offset) { this.ftbquestsentityvis$iconOffsetY = offset; this.ftbquestsentityvis$iconDirty = true; }

    @Override public float ftbquestsentityvis$getQuestIconVisRotation() { return ftbquestsentityvis$iconRotation; }
    @Override public void ftbquestsentityvis$setQuestIconVisRotation(float rotation) { this.ftbquestsentityvis$iconRotation = rotation; this.ftbquestsentityvis$iconDirty = true; }

    @Override public OverrideMode ftbquestsentityvis$getQuestIconSpinMode() { return ftbquestsentityvis$iconSpinMode; }
    @Override public void ftbquestsentityvis$setQuestIconSpinMode(OverrideMode mode) { this.ftbquestsentityvis$iconSpinMode = mode; this.ftbquestsentityvis$iconDirty = true; }

    @Override public OverrideMode ftbquestsentityvis$getQuestIconIdleMode() { return ftbquestsentityvis$iconIdleMode; }
    @Override public void ftbquestsentityvis$setQuestIconIdleMode(OverrideMode mode) { this.ftbquestsentityvis$iconIdleMode = mode; this.ftbquestsentityvis$iconDirty = true; }

    @Override public OverrideMode ftbquestsentityvis$getQuestIconWalkMode() { return ftbquestsentityvis$iconWalkMode; }
    @Override public void ftbquestsentityvis$setQuestIconWalkMode(OverrideMode mode) { this.ftbquestsentityvis$iconWalkMode = mode; this.ftbquestsentityvis$iconDirty = true; }

    @Override public SilhouetteMode ftbquestsentityvis$getQuestIconSilhouetteMode() { return ftbquestsentityvis$iconSilhouetteMode; }
    @Override public void ftbquestsentityvis$setQuestIconSilhouetteMode(SilhouetteMode mode) { this.ftbquestsentityvis$iconSilhouetteMode = mode; this.ftbquestsentityvis$iconDirty = true; }

    @Override public String ftbquestsentityvis$getQuestIconNbt() { return ftbquestsentityvis$iconNbt; }
    @Override public void ftbquestsentityvis$setQuestIconNbt(String nbt) { this.ftbquestsentityvis$iconNbt = nbt == null ? "" : nbt; this.ftbquestsentityvis$iconDirty = true; }

    @Override public boolean ftbquestsentityvis$isQuestIconDirty() { return ftbquestsentityvis$iconDirty; }
    @Override public void ftbquestsentityvis$setQuestIconDirty(boolean dirty) { this.ftbquestsentityvis$iconDirty = dirty; }

    @Inject(method = "writeData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$writeData(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$writeData(CompoundTag nbt, CallbackInfo ci) {
    //?}
        nbt.putFloat(ftbquestsentityvis$KEY_QUEST_SIZE, ftbquestsentityvis$questVisSize);
        if (!ftbquestsentityvis$iconEnabled && ftbquestsentityvis$iconEntity == null) {
            return;
        }
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(ftbquestsentityvis$KEY_ENABLED, ftbquestsentityvis$iconEnabled);
        if (ftbquestsentityvis$iconEntity != null) {
            tag.putString(ftbquestsentityvis$KEY_ENTITY, ftbquestsentityvis$iconEntity.toString());
        }
        tag.putFloat(ftbquestsentityvis$KEY_SIZE, ftbquestsentityvis$iconSize);
        tag.putFloat(ftbquestsentityvis$KEY_OFFSET_X, ftbquestsentityvis$iconOffsetX);
        tag.putFloat(ftbquestsentityvis$KEY_OFFSET_Y, ftbquestsentityvis$iconOffsetY);
        tag.putFloat(ftbquestsentityvis$KEY_ROTATION, ftbquestsentityvis$iconRotation);
        tag.putString(ftbquestsentityvis$KEY_SPIN_MODE, ftbquestsentityvis$iconSpinMode.name());
        tag.putString(ftbquestsentityvis$KEY_IDLE_MODE, ftbquestsentityvis$iconIdleMode.name());
        tag.putString(ftbquestsentityvis$KEY_WALK_MODE, ftbquestsentityvis$iconWalkMode.name());
        tag.putString(ftbquestsentityvis$KEY_SILHOUETTE_MODE, ftbquestsentityvis$iconSilhouetteMode.name());
        if (!ftbquestsentityvis$iconNbt.isEmpty()) {
            tag.putString(ftbquestsentityvis$KEY_NBT, ftbquestsentityvis$iconNbt);
        }
        nbt.put(ftbquestsentityvis$KEY_ROOT, tag);
    }

    @Inject(method = "readData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$readData(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$readData(CompoundTag nbt, CallbackInfo ci) {
    //?}
        ftbquestsentityvis$questVisSize = nbt.contains(ftbquestsentityvis$KEY_QUEST_SIZE) ? nbt.getFloat(ftbquestsentityvis$KEY_QUEST_SIZE) : 1.0F;
        ftbquestsentityvis$iconDirty = true;
        if (!nbt.contains(ftbquestsentityvis$KEY_ROOT)) {
            ftbquestsentityvis$iconEnabled = false;
            ftbquestsentityvis$iconEntity = null;
            return;
        }
        CompoundTag tag = nbt.getCompound(ftbquestsentityvis$KEY_ROOT);
        ftbquestsentityvis$iconEnabled = tag.getBoolean(ftbquestsentityvis$KEY_ENABLED);
        ftbquestsentityvis$iconEntity = tag.contains(ftbquestsentityvis$KEY_ENTITY)
                ? ResourceLocation.tryParse(tag.getString(ftbquestsentityvis$KEY_ENTITY)) : null;
        ftbquestsentityvis$iconSize = tag.contains(ftbquestsentityvis$KEY_SIZE) ? tag.getFloat(ftbquestsentityvis$KEY_SIZE) : 1.0F;
        ftbquestsentityvis$iconOffsetX = tag.contains(ftbquestsentityvis$KEY_OFFSET_X) ? tag.getFloat(ftbquestsentityvis$KEY_OFFSET_X) : 0.0F;
        ftbquestsentityvis$iconOffsetY = tag.contains(ftbquestsentityvis$KEY_OFFSET_Y) ? tag.getFloat(ftbquestsentityvis$KEY_OFFSET_Y) : 0.0F;
        ftbquestsentityvis$iconRotation = tag.contains(ftbquestsentityvis$KEY_ROTATION) ? tag.getFloat(ftbquestsentityvis$KEY_ROTATION) : 0.0F;
        ftbquestsentityvis$iconSpinMode = OverrideMode.fromName(tag.getString(ftbquestsentityvis$KEY_SPIN_MODE));
        ftbquestsentityvis$iconIdleMode = OverrideMode.fromName(tag.getString(ftbquestsentityvis$KEY_IDLE_MODE));
        ftbquestsentityvis$iconWalkMode = OverrideMode.fromName(tag.getString(ftbquestsentityvis$KEY_WALK_MODE));
        ftbquestsentityvis$iconSilhouetteMode = SilhouetteMode.fromName(tag.getString(ftbquestsentityvis$KEY_SILHOUETTE_MODE));
        ftbquestsentityvis$iconNbt = tag.contains(ftbquestsentityvis$KEY_NBT) ? tag.getString(ftbquestsentityvis$KEY_NBT) : "";
    }

    @Inject(method = "writeNetData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$writeNetData(net.minecraft.network.RegistryFriendlyByteBuf buf, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$writeNetData(FriendlyByteBuf buf, CallbackInfo ci) {
    //?}
        buf.writeFloat(ftbquestsentityvis$questVisSize);
        buf.writeBoolean(ftbquestsentityvis$iconEnabled);
        boolean hasEntity = ftbquestsentityvis$iconEntity != null;
        buf.writeBoolean(hasEntity);
        if (hasEntity) {
            buf.writeUtf(ftbquestsentityvis$iconEntity.toString());
        }
        buf.writeFloat(ftbquestsentityvis$iconSize);
        buf.writeFloat(ftbquestsentityvis$iconOffsetX);
        buf.writeFloat(ftbquestsentityvis$iconOffsetY);
        buf.writeFloat(ftbquestsentityvis$iconRotation);
        buf.writeUtf(ftbquestsentityvis$iconSpinMode.name());
        buf.writeUtf(ftbquestsentityvis$iconIdleMode.name());
        buf.writeUtf(ftbquestsentityvis$iconWalkMode.name());
        buf.writeUtf(ftbquestsentityvis$iconSilhouetteMode.name());
        buf.writeUtf(ftbquestsentityvis$iconNbt, Short.MAX_VALUE);
    }

    @Inject(method = "readNetData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$readNetData(net.minecraft.network.RegistryFriendlyByteBuf buf, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$readNetData(FriendlyByteBuf buf, CallbackInfo ci) {
    //?}
        ftbquestsentityvis$questVisSize = buf.readFloat();
        ftbquestsentityvis$iconEnabled = buf.readBoolean();
        ftbquestsentityvis$iconEntity = buf.readBoolean() ? ResourceLocation.tryParse(buf.readUtf()) : null;
        ftbquestsentityvis$iconSize = buf.readFloat();
        ftbquestsentityvis$iconOffsetX = buf.readFloat();
        ftbquestsentityvis$iconOffsetY = buf.readFloat();
        ftbquestsentityvis$iconRotation = buf.readFloat();
        ftbquestsentityvis$iconSpinMode = OverrideMode.fromName(buf.readUtf());
        ftbquestsentityvis$iconIdleMode = OverrideMode.fromName(buf.readUtf());
        ftbquestsentityvis$iconWalkMode = OverrideMode.fromName(buf.readUtf());
        ftbquestsentityvis$iconSilhouetteMode = SilhouetteMode.fromName(buf.readUtf());
        ftbquestsentityvis$iconNbt = buf.readUtf(Short.MAX_VALUE);
        ftbquestsentityvis$iconDirty = true;
    }

    @Inject(method = "fillConfigGroup", at = @At("TAIL"), remap = false)
    private void ftbquestsentityvis$fillConfigGroup(ConfigGroup config, CallbackInfo ci) {
        ConfigGroup appearance = config.getOrCreateSubgroup("appearance");
        appearance.addDouble("entity_vis_size", ftbquestsentityvis$questVisSize,
                v -> ftbquestsentityvis$questVisSize = v.floatValue(),
                1.0D, 0.0D, 10.0D);
    }
}
