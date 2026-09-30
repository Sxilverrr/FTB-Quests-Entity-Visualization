package com.sxilverr.ftbquestsentityvis.client;

import dev.ftb.mods.ftblibrary.icon.CombinedIcon;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.IconAnimation;
import net.minecraft.client.gui.GuiGraphics;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class QuestSizeWrappedIcon extends Icon {
    private static Float override;

    private final Icon delegate;
    private final float questSize;

    public QuestSizeWrappedIcon(Icon delegate, float questSize) {
        this.delegate = delegate;
        this.questSize = questSize;
    }

    public static Icon wrapIfNeeded(Icon icon, float questSize) {
        return icon != null && containsEntity(icon) ? new QuestSizeWrappedIcon(icon, questSize) : icon;
    }

    public static boolean containsEntity(Icon icon) {
        return icon instanceof EntityIcon || icon instanceof CyclingEntityIcon
                || icon instanceof QuestSizeWrappedIcon wrapped && containsEntity(wrapped.delegate)
                || icon instanceof IconAnimation animation && animation.list.stream().anyMatch(QuestSizeWrappedIcon::containsEntity)
                || icon instanceof CombinedIcon combined && combined.list.stream().anyMatch(QuestSizeWrappedIcon::containsEntity);
    }

    public static float resolveSize(float size) {
        return override != null ? override : size;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int w, int h) {
        override = questSize;
        try {
            delegate.draw(graphics, x, y, w, h);
        } finally {
            override = null;
        }
    }

    @Override
    public Object getIngredient() {
        return delegate.getIngredient();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof QuestSizeWrappedIcon other
                && Float.compare(other.questSize, questSize) == 0
                && other.delegate.equals(delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode() * 31 + Float.hashCode(questSize);
    }
}
