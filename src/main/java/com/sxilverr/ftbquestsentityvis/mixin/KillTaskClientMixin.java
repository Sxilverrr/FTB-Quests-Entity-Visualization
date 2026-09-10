package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.Config;
import com.sxilverr.ftbquestsentityvis.client.ClientStateUtil;
import com.sxilverr.ftbquestsentityvis.client.CyclingEntityIcon;
import com.sxilverr.ftbquestsentityvis.client.EntityIcon;
import com.sxilverr.ftbquestsentityvis.duck.IKillTaskTagOption;
import com.sxilverr.ftbquestsentityvis.duck.IKillTaskVisOptions;
import com.sxilverr.ftbquestsentityvis.duck.OverrideMode;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.task.KillTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

@Mixin(KillTask.class)
public abstract class KillTaskClientMixin {
    //? if >=1.21.1 {
    /*@Shadow(remap = false)
    private ResourceLocation entityTypeId;

    @Shadow(remap = false)
    private TagKey<EntityType<?>> entityTypeTag;*/
    //?} else {
    @Shadow(remap = false)
    private ResourceLocation entity;
    //?}

    @Inject(method = "getAltIcon", at = @At("HEAD"), cancellable = true, remap = false)
    private void ftbquestsentityvis$replaceWithEntityIcon(CallbackInfoReturnable<Icon> cir) {
        IKillTaskVisOptions opts = (IKillTaskVisOptions) this;
        Task self = (Task) (Object) this;
        BooleanSupplier silhouette = ClientStateUtil.silhouetteCheck(self, opts.ftbquestsentityvis$getSilhouetteMode());

        TagKey<EntityType<?>> tag = ftbquestsentityvis$resolveVisualTag();
        if (tag != null
                && opts.ftbquestsentityvis$getTagCycleMode().resolve(Config.tagCycle)
                && !CyclingEntityIcon.entitiesIn(tag).isEmpty()) {
            cir.setReturnValue(new CyclingEntityIcon(
                    tag,
                    opts.ftbquestsentityvis$getVisSize(),
                    opts.ftbquestsentityvis$getVisOffsetX(),
                    opts.ftbquestsentityvis$getVisOffsetY(),
                    opts.ftbquestsentityvis$getVisRotation(),
                    opts.ftbquestsentityvis$getSpinMode(),
                    opts.ftbquestsentityvis$getIdleMode(),
                    opts.ftbquestsentityvis$getWalkMode(),
                    silhouette,
                    opts.ftbquestsentityvis$getVisNbt(),
                    opts.ftbquestsentityvis$getTagCycleSeconds()
            ));
            return;
        }

        ResourceLocation visualEntity = ftbquestsentityvis$resolveVisualEntity();
        if (visualEntity == null) {
            return;
        }
        cir.setReturnValue(new EntityIcon(
                visualEntity,
                opts.ftbquestsentityvis$getVisSize(),
                opts.ftbquestsentityvis$getVisOffsetX(),
                opts.ftbquestsentityvis$getVisOffsetY(),
                opts.ftbquestsentityvis$getVisRotation(),
                opts.ftbquestsentityvis$getSpinMode(),
                opts.ftbquestsentityvis$getIdleMode(),
                opts.ftbquestsentityvis$getWalkMode(),
                silhouette,
                opts.ftbquestsentityvis$getVisNbt(),
                opts.ftbquestsentityvis$getTagCycleSeconds()
        ));
    }

    @Unique
    private TagKey<EntityType<?>> ftbquestsentityvis$resolveVisualTag() {
        //? if >=1.21.1 {
        /*return entityTypeTag;*/
        //?} else {
        if (entity == null || !((IKillTaskTagOption) this).ftbquestsentityvis$getUseTag()) {
            return null;
        }
        return TagKey.create(Registries.ENTITY_TYPE, entity);
        //?}
    }

    @Unique
    private ResourceLocation ftbquestsentityvis$resolveVisualEntity() {
        TagKey<EntityType<?>> tag = ftbquestsentityvis$resolveVisualTag();
        if (tag != null) {
            Optional<EntityType<?>> first = BuiltInRegistries.ENTITY_TYPE.getTag(tag)
                    .flatMap(set -> set.stream().findFirst())
                    .map(holder -> holder.value());
            return first.map(BuiltInRegistries.ENTITY_TYPE::getKey).orElse(null);
        }
        //? if >=1.21.1 {
        /*return entityTypeId;*/
        //?} else {
        return ((IKillTaskTagOption) this).ftbquestsentityvis$getUseTag() ? null : entity;
        //?}
    }

    //? if <1.21.1 {
    @Inject(method = "fillConfigGroup", at = @At("TAIL"), remap = false)
    private void ftbquestsentityvis$addUseTagConfig(ConfigGroup config, CallbackInfo ci) {
        IKillTaskTagOption tagOpts = (IKillTaskTagOption) this;
        config.addBool("entity_use_tag", tagOpts.ftbquestsentityvis$getUseTag(),
                tagOpts::ftbquestsentityvis$setUseTag, false);
    }
    //?}

    @SuppressWarnings({"unchecked", "rawtypes"})
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
        return builder.icon((Function<Object, Icon>) value -> {
            if (value instanceof ResourceLocation rl) {
                return new EntityIcon(rl, 1.0F, 0.0F, 0.0F, 0.0F,
                        OverrideMode.USE_GLOBAL, OverrideMode.USE_GLOBAL, OverrideMode.USE_GLOBAL);
            }
            return (Icon) originalIconFunc.apply(value);
        });
    }
}
