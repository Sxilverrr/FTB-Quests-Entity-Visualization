package com.sxilverr.ftbquestsentityvis;

import dev.ftb.mods.ftbquests.quest.task.ObservationTask;

import java.lang.reflect.Field;

public final class ObserveTypeAccess {
    public static final String ENTITY_TYPE = "ENTITY_TYPE";
    public static final String ENTITY_TYPE_TAG = "ENTITY_TYPE_TAG";

    private static volatile Field observeTypeField;

    private ObserveTypeAccess() {
    }

    public static String nameOf(Object task) {
        try {
            Field f = observeTypeField;
            if (f == null) {
                f = ObservationTask.class.getDeclaredField("observeType");
                f.setAccessible(true);
                observeTypeField = f;
            }
            Object value = f.get(task);
            return value instanceof Enum<?> e ? e.name() : null;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
