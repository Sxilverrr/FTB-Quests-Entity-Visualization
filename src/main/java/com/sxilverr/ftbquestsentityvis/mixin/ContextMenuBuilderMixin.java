package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.client.EntityIconScreen;
import com.sxilverr.ftbquestsentityvis.duck.IEntityIcon;
import com.sxilverr.ftbquestsentityvis.duck.IEntityVis;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftbquests.client.gui.ContextMenuBuilder;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(ContextMenuBuilder.class)
public abstract class ContextMenuBuilderMixin {
    @Unique private static final Icon ftbquestsentityvis$SHOW_ENTITY_ICON = ItemIcon.getItemIcon(Items.ENDER_EYE);
    @Unique private static final Icon ftbquestsentityvis$REMOVE_ICON = ItemIcon.getItemIcon(Items.BARRIER);

    @Shadow(remap = false) @Final private QuestObjectBase object;
    @Shadow(remap = false) @Final private QuestScreen screen;

    @ModifyArg(
            method = "openContextMenu",
            at = @At(value = "INVOKE",
                    target = "Ldev/ftb/mods/ftblibrary/ui/BaseScreen;openContextMenu(Ljava/util/List;)Ldev/ftb/mods/ftblibrary/ui/ContextMenu;",
                    remap = false),
            remap = false)
    private List<ContextMenuItem> ftbquestsentityvis$addEntityIconItems(List<ContextMenuItem> menu) {
        List<ContextMenuItem> items = new ArrayList<>();
        if (object instanceof Task task && task instanceof IEntityVis) {
            items.add(ftbquestsentityvis$item("edit_entity_icon", ftbquestsentityvis$SHOW_ENTITY_ICON, () -> EntityIconScreen.editEntityTask(screen, task)));
        } else if (object instanceof IEntityIcon host) {
            EntityVisSettings icon = host.ftbquestsentityvis$icon();
            String quest = object instanceof Task ? "" : "quest_";
            if (icon.enabled) {
                items.add(ftbquestsentityvis$item("edit_" + quest + "entity_icon", ftbquestsentityvis$SHOW_ENTITY_ICON, () -> EntityIconScreen.editIcon(screen, object, icon)));
                items.add(ftbquestsentityvis$item("remove_" + quest + "entity_icon", ftbquestsentityvis$REMOVE_ICON, () -> EntityIconScreen.removeIcon(screen, object, icon)));
            } else {
                items.add(ftbquestsentityvis$item("show_entity_as_" + quest + "icon", ftbquestsentityvis$SHOW_ENTITY_ICON, () -> EntityIconScreen.editIcon(screen, object, icon)));
            }
        } else {
            return menu;
        }

        int idx = EntityIconScreen.menuIndex(menu, "ftbquests.gui.use_as_quest_icon");
        if (idx < 0) {
            idx = EntityIconScreen.menuIndex(menu, "ftbquests.gui.edit");
        }
        if (idx < 0) {
            idx = menu.indexOf(ContextMenuItem.SEPARATOR);
        }
        List<ContextMenuItem> copy = new ArrayList<>(menu);
        copy.addAll(idx >= 0 ? idx + 1 : copy.size(), items);
        return copy;
    }

    @Unique
    private static ContextMenuItem ftbquestsentityvis$item(String key, Icon icon, Runnable action) {
        return new ContextMenuItem(Component.translatable("ftbquestsentityvis." + key), icon, b -> action.run());
    }
}
