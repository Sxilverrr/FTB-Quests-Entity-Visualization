package com.sxilverr.ftbquestsentityvis.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.NativeImage;
import com.sxilverr.ftbquestsentityvis.FTBQuestsEntityVisualization;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public final class PlayerSkins {
    private static final long[] RETRY_DELAYS_MS = {2_000L, 2_000L, 10_000L, 10_000L, 30_000L};
    private static final Map<String, Optional<ResourceLocation>> FILES = new HashMap<>();
    private static final Map<String, GameProfile> PROFILES = new ConcurrentHashMap<>();
    private static final Map<String, Long> NEXT_LOOKUP = new ConcurrentHashMap<>();
    private static final Map<String, Integer> FAILURES = new ConcurrentHashMap<>();

    public enum Status {
        READY,
        LOADING,
        RETRYING,
        FAILED
    }

    private PlayerSkins() {
    }

    public static Status status(String skin) {
        if (skin.isEmpty() || isFile(skin) || profile(skin) != null) {
            return Status.READY;
        }
        int failures = failures(skin);
        return failures == 0 ? Status.LOADING : failures > RETRY_DELAYS_MS.length ? Status.FAILED : Status.RETRYING;
    }

    public static int failures(String skin) {
        return FAILURES.getOrDefault(skin.toLowerCase(Locale.ROOT), 0);
    }

    //? if >=1.21.1 {
    /*public static net.minecraft.client.resources.PlayerSkin skin(String skin, boolean slim) {
        net.minecraft.client.resources.PlayerSkin.Model model = slim ? net.minecraft.client.resources.PlayerSkin.Model.SLIM : net.minecraft.client.resources.PlayerSkin.Model.WIDE;
        if (isFile(skin)) {
            return new net.minecraft.client.resources.PlayerSkin(file(skin).orElse(DefaultPlayerSkin.getDefaultTexture()), null, null, null, model, true);
        }
        GameProfile profile = profile(skin);
        return profile != null ? Minecraft.getInstance().getSkinManager().getInsecureSkin(profile)
                : new net.minecraft.client.resources.PlayerSkin(DefaultPlayerSkin.getDefaultTexture(), null, null, null, model, true);
    }

    private static void lookup(String key, String name) {
        UUID id = uuid(name);
        (id != null
                ? CompletableFuture.supplyAsync(() -> fetch(id), Util.backgroundExecutor())
                : SkullBlockEntity.fetchGameProfile(name).thenApplyAsync(found -> found.map(p -> textured(p) ? p : fetch(p.getId())).orElse(null), Util.backgroundExecutor())
        ).whenComplete((profile, error) -> store(key, profile));
    }

    private static GameProfile fetch(UUID id) {
        com.mojang.authlib.yggdrasil.ProfileResult result = Minecraft.getInstance().getMinecraftSessionService().fetchProfile(id, true);
        return result == null ? null : result.profile();
    }*/
    //?} else {
    public static ResourceLocation texture(String skin) {
        if (isFile(skin)) {
            return file(skin).orElse(DefaultPlayerSkin.getDefaultSkin());
        }
        GameProfile profile = profile(skin);
        return profile != null ? Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(profile) : DefaultPlayerSkin.getDefaultSkin();
    }

    public static boolean slim(String skin, boolean slim) {
        if (isFile(skin)) {
            return slim;
        }
        GameProfile profile = profile(skin);
        MinecraftProfileTexture texture = profile == null ? null
                : Minecraft.getInstance().getSkinManager().getInsecureSkinInformation(profile).get(MinecraftProfileTexture.Type.SKIN);
        return texture != null && "slim".equals(texture.getMetadata("model"));
    }

    private static void lookup(String key, String name) {
        UUID id = uuid(name);
        if (id == null) {
            SkullBlockEntity.updateGameprofile(new GameProfile(null, name), profile -> store(key, profile));
            return;
        }
        CompletableFuture.supplyAsync(() -> Minecraft.getInstance().getMinecraftSessionService().fillProfileProperties(new GameProfile(id, null), true), Util.backgroundExecutor())
                .whenComplete((profile, error) -> store(key, profile));
    }
    //?}

    private static UUID uuid(String value) {
        String hex = value.replace("-", "");
        if (hex.length() != 32 || !hex.chars().allMatch(c -> Character.digit(c, 16) >= 0)) {
            return null;
        }
        return UUID.fromString(hex.replaceFirst("(.{8})(.{4})(.{4})(.{4})(.{12})", "$1-$2-$3-$4-$5"));
    }

    private static GameProfile profile(String name) {
        String key = name.toLowerCase(Locale.ROOT);
        GameProfile profile = PROFILES.get(key);
        if (profile == null && System.currentTimeMillis() >= NEXT_LOOKUP.getOrDefault(key, 0L)) {
            NEXT_LOOKUP.put(key, Long.MAX_VALUE);
            lookup(key, name);
        }
        return profile;
    }

    private static void store(String key, GameProfile profile) {
        if (profile != null && textured(profile)) {
            PROFILES.put(key, profile);
            FAILURES.remove(key);
            return;
        }
        int failures = FAILURES.merge(key, 1, Integer::sum);
        NEXT_LOOKUP.put(key, failures <= RETRY_DELAYS_MS.length ? System.currentTimeMillis() + RETRY_DELAYS_MS[failures - 1] : Long.MAX_VALUE);
    }

    private static boolean textured(GameProfile profile) {
        return profile.getProperties().containsKey("textures");
    }

    private static boolean isFile(String skin) {
        return skin.toLowerCase(Locale.ROOT).endsWith(".png");
    }

    private static Optional<ResourceLocation> file(String name) {
        return FILES.computeIfAbsent(name, PlayerSkins::load);
    }

    private static Optional<ResourceLocation> load(String name) {
        Path dir = Minecraft.getInstance().gameDirectory.toPath().toAbsolutePath().normalize()
                .resolve("config").resolve(FTBQuestsEntityVisualization.MODID).resolve("skins");
        Path file = dir.resolve(name).normalize();
        if (!file.startsWith(dir)) {
            return Optional.empty();
        }
        try {
            Files.createDirectories(dir);
            try (InputStream in = Files.newInputStream(file)) {
                ResourceLocation id = ResourceLocation.tryParse(FTBQuestsEntityVisualization.MODID + ":skins/"
                        + name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_"));
                Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(NativeImage.read(in)));
                return Optional.of(id);
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
