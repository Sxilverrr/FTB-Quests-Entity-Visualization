package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.ObserveTypeAccess;
import com.sxilverr.ftbquestsentityvis.duck.IEntityVis;
import dev.ftb.mods.ftbquests.quest.task.ObservationTask;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ObservationTask.class)
public abstract class ObservationTaskMixin implements IEntityVis {
    @Shadow(remap = false) private String toObserve;

    @Unique private final EntityVisSettings ftbquestsentityvis$vis = new EntityVisSettings();

    @Override
    public EntityVisSettings ftbquestsentityvis$vis() {
        return ftbquestsentityvis$vis;
    }

    @Override
    public ResourceLocation ftbquestsentityvis$visEntity() {
        ResourceLocation id = ftbquestsentityvis$observed(ObserveTypeAccess.ENTITY_TYPE);
        return id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id) ? id : null;
    }

    @Override
    public TagKey<EntityType<?>> ftbquestsentityvis$visTag() {
        ResourceLocation id = ftbquestsentityvis$observed(ObserveTypeAccess.ENTITY_TYPE_TAG);
        return id == null ? null : TagKey.create(Registries.ENTITY_TYPE, id);
    }

    @Unique
    private ResourceLocation ftbquestsentityvis$observed(String type) {
        if (toObserve == null || toObserve.isEmpty() || !type.equals(ObserveTypeAccess.nameOf(this))) {
            return null;
        }
        return ResourceLocation.tryParse(toObserve.startsWith("#") ? toObserve.substring(1) : toObserve);
    }

    @Inject(method = "writeData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$writeData(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$writeData(CompoundTag nbt, CallbackInfo ci) {
    //?}
        ftbquestsentityvis$vis.write(nbt, EntityVisSettings.PREFIX);
    }

    @Inject(method = "readData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$readData(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$readData(CompoundTag nbt, CallbackInfo ci) {
    //?}
        ftbquestsentityvis$vis.read(nbt, EntityVisSettings.PREFIX);
    }

    @Inject(method = "writeNetData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$writeNetData(net.minecraft.network.RegistryFriendlyByteBuf buf, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$writeNetData(FriendlyByteBuf buf, CallbackInfo ci) {
    //?}
        ftbquestsentityvis$vis.write(buf);
    }

    @Inject(method = "readNetData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$readNetData(net.minecraft.network.RegistryFriendlyByteBuf buf, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$readNetData(FriendlyByteBuf buf, CallbackInfo ci) {
    //?}
        ftbquestsentityvis$vis.read(buf);
    }
}
