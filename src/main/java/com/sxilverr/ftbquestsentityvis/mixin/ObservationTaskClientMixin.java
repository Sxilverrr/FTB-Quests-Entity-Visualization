package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.client.EntityIcon;
import com.sxilverr.ftbquestsentityvis.duck.IEntityVis;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.task.ObservationTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ObservationTask.class)
public abstract class ObservationTaskClientMixin {
    @Shadow(remap = false)
    public abstract TaskType getType();

    public Icon getAltIcon() {
        Icon icon = EntityIcon.forTask((Task) (Object) this, (IEntityVis) this);
        return icon != null ? icon : getType().getIconSupplier();
    }
}
