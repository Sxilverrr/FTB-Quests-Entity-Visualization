package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.duck.IEntityIcon;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Task.class)
public abstract class TaskMixin implements IEntityIcon {
    @Unique private final EntityVisSettings ftbquestsentityvis$icon = new EntityVisSettings();

    @Override
    public EntityVisSettings ftbquestsentityvis$icon() {
        return ftbquestsentityvis$icon;
    }

    @Inject(method = "writeData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$writeData(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$writeData(CompoundTag nbt, CallbackInfo ci) {
    //?}
        if (ftbquestsentityvis$icon.enabled || ftbquestsentityvis$icon.entityId != null) {
            nbt.put(EntityVisSettings.ICON_KEY, ftbquestsentityvis$icon.write(new CompoundTag(), ""));
        }
    }

    @Inject(method = "readData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$readData(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$readData(CompoundTag nbt, CallbackInfo ci) {
    //?}
        ftbquestsentityvis$icon.read(nbt.getCompound(EntityVisSettings.ICON_KEY), "");
    }

    @Inject(method = "writeNetData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$writeNetData(net.minecraft.network.RegistryFriendlyByteBuf buf, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$writeNetData(FriendlyByteBuf buf, CallbackInfo ci) {
    //?}
        ftbquestsentityvis$icon.write(buf);
    }

    @Inject(method = "readNetData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$readNetData(net.minecraft.network.RegistryFriendlyByteBuf buf, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$readNetData(FriendlyByteBuf buf, CallbackInfo ci) {
    //?}
        ftbquestsentityvis$icon.read(buf);
    }
}
