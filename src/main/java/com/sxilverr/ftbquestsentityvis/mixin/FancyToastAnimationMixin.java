package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.Config;
import com.sxilverr.ftbquestsentityvis.client.ToastEntityIcons;
import dev.ftb.mods.ftblibrary.icon.Icon;
import net.bivrik.fancytoasts.core.Color;
import net.bivrik.fancytoasts.platform.utility.AdvancementDisplay;
import net.bivrik.fancytoasts.platform.utility.GuiContext;
import net.bivrik.fancytoasts.utility.TypeBasedUVs;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.bivrik.fancytoasts.client.toast.animation.FancyToastAnimation", remap = false)
public abstract class FancyToastAnimationMixin {
    @Shadow protected AdvancementDisplay display;

    @Shadow private ResourceLocation textureLocation;

    @Shadow private TypeBasedUVs typeBasedUVs;

    @Shadow
    private Color getColor(float alpha) {
        throw new AssertionError();
    }

    @Inject(method = "drawIcon(Lnet/bivrik/fancytoasts/platform/utility/GuiContext;F)V",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void ftbquestsentityvis$drawEntityToastIcon(GuiContext context, float alpha, CallbackInfo ci) {
        if (!Config.toastEntityIcons) {
            return;
        }
        Icon icon = ToastEntityIcons.get(display);
        if (icon == null) {
            return;
        }
        context.drawGUITexture(textureLocation, 68, 0, 26, 26, typeBasedUVs.frame(), getColor(alpha).getARGB());
        icon.draw(context.guiGraphics(), 73, 5, 16, 16);
        ci.cancel();
    }
}
