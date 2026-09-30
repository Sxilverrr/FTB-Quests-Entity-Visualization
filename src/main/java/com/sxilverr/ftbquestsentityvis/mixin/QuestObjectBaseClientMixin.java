package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.client.EntityIcon;
import com.sxilverr.ftbquestsentityvis.duck.IEntityIcon;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(QuestObjectBase.class)
public abstract class QuestObjectBaseClientMixin {
    @Unique private EntityIcon ftbquestsentityvis$entityIconCache;

    @Inject(method = "getIcon", at = @At("HEAD"), cancellable = true, remap = false)
    private void ftbquestsentityvis$entityIcon(CallbackInfoReturnable<Icon> cir) {
        if (!((Object) this instanceof IEntityIcon host)) {
            return;
        }
        EntityVisSettings icon = host.ftbquestsentityvis$icon();
        if (!icon.enabled || icon.entityId == null) {
            return;
        }
        if (ftbquestsentityvis$entityIconCache == null || !ftbquestsentityvis$entityIconCache.showing(icon)) {
            ftbquestsentityvis$entityIconCache = new EntityIcon(icon.entityId, icon, (QuestObject) (Object) this);
        }
        cir.setReturnValue(ftbquestsentityvis$entityIconCache);
    }
}
