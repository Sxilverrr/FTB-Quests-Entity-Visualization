package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.duck.IEntityVis;
import com.sxilverr.ftbquestsentityvis.duck.IKillTaskTagOption;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.KillTask;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KillTask.class)
public abstract class KillTaskMixin implements IEntityVis/*? if <1.21.1 {*/, IKillTaskTagOption/*?}*/ {
    @Unique private final EntityVisSettings ftbquestsentityvis$vis = new EntityVisSettings();

    //? if >=1.21.1 {
    /*@Shadow(remap = false) private ResourceLocation entityTypeId;
    @Shadow(remap = false) private TagKey<EntityType<?>> entityTypeTag;

    @Override
    public ResourceLocation ftbquestsentityvis$visEntity() {
        return entityTypeId;
    }

    @Override
    public TagKey<EntityType<?>> ftbquestsentityvis$visTag() {
        return entityTypeTag;
    }*/
    //?} else {
    @Unique private static final String ftbquestsentityvis$KEY_USE_TAG = "entity_use_tag";

    @Shadow(remap = false) private ResourceLocation entity;
    @Unique private boolean ftbquestsentityvis$useTag;

    @Override
    public ResourceLocation ftbquestsentityvis$visEntity() {
        return ftbquestsentityvis$useTag ? null : entity;
    }

    @Override
    public TagKey<EntityType<?>> ftbquestsentityvis$visTag() {
        return ftbquestsentityvis$useTag && entity != null ? TagKey.create(Registries.ENTITY_TYPE, entity) : null;
    }

    @Override
    public boolean ftbquestsentityvis$getUseTag() {
        return ftbquestsentityvis$useTag;
    }

    @Override
    public void ftbquestsentityvis$setUseTag(boolean useTag) {
        ftbquestsentityvis$useTag = useTag;
    }

    @Inject(method = "kill", at = @At("HEAD"), cancellable = true, remap = false)
    private void ftbquestsentityvis$tagAwareKill(TeamData data, LivingEntity victim, CallbackInfo ci) {
        if (!ftbquestsentityvis$useTag) {
            return;
        }
        KillTask self = (KillTask) (Object) this;
        TagKey<EntityType<?>> tag = ftbquestsentityvis$visTag();
        if (tag != null && !data.isCompleted(self) && victim.getType().is(tag)) {
            data.addProgress(self, 1L);
        }
        ci.cancel();
    }
    //?}

    @Override
    public EntityVisSettings ftbquestsentityvis$vis() {
        return ftbquestsentityvis$vis;
    }

    @Inject(method = "writeData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$writeData(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$writeData(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean(ftbquestsentityvis$KEY_USE_TAG, ftbquestsentityvis$useTag);
    //?}
        ftbquestsentityvis$vis.write(nbt, EntityVisSettings.PREFIX);
    }

    @Inject(method = "readData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$readData(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$readData(CompoundTag nbt, CallbackInfo ci) {
        ftbquestsentityvis$useTag = nbt.getBoolean(ftbquestsentityvis$KEY_USE_TAG);
    //?}
        ftbquestsentityvis$vis.read(nbt, EntityVisSettings.PREFIX);
    }

    @Inject(method = "writeNetData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$writeNetData(net.minecraft.network.RegistryFriendlyByteBuf buf, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$writeNetData(FriendlyByteBuf buf, CallbackInfo ci) {
        buf.writeBoolean(ftbquestsentityvis$useTag);
    //?}
        ftbquestsentityvis$vis.write(buf);
    }

    @Inject(method = "readNetData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$readNetData(net.minecraft.network.RegistryFriendlyByteBuf buf, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$readNetData(FriendlyByteBuf buf, CallbackInfo ci) {
        ftbquestsentityvis$useTag = buf.readBoolean();
    //?}
        ftbquestsentityvis$vis.read(buf);
    }
}
