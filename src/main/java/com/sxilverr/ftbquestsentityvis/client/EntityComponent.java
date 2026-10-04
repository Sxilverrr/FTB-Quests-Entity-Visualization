package com.sxilverr.ftbquestsentityvis.client;

import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.duck.OverrideMode;
import com.sxilverr.ftbquestsentityvis.duck.SilhouetteMode;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.util.client.ImageComponent;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class EntityComponent extends ImageComponent {
    public final EntityVisSettings vis = new EntityVisSettings();

    public EntityComponent() {
        vis.entityId = EntityVisSettings.DEFAULT_ENTITY;
        setCompWidth(100);
        setCompHeight(100);
        setCompAlign(ImageAlign.CENTER);
    }

    public static EntityComponent fromProperties(Map<String, String> map) {
        EntityComponent c = new EntityComponent();
        EntityVisSettings s = c.vis;
        ResourceLocation id = ResourceLocation.tryParse(map.get("entity"));
        if (id != null) {
            s.entityId = id;
        }
        s.size = num(map.get("ent_size"), 1.0F);
        s.offsetX = num(map.get("off_x"), 0.0F);
        s.offsetY = num(map.get("off_y"), 0.0F);
        s.rotation = num(map.get("rot"), 0.0F);
        s.spinMode = EntityVisSettings.enumOr(OverrideMode.USE_GLOBAL, map.getOrDefault("spin", ""));
        s.idleMode = EntityVisSettings.enumOr(OverrideMode.USE_GLOBAL, map.getOrDefault("idle", ""));
        s.walkMode = EntityVisSettings.enumOr(OverrideMode.USE_GLOBAL, map.getOrDefault("walk", ""));
        s.silhouetteMode = "true".equals(map.get("silhouette")) ? SilhouetteMode.ALWAYS : SilhouetteMode.NONE;
        s.nbt = decode(map.get("nbt"));
        s.cycleSeconds = num(map.get("cycle"), 0.0F);
        s.skin = decode(map.get("skin"));
        s.slimArms = "true".equals(map.get("slim"));
        s.nameTag = decode(map.get("name"));
        c.setCompWidth((int) num(map.get("width"), 100));
        c.setCompHeight((int) num(map.get("height"), 100));
        c.setCompAlign(alignByName(map.getOrDefault("align", "center")));
        c.setCompImage(new EntityIcon(s.entityId, s, null));
        return c;
    }

    public void fillConfig(ConfigGroup config) {
        EntityIconScreen.addPicker(config, vis);
        EntityIconScreen.fill(config, vis, vis.entityId, false, false);
        config.addInt("width", getCompWidth(), v -> setCompWidth(v), 100, 1, 1000)
                .setNameKey("ftbquestsentityvis.config.width");
        config.addInt("height", getCompHeight(), v -> setCompHeight(v), 100, 1, 1000)
                .setNameKey("ftbquestsentityvis.config.height");
        config.addEnum("align", getCompAlign(), v -> setCompAlign(v), ImageAlign.NAME_MAP, ImageAlign.CENTER)
                .setNameKey("ftbquestsentityvis.config.align");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{entity:");
        sb.append(vis.entityId);
        sb.append(" ent_size:").append(vis.size);
        sb.append(" off_x:").append(vis.offsetX);
        sb.append(" off_y:").append(vis.offsetY);
        sb.append(" rot:").append(vis.rotation);
        sb.append(" spin:").append(vis.spinMode.name());
        sb.append(" idle:").append(vis.idleMode.name());
        sb.append(" walk:").append(vis.walkMode.name());
        sb.append(" width:").append(getCompWidth());
        sb.append(" height:").append(getCompHeight());
        sb.append(" align:").append(alignName());
        if (vis.silhouetteMode == SilhouetteMode.ALWAYS) {
            sb.append(" silhouette:true");
        }
        if (!vis.nbt.isEmpty()) {
            sb.append(" nbt:").append(encode(vis.nbt));
        }
        if (vis.cycleSeconds > 0.0F) {
            sb.append(" cycle:").append(vis.cycleSeconds);
        }
        if (!vis.skin.isEmpty()) {
            sb.append(" skin:").append(encode(vis.skin));
        }
        if (vis.slimArms) {
            sb.append(" slim:true");
        }
        if (!vis.nameTag.isEmpty()) {
            sb.append(" name:").append(encode(vis.nameTag));
        }
        sb.append('}');
        return sb.toString();
    }

    private void setCompWidth(int v) {
        //? if >=1.21.1 {
        /*setWidth(v);*/
        //?} else {
        width = v;
        //?}
    }

    private int getCompWidth() {
        //? if >=1.21.1 {
        /*return getWidth();*/
        //?} else {
        return width;
        //?}
    }

    private void setCompHeight(int v) {
        //? if >=1.21.1 {
        /*setHeight(v);*/
        //?} else {
        height = v;
        //?}
    }

    private int getCompHeight() {
        //? if >=1.21.1 {
        /*return getHeight();*/
        //?} else {
        return height;
        //?}
    }

    private void setCompAlign(ImageAlign v) {
        //? if >=1.21.1 {
        /*setAlign(v);*/
        //?} else {
        align = v;
        //?}
    }

    private ImageAlign getCompAlign() {
        //? if >=1.21.1 {
        /*return getAlign();*/
        //?} else {
        return align;
        //?}
    }

    private void setCompImage(Icon v) {
        //? if >=1.21.1 {
        /*setImage(v);*/
        //?} else {
        image = v;
        //?}
    }

    private static ImageAlign alignByName(String s) {
        //? if >=1.21.1 {
        /*return ImageAlign.byName(s);*/
        //?} else {
        return ImageAlign.fromString(s);
        //?}
    }

    private String alignName() {
        return switch (getCompAlign()) {
            case LEFT -> "left";
            case RIGHT -> "right";
            default -> "center";
        };
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return "";
        }
        try {
            return new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return encoded;
        }
    }

    private static float num(String s, float def) {
        try {
            return s == null ? def : Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
