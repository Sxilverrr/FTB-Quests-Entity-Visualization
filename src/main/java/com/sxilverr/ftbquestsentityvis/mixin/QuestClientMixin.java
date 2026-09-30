package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.client.QuestSizeWrappedIcon;
import com.sxilverr.ftbquestsentityvis.duck.IEntityIcon;
import com.sxilverr.ftbquestsentityvis.duck.IEntityVis;
import com.sxilverr.ftbquestsentityvis.duck.IQuestVisOptions;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.IconAnimation;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Quest.class)
public abstract class QuestClientMixin {
    @Inject(method = "getAltIcon", at = @At("HEAD"), cancellable = true, remap = false)
    private void ftbquestsentityvis$selectQuestIconTasks(CallbackInfoReturnable<Icon> cir) {
        Quest self = (Quest) (Object) this;
        List<Icon> selected = new ArrayList<>();
        for (Task task : self.getTasksAsList()) {
            EntityVisSettings icon = ((IEntityIcon) task).ftbquestsentityvis$icon();
            if (task instanceof IEntityVis host ? host.ftbquestsentityvis$vis().useAsQuestIcon : icon.enabled && icon.useAsQuestIcon) {
                selected.add(task.getIcon());
            }
        }
        if (selected.isEmpty()) {
            return;
        }
        IQuestVisOptions opts = (IQuestVisOptions) this;
        Icon merged = IconAnimation.fromList(selected, false);
        cir.setReturnValue(QuestSizeWrappedIcon.wrapIfNeeded(merged, opts.ftbquestsentityvis$getQuestVisSize()));
    }

    @Inject(method = "fillConfigGroup", at = @At("TAIL"), remap = false)
    private void ftbquestsentityvis$fillConfigGroup(ConfigGroup config, CallbackInfo ci) {
        IQuestVisOptions opts = (IQuestVisOptions) this;
        config.getOrCreateSubgroup("appearance").addDouble("entity_vis_size", opts.ftbquestsentityvis$getQuestVisSize(),
                v -> opts.ftbquestsentityvis$setQuestVisSize(v.floatValue()), 1.0D, 0.0D, 10.0D);
    }

    @Inject(method = "getAltIcon", at = @At("RETURN"), cancellable = true, remap = false)
    private void ftbquestsentityvis$wrapWithQuestSize(CallbackInfoReturnable<Icon> cir) {
        Icon original = cir.getReturnValue();
        if (original == null || original instanceof QuestSizeWrappedIcon) {
            return;
        }
        IQuestVisOptions opts = (IQuestVisOptions) this;
        cir.setReturnValue(QuestSizeWrappedIcon.wrapIfNeeded(original, opts.ftbquestsentityvis$getQuestVisSize()));
    }
}
