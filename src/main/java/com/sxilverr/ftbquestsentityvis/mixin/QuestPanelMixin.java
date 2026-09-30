package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.client.EntityIconScreen;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestPanel;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.stream.IntStream;

@Mixin(QuestPanel.class)
public abstract class QuestPanelMixin {
    @Unique private static final Icon ftbquestsentityvis$SHOW_ENTITY_ICON = ItemIcon.getItemIcon(Items.ENDER_EYE);

    @Shadow(remap = false) @Final private QuestScreen questScreen;
    @Shadow(remap = false) protected double questX;
    @Shadow(remap = false) protected double questY;

    @ModifyArg(
            method = "mousePressed",
            at = @At(value = "INVOKE",
                    target = "Ldev/ftb/mods/ftbquests/client/gui/quests/QuestScreen;openContextMenu(Ljava/util/List;)Ldev/ftb/mods/ftblibrary/ui/ContextMenu;",
                    remap = false),
            remap = false)
    private List<ContextMenuItem> ftbquestsentityvis$addShowEntity(List<ContextMenuItem> menu) {
        Chapter chapter = ((QuestScreenAccessor) (Object) questScreen).ftbquestsentityvis$getSelectedChapter();
        if (chapter == null) {
            return menu;
        }

        double qx = questX;
        double qy = questY;
        ContextMenuItem item = new ContextMenuItem(
                Component.translatable(EntityIconScreen.SHOW_TITLE),
                ftbquestsentityvis$SHOW_ENTITY_ICON,
                b -> EntityIconScreen.createImage(questScreen, chapter, qx, qy));

        Component killName = TaskTypes.KILL.getDisplayName();
        int kill = IntStream.range(0, menu.size()).filter(i -> killName.equals(menu.get(i).getTitle())).findFirst().orElse(-1);
        int separator = menu.indexOf(ContextMenuItem.SEPARATOR);
        menu.add(kill >= 0 ? kill + 1 : separator >= 0 ? separator : menu.size(), item);
        return menu;
    }
}
