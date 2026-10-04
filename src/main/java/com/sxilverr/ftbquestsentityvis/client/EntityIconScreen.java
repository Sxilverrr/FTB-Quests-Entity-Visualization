package com.sxilverr.ftbquestsentityvis.client;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.FTBQuestsEntityVisualization;
import com.sxilverr.ftbquestsentityvis.duck.IEntityVis;
import com.sxilverr.ftbquestsentityvis.duck.OverrideMode;
import com.sxilverr.ftbquestsentityvis.duck.SilhouetteMode;
import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import dev.ftb.mods.ftbquests.net.EditObjectMessage;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.ChapterImage;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public final class EntityIconScreen {
    private static final String KEY = "ftbquestsentityvis.config.";
    private static final String TASK_TITLE = "ftbquestsentityvis.edit_entity_icon";
    private static final String QUEST_TITLE = "ftbquestsentityvis.edit_quest_entity_icon";
    public static final String SHOW_TITLE = "ftbquestsentityvis.show_entity";

    private EntityIconScreen() {
    }

    public static void editIcon(QuestScreen screen, QuestObjectBase object, EntityVisSettings icon) {
        boolean task = object instanceof Task;
        EntityVisSettings edit = icon.copy();
        edit.enabled = true;
        if (edit.entityId == null) {
            edit.entityId = EntityVisSettings.DEFAULT_ENTITY;
        }
        open(task ? TASK_TITLE : QUEST_TITLE, accepted -> apply(screen, object, icon, edit, accepted), g -> {
            addPicker(g, edit);
            fill(g, edit, edit.entityId, true, false);
            if (task) {
                addQuestIconToggle(g, edit);
            }
        });
    }

    public static void removeIcon(QuestScreen screen, QuestObjectBase object, EntityVisSettings icon) {
        icon.enabled = false;
        save(screen, object);
    }

    public static void editEntityTask(QuestScreen screen, Task task) {
        IEntityVis host = (IEntityVis) task;
        EntityVisSettings vis = host.ftbquestsentityvis$vis();
        EntityVisSettings edit = vis.copy();
        open(TASK_TITLE, accepted -> apply(screen, task, vis, edit, accepted), g -> {
            fill(g, edit, host.ftbquestsentityvis$visEntity(), true, host.ftbquestsentityvis$visTag() != null);
            addQuestIconToggle(g, edit);
        });
    }

    public static void createImage(QuestScreen screen, Chapter chapter, double x, double y) {
        //? if >=1.21.1 {
        /*ChapterImage image = new ChapterImage(chapter.getQuestFile().newID(), chapter);*/
        //?} else {
        ChapterImage image = new ChapterImage(chapter);
        //?}
        EntityVisSettings vis = ((IEntityVis) (Object) image).ftbquestsentityvis$vis();
        vis.enabled = true;
        vis.entityId = EntityVisSettings.DEFAULT_ENTITY;
        image.setPosition(x, y);
        open(SHOW_TITLE, accepted -> {
            if (accepted) {
                chapter.addImage(image);
                send(chapter);
            }
            screen.openGui();
        }, g -> image.fillConfigGroup(g.getOrCreateSubgroup("entity_image")));
    }

    public static void open(String titleKey, ConfigCallback callback, Consumer<ConfigGroup> filler) {
        ConfigGroup group = new ConfigGroup(FTBQuestsEntityVisualization.MODID, callback).setNameKey(titleKey);
        filler.accept(group);
        new EditConfigScreen(group).openGui();
    }

    public static void send(QuestObjectBase object) {
        //? if >=1.21.1 {
        /*EditObjectMessage.sendToServer(object);*/
        //?} else {
        new EditObjectMessage(object).sendToServer();
        //?}
    }

    private static void apply(QuestScreen screen, QuestObjectBase object, EntityVisSettings target, EntityVisSettings edit, boolean accepted) {
        if (accepted) {
            target.set(edit);
            save(screen, object);
        } else {
            screen.openGui();
        }
    }

    private static void save(QuestScreen screen, QuestObjectBase object) {
        object.clearCachedData();
        send(object);
        screen.openGui();
    }

    public static int menuIndex(List<ContextMenuItem> menu, String key) {
        for (int i = 0; i < menu.size(); i++) {
            Component title = menu.get(i).getTitle();
            if (title != null && title.getContents() instanceof TranslatableContents tc && key.equals(tc.getKey())) {
                return i;
            }
        }
        return -1;
    }

    public static void addPicker(ConfigGroup config, EntityVisSettings s) {
        ResourceLocation def = EntityVisSettings.DEFAULT_ENTITY;
        config.addEnum("entity", s.entityId, v -> s.entityId = v,
                        NameMap.of(def, new ArrayList<>(BuiltInRegistries.ENTITY_TYPE.keySet()))
                                .nameKey(v -> "entity." + v.getNamespace() + "." + v.getPath())
                                .icon(v -> new EntityIcon(v, ""))
                                .create(), def)
                .setNameKey(KEY + "entity");
    }

    public static void fill(ConfigGroup config, EntityVisSettings s, ResourceLocation variantsFor, boolean progress, boolean tagTarget) {
        EntityVariants.addNbtControls(config, variantsFor, s.nbt, v -> s.nbt = v);
        config.addString("name_tag", s.nameTag, v -> s.nameTag = v == null ? "" : v, "").setNameKey(KEY + "name_tag");
        if (EntityType.getKey(EntityType.PLAYER).equals(variantsFor)) {
            config.addString("player_skin", s.skin, v -> s.skin = v == null ? "" : v.trim(), "").setNameKey(KEY + "player_skin");
            config.addBool("slim_arms", s.slimArms, v -> s.slimArms = v, false).setNameKey(KEY + "slim_arms");
        }
        number(config, "size", s.size, v -> s.size = v, 1.0D, 0.0D, 10.0D);
        number(config, "offset_x", s.offsetX, v -> s.offsetX = v, 0.0D, -2.0D, 2.0D);
        number(config, "offset_y", s.offsetY, v -> s.offsetY = v, 0.0D, -2.0D, 2.0D);
        number(config, "rotation", s.rotation, v -> s.rotation = v, 0.0D, -180.0D, 180.0D);
        mode(config, "spin_mode", s.spinMode, v -> s.spinMode = v);
        mode(config, "idle_mode", s.idleMode, v -> s.idleMode = v);
        mode(config, "walk_mode", s.walkMode, v -> s.walkMode = v);
        if (progress) {
            config.addEnum("silhouette_mode", s.silhouetteMode, v -> s.silhouetteMode = v,
                            NameMap.of(SilhouetteMode.NONE, List.of(SilhouetteMode.NONE, SilhouetteMode.UNTIL_AVAILABLE, SilhouetteMode.UNTIL_COMPLETED))
                                    .nameKey(v -> KEY + "silhouette_mode." + v.name().toLowerCase())
                                    .create(), SilhouetteMode.NONE)
                    .setNameKey(KEY + "silhouette_mode");
        } else {
            config.addBool("silhouette", s.silhouetteMode == SilhouetteMode.ALWAYS,
                            v -> s.silhouetteMode = v ? SilhouetteMode.ALWAYS : SilhouetteMode.NONE, false)
                    .setNameKey(KEY + "silhouette");
        }
        if (tagTarget) {
            mode(config, "tag_cycle_mode", s.tagCycleMode, v -> s.tagCycleMode = v);
        }
        if (tagTarget || EntityNbt.cycles(s.nbt)) {
            number(config, "cycle_seconds", s.cycleSeconds, v -> s.cycleSeconds = v, 0.0D, 0.0D, 60.0D);
        }
    }

    private static void addQuestIconToggle(ConfigGroup config, EntityVisSettings s) {
        config.addBool("use_as_quest_icon", s.useAsQuestIcon, v -> s.useAsQuestIcon = v, false)
                .setNameKey(KEY + "use_as_quest_icon");
    }

    private static void number(ConfigGroup config, String key, float value, Consumer<Float> set, double def, double min, double max) {
        config.addDouble(key, value, v -> set.accept(v.floatValue()), def, min, max).setNameKey(KEY + key);
    }

    private static void mode(ConfigGroup config, String key, OverrideMode value, Consumer<OverrideMode> set) {
        config.addEnum(key, value, set, NameMap.of(OverrideMode.USE_GLOBAL, OverrideMode.values())
                        .nameKey(v -> KEY + key + "." + v.name().toLowerCase())
                        .create(), OverrideMode.USE_GLOBAL)
                .setNameKey(KEY + key);
    }
}
