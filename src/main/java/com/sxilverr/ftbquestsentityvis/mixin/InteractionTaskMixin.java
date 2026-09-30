package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.duck.IEntityVis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "questsadditions.tasks.InteractionTask")
public abstract class InteractionTaskMixin implements IEntityVis {
    @Shadow(remap = false) public ResourceLocation entity;

    @Unique private final EntityVisSettings ftbquestsentityvis$vis = new EntityVisSettings();

    @Override
    public EntityVisSettings ftbquestsentityvis$vis() {
        return ftbquestsentityvis$vis;
    }

    @Override
    public ResourceLocation ftbquestsentityvis$visEntity() {
        return entity;
    }

    @Inject(method = "writeData", at = @At("TAIL"), remap = false)
    private void ftbquestsentityvis$writeData(CompoundTag nbt, CallbackInfo ci) {
        ftbquestsentityvis$vis.write(nbt, EntityVisSettings.PREFIX);
    }

    @Inject(method = "readData", at = @At("TAIL"), remap = false)
    private void ftbquestsentityvis$readData(CompoundTag nbt, CallbackInfo ci) {
        ftbquestsentityvis$vis.read(nbt, EntityVisSettings.PREFIX);
    }

    @Inject(method = "writeNetData", at = @At("TAIL"), remap = false)
    private void ftbquestsentityvis$writeNetData(FriendlyByteBuf buf, CallbackInfo ci) {
        ftbquestsentityvis$vis.write(buf);
    }

    @Inject(method = "readNetData", at = @At("TAIL"), remap = false)
    private void ftbquestsentityvis$readNetData(FriendlyByteBuf buf, CallbackInfo ci) {
        ftbquestsentityvis$vis.read(buf);
    }
}
