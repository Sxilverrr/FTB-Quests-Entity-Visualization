package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.Config;
import com.sxilverr.ftbquestsentityvis.ObserveTypeAccess;
import com.sxilverr.ftbquestsentityvis.client.ClientStateUtil;
import com.sxilverr.ftbquestsentityvis.client.CyclingEntityIcon;
import com.sxilverr.ftbquestsentityvis.client.EntityIcon;
import com.sxilverr.ftbquestsentityvis.duck.IKillTaskVisOptions;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.task.ObservationTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;
import java.util.function.BooleanSupplier;

@Mixin(ObservationTask.class)
public abstract class ObservationTaskClientMixin {
    @Shadow(remap = false)
    private String toObserve;

    @Shadow(remap = false)
    public abstract TaskType getType();

    @Unique
    private TagKey<EntityType<?>> ftbquestsentityvis$resolveTag(String raw) {
        String stripped = raw.startsWith("#") ? raw.substring(1) : raw;
        ResourceLocation tagId = ResourceLocation.tryParse(stripped);
        if (tagId == null) {
            return null;
        }
        return TagKey.create(Registries.ENTITY_TYPE, tagId);
    }

    @Unique
    private ResourceLocation ftbquestsentityvis$resolveTagFirstEntity(TagKey<EntityType<?>> tag) {
        if (tag == null) {
            return null;
        }
        Optional<EntityType<?>> first = BuiltInRegistries.ENTITY_TYPE.getTag(tag)
                .flatMap(set -> set.stream().findFirst())
                .map(holder -> holder.value());
        return first.map(BuiltInRegistries.ENTITY_TYPE::getKey).orElse(null);
    }

    public Icon getAltIcon() {
        if (toObserve != null && !toObserve.isEmpty()) {
            String typeName = ObserveTypeAccess.nameOf(this);
            IKillTaskVisOptions opts = (IKillTaskVisOptions) this;
            Task self = (Task) (Object) this;
            BooleanSupplier silhouette = ClientStateUtil.silhouetteCheck(self, opts.ftbquestsentityvis$getSilhouetteMode());
            ResourceLocation entityId = null;

            if (ObserveTypeAccess.ENTITY_TYPE.equals(typeName)) {
                ResourceLocation rl = ResourceLocation.tryParse(toObserve);
                if (rl != null && BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) {
                    entityId = rl;
                }
            } else if (ObserveTypeAccess.ENTITY_TYPE_TAG.equals(typeName)) {
                TagKey<EntityType<?>> tag = ftbquestsentityvis$resolveTag(toObserve);
                if (tag != null
                        && opts.ftbquestsentityvis$getTagCycleMode().resolve(Config.tagCycle)
                        && !CyclingEntityIcon.entitiesIn(tag).isEmpty()) {
                    return new CyclingEntityIcon(
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
                    );
                }
                entityId = ftbquestsentityvis$resolveTagFirstEntity(tag);
            }

            if (entityId != null) {
                return new EntityIcon(
                        entityId,
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
                );
            }
        }
        return getType().getIconSupplier();
    }
}
