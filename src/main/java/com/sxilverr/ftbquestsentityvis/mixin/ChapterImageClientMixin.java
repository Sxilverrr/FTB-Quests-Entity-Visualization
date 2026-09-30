package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.client.EntityIcon;
import com.sxilverr.ftbquestsentityvis.client.EntityIconScreen;
import com.sxilverr.ftbquestsentityvis.duck.IEntityVis;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.ChapterImage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChapterImage.class)
public abstract class ChapterImageClientMixin {
    @Shadow(remap = false) private double width;
    @Shadow(remap = false) private double height;
    @Shadow(remap = false) private int order;
    @Shadow(remap = false) private boolean editorsOnly;
    //? if <1.21.1 {
    @Shadow(remap = false) @Final private List<String> hover;
    //?}

    @Unique private EntityIcon ftbquestsentityvis$cachedIcon;

    @Unique
    private EntityVisSettings ftbquestsentityvis$shown() {
        EntityVisSettings vis = ((IEntityVis) this).ftbquestsentityvis$vis();
        return vis.enabled && vis.entityId != null ? vis : null;
    }

    @Inject(method = "getImage", at = @At("HEAD"), cancellable = true, remap = false)
    private void ftbquestsentityvis$entityImage(CallbackInfoReturnable<Icon> cir) {
        EntityVisSettings vis = ftbquestsentityvis$shown();
        if (vis == null) {
            return;
        }
        if (ftbquestsentityvis$cachedIcon == null || !ftbquestsentityvis$cachedIcon.showing(vis)) {
            ftbquestsentityvis$cachedIcon = new EntityIcon(vis.entityId, vis, null);
        }
        cir.setReturnValue(ftbquestsentityvis$cachedIcon);
    }

    //? if >=1.21.1 {
    /*@Inject(method = "getAltTitle", at = @At("HEAD"), cancellable = true, remap = false)*/
    //?} else {
    @Inject(method = "getTitle", at = @At("HEAD"), cancellable = true, remap = false)
    //?}
    private void ftbquestsentityvis$entityTitle(CallbackInfoReturnable<Component> cir) {
        EntityVisSettings vis = ftbquestsentityvis$shown();
        if (vis != null) {
            ResourceLocation id = vis.entityId;
            cir.setReturnValue(Component.translatable("entity." + id.getNamespace() + "." + id.getPath()));
        }
    }

    @Inject(method = "fillConfigGroup", at = @At("HEAD"), cancellable = true, remap = false)
    private void ftbquestsentityvis$entityConfig(ConfigGroup config, CallbackInfo ci) {
        EntityVisSettings vis = ((IEntityVis) this).ftbquestsentityvis$vis();
        if (!vis.enabled) {
            return;
        }
        EntityIconScreen.addPicker(config, vis);
        EntityIconScreen.fill(config, vis, vis.entityId, false, false);
        config.addDouble("width", width, v -> width = v, 1.0D, 0.0D, Double.POSITIVE_INFINITY)
                .setNameKey("ftbquestsentityvis.config.width");
        config.addDouble("height", height, v -> height = v, 1.0D, 0.0D, Double.POSITIVE_INFINITY)
                .setNameKey("ftbquestsentityvis.config.height");
        config.addInt("order", order, v -> order = v, 0, Integer.MIN_VALUE, Integer.MAX_VALUE)
                .setNameKey("ftbquestsentityvis.config.order");
        config.addBool("dev", editorsOnly, v -> editorsOnly = v, false)
                .setNameKey("ftbquestsentityvis.config.dev");
        //? if <1.21.1 {
        config.addList("hover", hover, new StringConfig(), "")
                .setNameKey("ftbquestsentityvis.config.hover");
        //?}
        ci.cancel();
    }
}
