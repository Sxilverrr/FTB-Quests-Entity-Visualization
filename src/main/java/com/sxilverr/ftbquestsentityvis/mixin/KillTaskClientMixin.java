package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.client.EntityIcon;
import com.sxilverr.ftbquestsentityvis.duck.IEntityVis;
import com.sxilverr.ftbquestsentityvis.duck.IKillTaskTagOption;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.task.KillTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(KillTask.class)
public abstract class KillTaskClientMixin {
    @Inject(method = "getAltIcon", at = @At("HEAD"), cancellable = true, remap = false)
    private void ftbquestsentityvis$replaceWithEntityIcon(CallbackInfoReturnable<Icon> cir) {
        Icon icon = EntityIcon.forTask((Task) (Object) this, (IEntityVis) this);
        if (icon != null) {
            cir.setReturnValue(icon);
        }
    }

    //? if <1.21.1 {
    @Inject(method = "fillConfigGroup", at = @At("TAIL"), remap = false)
    private void ftbquestsentityvis$addUseTagConfig(ConfigGroup config, CallbackInfo ci) {
        IKillTaskTagOption tagOpts = (IKillTaskTagOption) this;
        config.addBool("entity_use_tag", tagOpts.ftbquestsentityvis$getUseTag(), tagOpts::ftbquestsentityvis$setUseTag, false);
    }
    //?}

    @SuppressWarnings("rawtypes")
    //? if >=1.21.1 {
    /*@Redirect(method = "scanEntityTypes", remap = false,
            at = @At(value = "INVOKE",
                    target = "Ldev/ftb/mods/ftblibrary/config/NameMap$Builder;icon(Ljava/util/function/Function;)Ldev/ftb/mods/ftblibrary/config/NameMap$Builder;"))
    private static NameMap.Builder ftbquestsentityvis$replaceEntitySelectorIcons(NameMap.Builder builder, Function originalIconFunc) {*/
    //?} else {
    @Redirect(method = "fillConfigGroup", remap = false,
            at = @At(value = "INVOKE",
                    target = "Ldev/ftb/mods/ftblibrary/config/NameMap$Builder;icon(Ljava/util/function/Function;)Ldev/ftb/mods/ftblibrary/config/NameMap$Builder;"))
    private NameMap.Builder ftbquestsentityvis$replaceEntitySelectorIcons(NameMap.Builder builder, Function originalIconFunc) {
    //?}
        return EntityIcon.withEntityIcons(builder, originalIconFunc);
    }
}
