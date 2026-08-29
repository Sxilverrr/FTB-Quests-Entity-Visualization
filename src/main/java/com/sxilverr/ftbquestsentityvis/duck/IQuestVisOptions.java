package com.sxilverr.ftbquestsentityvis.duck;

import net.minecraft.resources.ResourceLocation;

public interface IQuestVisOptions {
    float ftbquestsentityvis$getQuestVisSize();

    void ftbquestsentityvis$setQuestVisSize(float size);

    boolean ftbquestsentityvis$getQuestIconEntityEnabled();

    void ftbquestsentityvis$setQuestIconEntityEnabled(boolean enabled);

    ResourceLocation ftbquestsentityvis$getQuestIconEntityId();

    void ftbquestsentityvis$setQuestIconEntityId(ResourceLocation id);

    float ftbquestsentityvis$getQuestIconVisSize();

    void ftbquestsentityvis$setQuestIconVisSize(float size);

    float ftbquestsentityvis$getQuestIconVisOffsetX();

    void ftbquestsentityvis$setQuestIconVisOffsetX(float offset);

    float ftbquestsentityvis$getQuestIconVisOffsetY();

    void ftbquestsentityvis$setQuestIconVisOffsetY(float offset);

    float ftbquestsentityvis$getQuestIconVisRotation();

    void ftbquestsentityvis$setQuestIconVisRotation(float rotation);

    OverrideMode ftbquestsentityvis$getQuestIconSpinMode();

    void ftbquestsentityvis$setQuestIconSpinMode(OverrideMode mode);

    OverrideMode ftbquestsentityvis$getQuestIconIdleMode();

    void ftbquestsentityvis$setQuestIconIdleMode(OverrideMode mode);

    OverrideMode ftbquestsentityvis$getQuestIconWalkMode();

    void ftbquestsentityvis$setQuestIconWalkMode(OverrideMode mode);

    SilhouetteMode ftbquestsentityvis$getQuestIconSilhouetteMode();

    void ftbquestsentityvis$setQuestIconSilhouetteMode(SilhouetteMode mode);

    String ftbquestsentityvis$getQuestIconNbt();

    void ftbquestsentityvis$setQuestIconNbt(String nbt);

    boolean ftbquestsentityvis$isQuestIconDirty();

    void ftbquestsentityvis$setQuestIconDirty(boolean dirty);
}
