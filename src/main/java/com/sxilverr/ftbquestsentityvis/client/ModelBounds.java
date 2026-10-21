package com.sxilverr.ftbquestsentityvis.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ModelBounds implements VertexConsumer {
    private static final float MIN_EXTENT = 0.1F;

    float minY = Float.MAX_VALUE;
    float maxY = -Float.MAX_VALUE;
    private float halfWidth;

    private ModelBounds() {
    }

    public static ModelBounds measure(Entity entity) {
        ModelBounds bounds = new ModelBounds();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);
        try {
            dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, new PoseStack(), type -> bounds, LightTexture.FULL_BRIGHT);
        } catch (Throwable ignored) {
            return null;
        } finally {
            dispatcher.setRenderShadow(true);
        }
        return bounds.maxY < bounds.minY ? null : bounds;
    }

    public float fit(int w, int h) {
        return Math.min(h / Math.max(maxY - minY, MIN_EXTENT), w / Math.max(halfWidth * 2.0F, MIN_EXTENT));
    }

    private void add(double x, double y, double z) {
        minY = Math.min(minY, (float) y);
        maxY = Math.max(maxY, (float) y);
        halfWidth = Math.max(halfWidth, (float) Math.max(Math.abs(x), Math.abs(z)));
    }

    //? if >=1.21.1 {
    /*@Override
    public VertexConsumer addVertex(float x, float y, float z) {
        add(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        return this;
    }*/
    //?} else {
    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        add(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return this;
    }

    @Override
    public void endVertex() {
    }

    @Override
    public void defaultColor(int r, int g, int b, int a) {
    }

    @Override
    public void unsetDefaultColor() {
    }
    //?}
}
