package com.sxilverr.ftbquestsentityvis.client;

import dev.ftb.mods.ftblibrary.icon.CombinedIcon;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.IconAnimation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class EntityIconLookup {
    private EntityIconLookup() {
    }

    public static boolean contains(Icon icon) {
        if (icon instanceof EntityIcon || icon instanceof CyclingEntityIcon) {
            return true;
        }
        if (icon instanceof QuestSizeWrappedIcon wrapped) {
            return contains(wrapped.getDelegate());
        }
        if (icon instanceof IconAnimation animation) {
            return containsAny(animation.list);
        }
        if (icon instanceof CombinedIcon combined) {
            return containsAny(combined.list);
        }
        return false;
    }

    private static boolean containsAny(List<Icon> icons) {
        for (Icon child : icons) {
            if (contains(child)) {
                return true;
            }
        }
        return false;
    }
}
