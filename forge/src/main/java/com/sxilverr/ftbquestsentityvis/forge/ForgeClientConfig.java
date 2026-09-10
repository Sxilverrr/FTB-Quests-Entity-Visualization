package com.sxilverr.ftbquestsentityvis.forge;

import com.sxilverr.ftbquestsentityvis.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ForgeClientConfig {
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.BooleanValue MOBS_SPIN;
    private static final ForgeConfigSpec.DoubleValue SPIN_SPEED;
    private static final ForgeConfigSpec.DoubleValue TILT_DEGREES;
    private static final ForgeConfigSpec.BooleanValue FULL_BRIGHT;
    private static final ForgeConfigSpec.BooleanValue IDLE_ANIMATION;
    private static final ForgeConfigSpec.BooleanValue WALK_ANIMATION;
    private static final ForgeConfigSpec.BooleanValue TAG_CYCLE;
    private static final ForgeConfigSpec.DoubleValue TAG_CYCLE_SECONDS;
    private static final ForgeConfigSpec.BooleanValue TOAST_ENTITY_ICONS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        MOBS_SPIN = builder.comment("Do mobs spin?").define("mobsSpin", true);
        SPIN_SPEED = builder.comment("Spin speed multiplier.").defineInRange("spinSpeed", 1.0, 0.0, 10.0);
        TILT_DEGREES = builder.comment("Camera tilt in degrees.").defineInRange("tiltDegrees", 15.0, -90.0, 90.0);
        FULL_BRIGHT = builder.comment("Render mobs at full brightness?").define("fullBright", true);
        IDLE_ANIMATION = builder.comment("Should mobs play idle animation?").define("idleAnimation", true);
        WALK_ANIMATION = builder.comment("Should mobs play walk animation?").define("walkAnimation", false);
        TAG_CYCLE = builder.comment("Cycle through every entity of an entity type tag?").define("tagCycle", true);
        TAG_CYCLE_SECONDS = builder.comment("Seconds each entity of a tag, or each NBT variant, is shown before cycling.").defineInRange("tagCycleSeconds", 2.0, 0.1, 60.0);
        TOAST_ENTITY_ICONS = builder.comment("Render entity icons inside Fancy Toasts quest notifications?").define("toastEntityIcons", true);
        SPEC = builder.build();
    }

    private ForgeClientConfig() {
    }

    public static void sync() {
        Config.mobsSpin = MOBS_SPIN.get();
        Config.spinSpeed = SPIN_SPEED.get();
        Config.tiltDegrees = TILT_DEGREES.get();
        Config.fullBright = FULL_BRIGHT.get();
        Config.idleAnimation = IDLE_ANIMATION.get();
        Config.walkAnimation = WALK_ANIMATION.get();
        Config.tagCycle = TAG_CYCLE.get();
        Config.tagCycleSeconds = TAG_CYCLE_SECONDS.get();
        Config.toastEntityIcons = TOAST_ENTITY_ICONS.get();
    }
}
