package com.sxilverr.ftbquestsentityvis.client;

import dev.ftb.mods.ftblibrary.icon.Icon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@Environment(EnvType.CLIENT)
public final class ToastEntityIcons {
    private static final Map<Object, Icon> ICONS = Collections.synchronizedMap(new WeakHashMap<>());

    private ToastEntityIcons() {
    }

    public static void put(Object display, Icon icon) {
        if (display != null && icon != null) {
            ICONS.put(display, icon);
        }
    }

    public static Icon get(Object display) {
        return display == null ? null : ICONS.get(display);
    }
}
