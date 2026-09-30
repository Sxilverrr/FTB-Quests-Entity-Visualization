package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.duck.IEntityIcon;
import com.sxilverr.ftbquestsentityvis.duck.IQuestVisOptions;
import dev.ftb.mods.ftbquests.quest.Quest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Quest.class)
public abstract class QuestMixin implements IEntityIcon, IQuestVisOptions {
    @Unique private static final String ftbquestsentityvis$KEY_QUEST_SIZE = "entity_vis_size";

    @Unique private float ftbquestsentityvis$questVisSize = 1.0F;
    @Unique private final EntityVisSettings ftbquestsentityvis$icon = new EntityVisSettings();

    @Override
    public float ftbquestsentityvis$getQuestVisSize() {
        return ftbquestsentityvis$questVisSize;
    }

    @Override
    public void ftbquestsentityvis$setQuestVisSize(float size) {
        ftbquestsentityvis$questVisSize = size;
    }

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
        nbt.putFloat(ftbquestsentityvis$KEY_QUEST_SIZE, ftbquestsentityvis$questVisSize);
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
        ftbquestsentityvis$questVisSize = nbt.contains(ftbquestsentityvis$KEY_QUEST_SIZE) ? nbt.getFloat(ftbquestsentityvis$KEY_QUEST_SIZE) : 1.0F;
        ftbquestsentityvis$icon.read(nbt.getCompound(EntityVisSettings.ICON_KEY), "");
    }

    @Inject(method = "writeNetData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$writeNetData(net.minecraft.network.RegistryFriendlyByteBuf buf, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$writeNetData(FriendlyByteBuf buf, CallbackInfo ci) {
    //?}
        buf.writeFloat(ftbquestsentityvis$questVisSize);
        ftbquestsentityvis$icon.write(buf);
    }

    @Inject(method = "readNetData", at = @At("TAIL"), remap = false)
    //? if >=1.21.1 {
    /*private void ftbquestsentityvis$readNetData(net.minecraft.network.RegistryFriendlyByteBuf buf, CallbackInfo ci) {*/
    //?} else {
    private void ftbquestsentityvis$readNetData(FriendlyByteBuf buf, CallbackInfo ci) {
    //?}
        ftbquestsentityvis$questVisSize = buf.readFloat();
        ftbquestsentityvis$icon.read(buf);
    }
}
