package com.sxilverr.ftbquestsentityvis.mixin;

import com.sxilverr.ftbquestsentityvis.client.EntityIconLookup;
import com.sxilverr.ftbquestsentityvis.client.ToastEntityIcons;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.client.gui.ToastQuestObject;
import net.bivrik.fancytoasts.platform.utility.AdvancementDisplay;
import net.minecraft.client.gui.components.toasts.Toast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.bivrik.fancytoasts.compat.FTBQuestsCompat", remap = false)
public class FTBQuestsCompatMixin {
    @Inject(method = "getDisplayInfo", at = @At("RETURN"), require = 0)
    private static void ftbquestsentityvis$captureEntityIcon(Toast toast, CallbackInfoReturnable<AdvancementDisplay> cir) {
        AdvancementDisplay display = cir.getReturnValue();
        if (display == null || !(toast instanceof ToastQuestObject questToast)) {
            return;
        }
        try {
            Icon icon = questToast.getIcon();
            if (EntityIconLookup.contains(icon)) {
                ToastEntityIcons.put(display, icon);
            }
        } catch (Throwable ignored) {
        }
    }
}
