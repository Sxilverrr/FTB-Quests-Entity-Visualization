package com.sxilverr.ftbquestsentityvis.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
//? if <1.21.1
import org.joml.Matrix4f;
import com.sxilverr.ftbquestsentityvis.Config;
import com.sxilverr.ftbquestsentityvis.EntityVisSettings;
import com.sxilverr.ftbquestsentityvis.duck.IEntityVis;
import com.sxilverr.ftbquestsentityvis.duck.SilhouetteMode;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.util.client.ClientTextComponentUtils;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class EntityIcon extends Icon {
    private static final long SPIN_PERIOD_MS = 15000L;
    private static final long ANIM_TICK_INTERVAL_MS = 50L;
    private static final float WALK_ANIM_SPEED = 0.6F;
    private static final float MIN_CYCLE_SECONDS = 0.1F;

    private final ResourceLocation entityId;
    private final EntityVisSettings settings;
    private final QuestObject owner;
    private final List<String> variants;
    private final Component nameTag;

    private Entity[] cachedEntities;
    private boolean[] creationFailed;
    private Level cachedLevel;
    private long lastAnimTickMs;

    public EntityIcon(ResourceLocation entityId, EntityVisSettings settings, QuestObject owner) {
        this.entityId = entityId;
        this.settings = settings.copy();
        this.owner = owner;
        List<String> split = EntityNbt.split(settings.nbt);
        this.variants = split.isEmpty() ? List.of("") : split;
        this.nameTag = settings.nameTag.isBlank() ? null : ClientTextComponentUtils.parse(settings.nameTag);
    }

    public EntityIcon(ResourceLocation entityId, String nbt) {
        this(entityId, preview(nbt), null);
    }

    private static EntityVisSettings preview(String nbt) {
        EntityVisSettings s = new EntityVisSettings();
        s.nbt = nbt;
        return s;
    }

    public static Icon forTask(Task task, IEntityVis host) {
        EntityVisSettings s = host.ftbquestsentityvis$vis();
        TagKey<EntityType<?>> tag = host.ftbquestsentityvis$visTag();
        List<ResourceLocation> members = CyclingEntityIcon.entitiesIn(tag);
        if (!members.isEmpty() && s.tagCycleMode.resolve(Config.tagCycle)) {
            return new CyclingEntityIcon(tag, s, task);
        }
        ResourceLocation id = tag == null ? host.ftbquestsentityvis$visEntity() : members.isEmpty() ? null : members.get(0);
        return id == null ? null : new EntityIcon(id, s, task);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static NameMap.Builder withEntityIcons(NameMap.Builder builder, Function original) {
        return builder.icon((Function<Object, Icon>) v -> v instanceof ResourceLocation id ? new EntityIcon(id, "") : (Icon) original.apply(v));
    }

    public static int cycleIndex(float cycleSeconds, int count) {
        if (count <= 1) {
            return 0;
        }
        float seconds = Math.max(cycleSeconds > 0.0F ? cycleSeconds : (float) Config.tagCycleSeconds, MIN_CYCLE_SECONDS);
        return (int) Math.floorMod(System.currentTimeMillis() / (long) (seconds * 1000.0F), (long) count);
    }

    public boolean showing(EntityVisSettings s) {
        return settings.equals(s);
    }

    private boolean silhouette() {
        SilhouetteMode mode = settings.silhouetteMode;
        if (mode == SilhouetteMode.ALWAYS) {
            return true;
        }
        TeamData data = ClientQuestFile.INSTANCE == null ? null : ClientQuestFile.INSTANCE.selfTeamData;
        if (mode == SilhouetteMode.NONE || owner == null || data == null) {
            return false;
        }
        try {
            if (mode == SilhouetteMode.UNTIL_COMPLETED) {
                return !data.isCompleted(owner);
            }
            Quest quest = owner instanceof Task task ? task.getQuest() : (Quest) owner;
            return quest != null && !data.areDependenciesComplete(quest);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private Entity getEntity() {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return null;
        }
        if (cachedLevel != level || cachedEntities == null) {
            cachedLevel = level;
            cachedEntities = new Entity[variants.size()];
            creationFailed = new boolean[variants.size()];
            lastAnimTickMs = 0L;
        }
        int index = cycleIndex(settings.cycleSeconds, variants.size());
        if (cachedEntities[index] != null || creationFailed[index]) {
            return cachedEntities[index];
        }
        try {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
            Entity created = type == EntityType.PLAYER ? player(level) : type == null ? null : type.create(level);
            if (created != null) {
                applyNbt(created, variants.get(index));
                if (nameTag != null) {
                    created.setCustomName(nameTag);
                }
                cachedEntities[index] = created;
                return created;
            }
        } catch (Throwable ignored) {
        }
        creationFailed[index] = true;
        return null;
    }

    private Entity player(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (!(level instanceof ClientLevel clientLevel) || mc.player == null) {
            return null;
        }
        String skin = settings.skin.trim();
        boolean slim = settings.slimArms;
        return new RemotePlayer(clientLevel, mc.player.getGameProfile()) {
            {
                getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7F);
            }

            @Override
            public boolean isInvisibleTo(Player player) {
                return true;
            }

            //? if >=1.21.1 {
            /*@Override
            public net.minecraft.client.resources.PlayerSkin getSkin() {
                return skin.isEmpty() ? super.getSkin() : PlayerSkins.skin(skin, slim);
            }*/
            //?} else {
            @Override
            public ResourceLocation getSkinTextureLocation() {
                return skin.isEmpty() ? super.getSkinTextureLocation() : PlayerSkins.texture(skin);
            }

            @Override
            public String getModelName() {
                return skin.isEmpty() ? super.getModelName() : PlayerSkins.slim(skin, slim) ? "slim" : "default";
            }
            //?}
        };
    }

    private static void applyNbt(Entity entity, String variant) {
        if (variant.isEmpty()) {
            return;
        }
        try {
            CompoundTag tag = TagParser.parseTag(variant);
            if (tag.isEmpty()) {
                return;
            }
            entity.load(tag);
            entity.setPos(0.0D, 0.0D, 0.0D);
            entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
        } catch (Throwable ignored) {
        }
    }

    private void drawFallback(GuiGraphics graphics, int x, int y, int w, int h) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        SpawnEggItem egg = type != null ? SpawnEggItem.byId(type) : null;
        ItemIcon.getItemIcon(egg != null ? egg : Items.SPAWNER).draw(graphics, x, y, w, h);
        GuiHelper.setupDrawing();
    }

    private void advanceAnimations(Entity entity) {
        boolean idle = settings.idleMode.resolve(Config.idleAnimation);
        boolean walk = settings.walkMode.resolve(Config.walkAnimation);
        if (!idle && !walk) {
            return;
        }
        long now = System.currentTimeMillis();
        if (lastAnimTickMs == 0L) {
            lastAnimTickMs = now;
            return;
        }
        long elapsed = now - lastAnimTickMs;
        if (elapsed < ANIM_TICK_INTERVAL_MS) {
            return;
        }
        long ticks;
        if (elapsed > ANIM_TICK_INTERVAL_MS * 4L) {
            ticks = 1L;
            lastAnimTickMs = now;
        } else {
            ticks = elapsed / ANIM_TICK_INTERVAL_MS;
            lastAnimTickMs += ticks * ANIM_TICK_INTERVAL_MS;
        }
        LivingEntity living = entity instanceof LivingEntity le ? le : null;
        for (long i = 0; i < ticks; i++) {
            if (idle) {
                entity.tickCount++;
            }
            if (walk && living != null) {
                living.walkAnimation.update(WALK_ANIM_SPEED, 1.0F);
            }
        }
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int w, int h) {
        Entity entity = getEntity();
        if (entity == null) {
            drawFallback(graphics, x, y, w, h);
            return;
        }

        advanceAnimations(entity);

        float effectiveSize = QuestSizeWrappedIcon.resolveSize(settings.size);
        float bbHeight = Math.max(entity.getBbHeight(), 0.1F);
        float bbWidth = Math.max(entity.getBbWidth(), 0.1F);
        float scale = Math.min(h / bbHeight, w / bbWidth) * Math.max(effectiveSize, 0.01F);
        if (scale <= 0.0F) {
            drawFallback(graphics, x, y, w, h);
            return;
        }

        double cx = x + w / 2.0 + settings.offsetX * w;
        double cy = y + h / 2.0 + bbHeight * scale / 2.0 - settings.offsetY * h;

        float spin = settings.spinMode.resolve(Config.mobsSpin)
                ? (System.currentTimeMillis() % SPIN_PERIOD_MS) / (float) SPIN_PERIOD_MS * 360.0F * (float) Config.spinSpeed
                : 0.0F;
        float yaw = spin + settings.rotation;
        float tilt = (float) Config.tiltDegrees;

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(cx, cy, 64.0);
        //? if >=1.21.1 {
        /*pose.scale(scale, scale, -scale);*/
        //?} else {
        Matrix4f poseMatrix = pose.last().pose();
        float axisX = (float) Math.sqrt(poseMatrix.m00() * poseMatrix.m00()
                + poseMatrix.m10() * poseMatrix.m10()
                + poseMatrix.m20() * poseMatrix.m20());
        float axisZ = (float) Math.sqrt(poseMatrix.m02() * poseMatrix.m02()
                + poseMatrix.m12() * poseMatrix.m12()
                + poseMatrix.m22() * poseMatrix.m22());
        float depthFix = axisZ > 1.0E-5F ? axisX / axisZ : 1.0F;
        pose.scale(scale, scale, -scale * depthFix);
        //?}
        pose.mulPose(Axis.XP.rotationDegrees(180.0F + tilt));
        pose.mulPose(Axis.YP.rotationDegrees(yaw));

        LivingEntity living = entity instanceof LivingEntity le ? le : null;
        float prevYRot = entity.getYRot();
        float prevXRot = entity.getXRot();
        float prevYBodyRot = 0.0F;
        float prevYHeadRotO = 0.0F;
        float prevYHeadRot = 0.0F;
        if (living != null) {
            prevYBodyRot = living.yBodyRot;
            prevYHeadRotO = living.yHeadRotO;
            prevYHeadRot = living.yHeadRot;
            living.yBodyRot = 0.0F;
            living.yHeadRot = 0.0F;
            living.yHeadRotO = 0.0F;
        }
        entity.setYRot(0.0F);
        entity.setXRot(0.0F);

        boolean silhouette = silhouette();
        int packedLight = silhouette || Config.fullBright ? LightTexture.FULL_BRIGHT : 15728640;

        //? if <1.21.1 {
        float[] prevShaderColor = RenderSystem.getShaderColor().clone();
        //?}

        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);
        try {
            //? if >=1.21.1 {
            /*if (silhouette) {
                RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
            }*/
            //?} else {
            float channel = silhouette ? 0.0F : 1.0F;
            RenderSystem.setShaderColor(channel, channel, channel, 1.0F);
            //?}
            RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, pose, graphics.bufferSource(), packedLight));
            graphics.flush();
        } catch (Throwable ignored) {
        } finally {
            //? if >=1.21.1 {
            /*if (silhouette) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }*/
            //?} else {
            RenderSystem.setShaderColor(prevShaderColor[0], prevShaderColor[1], prevShaderColor[2], prevShaderColor[3]);
            //?}
            dispatcher.setRenderShadow(true);
            pose.popPose();
            Lighting.setupFor3DItems();
            entity.setYRot(prevYRot);
            entity.setXRot(prevXRot);
            if (living != null) {
                living.yBodyRot = prevYBodyRot;
                living.yHeadRotO = prevYHeadRotO;
                living.yHeadRot = prevYHeadRot;
            }
            GuiHelper.setupDrawing();
        }

        if (nameTag != null && !silhouette) {
            drawNameTag(graphics, cx, cy - (bbHeight + 0.5F) * scale * Math.cos(Math.toRadians(tilt)), scale * 0.025F);
        }

        if (entity instanceof Player) {
            String skin = settings.skin.trim();
            PlayerSkins.Status status = PlayerSkins.status(skin);
            if (status != PlayerSkins.Status.READY) {
                Component text = Component.translatable("ftbquestsentityvis.skin." + status.name().toLowerCase(), PlayerSkins.failures(skin));
                boolean waiting = status != PlayerSkins.Status.FAILED;
                drawCaption(graphics, text, waiting ? ".".repeat(1 + (int) (System.currentTimeMillis() / 400L % 3L)) : "",
                        waiting ? 0xFFFFFF : 0xFF5555, x, y, w, h);
            }
        }
    }

    private void drawNameTag(GuiGraphics graphics, double x, double y, float scale) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.font.width(nameTag);
        int left = -width / 2;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 100.0);
        pose.scale(scale, scale, 1.0F);
        graphics.fill(left - 1, -1, left + width + 1, 9, (int) (mc.options.getBackgroundOpacity(0.25F) * 255.0F) << 24);
        graphics.drawString(mc.font, nameTag, left, 0, 0xFFFFFF, false);
        pose.popPose();
        GuiHelper.setupDrawing();
    }

    private static void drawCaption(GuiGraphics graphics, Component text, String dots, int color, int x, int y, int w, int h) {
        Font font = Minecraft.getInstance().font;
        int dotsWidth = dots.isEmpty() ? 0 : font.width("...");
        float scale = h / (font.lineHeight * 8.0F);
        List<FormattedCharSequence> lines = font.split(text, Math.max(1, (int) (w / scale) - dotsWidth));
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + w / 2.0F, y + h - lines.size() * font.lineHeight * scale, 300.0F);
        pose.scale(scale, scale, 1.0F);
        for (int i = 0; i < lines.size(); i++) {
            FormattedCharSequence line = lines.get(i);
            boolean last = i == lines.size() - 1;
            int left = -(font.width(line) + (last ? dotsWidth : 0)) / 2;
            graphics.drawString(font, line, left, i * font.lineHeight, color, true);
            if (last && !dots.isEmpty()) {
                graphics.drawString(font, dots, left + font.width(line), i * font.lineHeight, color, true);
            }
        }
        pose.popPose();
        GuiHelper.setupDrawing();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EntityIcon other && other.entityId.equals(entityId) && other.settings.equals(settings);
    }

    @Override
    public int hashCode() {
        return entityId.hashCode() * 31 + settings.hashCode();
    }

    @Override
    public String toString() {
        return "entity:" + entityId + settings.nbt.trim();
    }
}
