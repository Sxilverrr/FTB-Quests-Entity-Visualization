package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.client.ClientStateUtil;
import com.sxilverr.ftbquestsentityvis.client.EntityIcon;
import com.sxilverr.ftbquestsentityvis.duck.IQuestVisOptions;
import com.sxilverr.ftbquestsentityvis.duck.ITaskIconVisOptions;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(QuestObjectBase.class)
public abstract class QuestObjectBaseClientMixin {
    @Unique private EntityIcon ftbquestsentityvis$entityIconCache;

    @Inject(method = "getIcon", at = @At("HEAD"), cancellable = true, remap = false)
    private void ftbquestsentityvis$entityIcon(CallbackInfoReturnable<Icon> cir) {
        if (((Object) this) instanceof Task self && ((Object) this) instanceof ITaskIconVisOptions opts) {
            ftbquestsentityvis$taskEntityIcon(self, opts, cir);
        } else if (((Object) this) instanceof Quest self && ((Object) this) instanceof IQuestVisOptions opts) {
            ftbquestsentityvis$questEntityIcon(self, opts, cir);
        }
    }

    @Unique
    private void ftbquestsentityvis$taskEntityIcon(Task self, ITaskIconVisOptions opts, CallbackInfoReturnable<Icon> cir) {
        if (!opts.ftbquestsentityvis$getIconEntityEnabled()) {
            return;
        }
        ResourceLocation id = opts.ftbquestsentityvis$getIconEntityId();
        if (id == null) {
            return;
        }
        if (ftbquestsentityvis$entityIconCache == null || opts.ftbquestsentityvis$isIconDirty()) {
            BooleanSupplier silhouette = ClientStateUtil.silhouetteCheck(self, opts.ftbquestsentityvis$getIconSilhouetteMode());
            ftbquestsentityvis$entityIconCache = new EntityIcon(
                    id,
                    opts.ftbquestsentityvis$getIconVisSize(),
                    opts.ftbquestsentityvis$getIconVisOffsetX(),
                    opts.ftbquestsentityvis$getIconVisOffsetY(),
                    opts.ftbquestsentityvis$getIconVisRotation(),
                    opts.ftbquestsentityvis$getIconSpinMode(),
                    opts.ftbquestsentityvis$getIconIdleMode(),
                    opts.ftbquestsentityvis$getIconWalkMode(),
                    silhouette,
                    opts.ftbquestsentityvis$getIconNbt(),
                    opts.ftbquestsentityvis$getIconCycleSeconds()
            );
            opts.ftbquestsentityvis$setIconDirty(false);
        }
        cir.setReturnValue(ftbquestsentityvis$entityIconCache);
    }

    @Unique
    private void ftbquestsentityvis$questEntityIcon(Quest self, IQuestVisOptions opts, CallbackInfoReturnable<Icon> cir) {
        if (!opts.ftbquestsentityvis$getQuestIconEntityEnabled()) {
            return;
        }
        ResourceLocation id = opts.ftbquestsentityvis$getQuestIconEntityId();
        if (id == null) {
            return;
        }
        if (ftbquestsentityvis$entityIconCache == null || opts.ftbquestsentityvis$isQuestIconDirty()) {
            BooleanSupplier silhouette = ClientStateUtil.silhouetteCheck(self, opts.ftbquestsentityvis$getQuestIconSilhouetteMode());
            ftbquestsentityvis$entityIconCache = new EntityIcon(
                    id,
                    opts.ftbquestsentityvis$getQuestIconVisSize(),
                    opts.ftbquestsentityvis$getQuestIconVisOffsetX(),
                    opts.ftbquestsentityvis$getQuestIconVisOffsetY(),
                    opts.ftbquestsentityvis$getQuestIconVisRotation(),
                    opts.ftbquestsentityvis$getQuestIconSpinMode(),
                    opts.ftbquestsentityvis$getQuestIconIdleMode(),
                    opts.ftbquestsentityvis$getQuestIconWalkMode(),
                    silhouette,
                    opts.ftbquestsentityvis$getQuestIconNbt(),
                    opts.ftbquestsentityvis$getQuestIconCycleSeconds()
            );
            opts.ftbquestsentityvis$setQuestIconDirty(false);
        }
        cir.setReturnValue(ftbquestsentityvis$entityIconCache);
    }
}
