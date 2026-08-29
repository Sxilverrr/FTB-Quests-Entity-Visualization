package com.sxilverr.ftbquestsentityvis.client;

import com.sxilverr.ftbquestsentityvis.duck.SilhouetteMode;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public final class ClientStateUtil {
    private ClientStateUtil() {
    }

    public static BooleanSupplier silhouetteCheck(Task task, SilhouetteMode mode) {
        return () -> shouldRenderSilhouette(task, mode);
    }

    public static boolean shouldRenderSilhouette(Task task, SilhouetteMode mode) {
        if (mode == null || mode == SilhouetteMode.NONE || task == null) {
            return false;
        }
        TeamData data = selfTeamData();
        if (data == null) {
            return false;
        }
        try {
            switch (mode) {
                case UNTIL_COMPLETED:
                    return !data.isCompleted(task);
                case UNTIL_AVAILABLE: {
                    Quest quest = task.getQuest();
                    return quest != null && !data.areDependenciesComplete(quest);
                }
                default:
                    return false;
            }
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static BooleanSupplier silhouetteCheck(Quest quest, SilhouetteMode mode) {
        return () -> shouldRenderSilhouette(quest, mode);
    }

    public static boolean shouldRenderSilhouette(Quest quest, SilhouetteMode mode) {
        if (mode == null || mode == SilhouetteMode.NONE || quest == null) {
            return false;
        }
        TeamData data = selfTeamData();
        if (data == null) {
            return false;
        }
        try {
            switch (mode) {
                case UNTIL_COMPLETED:
                    return !data.isCompleted(quest);
                case UNTIL_AVAILABLE:
                    return !data.areDependenciesComplete(quest);
                default:
                    return false;
            }
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static TeamData selfTeamData() {
        ClientQuestFile file = ClientQuestFile.INSTANCE;
        return file == null ? null : file.selfTeamData;
    }
}
