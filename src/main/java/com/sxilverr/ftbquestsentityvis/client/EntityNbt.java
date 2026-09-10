package com.sxilverr.ftbquestsentityvis.client;

import com.mojang.brigadier.StringReader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class EntityNbt {
    private EntityNbt() {
    }

    public static List<String> split(String nbt) {
        String trimmed = nbt == null ? "" : nbt.trim();
        if (trimmed.isEmpty()) {
            return List.of();
        }
        if (!trimmed.startsWith("[")) {
            return List.of(trimmed);
        }
        try {
            Tag tag = new TagParser(new StringReader(trimmed)).readValue();
            if (tag instanceof ListTag list) {
                List<String> variants = new ArrayList<>(list.size());
                for (Tag element : list) {
                    if (element instanceof CompoundTag compound) {
                        variants.add(compound.isEmpty() ? "" : compound.toString());
                    }
                }
                return variants;
            }
        } catch (Throwable ignored) {
        }
        return List.of(trimmed);
    }

    public static String join(List<String> variants) {
        List<String> cleaned = new ArrayList<>(variants.size());
        for (String variant : variants) {
            cleaned.add(variant == null ? "" : variant.trim());
        }
        if (cleaned.isEmpty()) {
            return "";
        }
        if (cleaned.size() == 1) {
            return cleaned.get(0);
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < cleaned.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(cleaned.get(i).isEmpty() ? "{}" : cleaned.get(i));
        }
        return sb.append(']').toString();
    }

    public static int count(String nbt) {
        return split(nbt).size();
    }

    public static boolean cycles(String nbt) {
        return count(nbt) > 1;
    }

    public static CompoundTag parseCompound(String nbt) {
        String trimmed = nbt == null ? "" : nbt.trim();
        if (trimmed.isEmpty()) {
            return new CompoundTag();
        }
        try {
            return TagParser.parseTag(trimmed);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static boolean sameCompound(String a, String b) {
        String ta = a == null ? "" : a.trim();
        String tb = b == null ? "" : b.trim();
        if (ta.equals(tb)) {
            return true;
        }
        CompoundTag ca = parseCompound(ta);
        CompoundTag cb = parseCompound(tb);
        return ca != null && ca.equals(cb);
    }
}
