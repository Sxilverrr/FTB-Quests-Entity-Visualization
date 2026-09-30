package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.duck.IEntityVis;
import dev.ftb.mods.ftbquests.quest.ChapterImage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChapterImage.class)
public abstract class ChapterImageMixin implements IEntityVis {
    @Unique private final EntityVisSettings ftbquestsentityvis$vis = new EntityVisSettings();

    @Override
    public EntityVisSettings ftbquestsentityvis$vis() {
        return ftbquestsentityvis$vis;
    }

    @Inject(method = "writeData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$writeData(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$writeData(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
    //?}
        if (ftbquestsentityvis$vis.enabled) {
            ftbquestsentityvis$vis.write(nbt, EntityVisSettings.PREFIX);
        }
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
