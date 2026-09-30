package com.sxilverr.ftbquestsentityvis.client;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public final class EntityVariants {
    public record Variant(String label, String nbt) {
    }

    private static final String[] DYE_LABELS = {
            "White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray",
            "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"
    };

    private static final Variant DEFAULT = new Variant("Default", "");

    private EntityVariants() {
    }

    public static void addNbtControls(ConfigGroup config, ResourceLocation entityId, String current, Consumer<String> setNbt) {
        String original = current == null ? "" : current.trim();
        List<String> currentVariants = EntityNbt.split(original);

        config.addString("nbt", original, v -> {
            String nv = v == null ? "" : v.trim();
            if (!nv.equals(original)) {
                setNbt.accept(nv);
            }
        }, "").setNameKey("ftbquestsentityvis.config.nbt");

        List<Variant> variants = forEntity(entityId);
        if (variants.isEmpty()) {
            return;
        }

        List<Variant> pickable = with(List.of(DEFAULT), variants.toArray(Variant[]::new));

        List<Variant> options = new ArrayList<>(pickable);
        Variant selected;
        if (currentVariants.size() > 1) {
            selected = new Variant("Cycling (" + currentVariants.size() + ")", original);
            options.add(selected);
        } else {
            selected = match(pickable, currentVariants.isEmpty() ? "" : currentVariants.get(0));
            if (selected == null) {
                selected = new Variant("Custom", original);
                options.add(selected);
            }
        }

        Variant def = selected;
        config.add("variant", new VariantConfig(entityId, options, nameMap(entityId, def, options)), selected, v -> {
            if (!v.nbt().equals(original)) {
                setNbt.accept(v.nbt());
            }
        }, def)
                .setNameKey("ftbquestsentityvis.config.variant");

        List<Variant> cycle = new ArrayList<>();
        for (String variant : currentVariants) {
            Variant matched = match(pickable, variant);
            cycle.add(matched != null ? matched : new Variant("Custom", variant));
        }
        config.addList("variants", cycle, new VariantConfig(entityId, pickable, nameMap(entityId, DEFAULT, pickable)), list -> {
            String joined = EntityNbt.join(list.stream().map(Variant::nbt).toList());
            if (!joined.equals(original)) {
                setNbt.accept(joined);
            }
        }, DEFAULT)
                .setNameKey("ftbquestsentityvis.config.variants");
    }

    private static Variant match(List<Variant> options, String nbt) {
        return options.stream().filter(option -> EntityNbt.sameCompound(option.nbt(), nbt)).findFirst().orElse(null);
    }

    private static NameMap<Variant> nameMap(ResourceLocation entityId, Variant def, List<Variant> options) {
        return NameMap.of(def, options)
                .nameKey(Variant::label)
                .icon(v -> new EntityIcon(entityId, v.nbt()))
                .create();
    }

    public static List<Variant> forEntity(ResourceLocation entityId) {
        if (entityId == null) {
            return List.of();
        }
        List<Variant> list = new ArrayList<>(specificVariants(entityId));
        Entity probe = createProbe(entityId);
        if (probe instanceof LivingEntity) {
            list.add(nametag("Dinnerbone"));
            list.add(nametag("Grumm"));
            if (probe instanceof AgeableMob) {
                list.add(new Variant("Baby", "{Age:-24000}"));
            }
        }
        return list;
    }

    private static List<Variant> specificVariants(ResourceLocation entityId) {
        if (!"minecraft".equals(entityId.getNamespace())) {
            return List.of();
        }
        return switch (entityId.getPath()) {
            case "sheep" -> with(intVariants("Color", DYE_LABELS), nametag("jeb_"));
            case "shulker" -> intVariants("Color", DYE_LABELS);
            case "rabbit" -> with(intVariants("RabbitType", "Brown", "White", "Black", "Black & White", "Gold", "Salt & Pepper"),
                    new Variant("The Killer Bunny", "{RabbitType:99}"), nametag("Toast"));
            case "horse" -> intVariants("Variant", "White", "Creamy", "Chestnut", "Brown", "Black", "Gray", "Dark Brown");
            case "llama", "trader_llama" -> intVariants("Variant", "Creamy", "White", "Brown", "Gray");
            case "parrot" -> intVariants("Variant", "Red", "Blue", "Green", "Cyan", "Gray");
            case "axolotl" -> intVariants("Variant", "Lucy", "Wild", "Gold", "Cyan", "Blue");
            case "fox" -> mapped(v -> "{Type:\"" + v + "\"}", "red", "snow");
            case "mooshroom" -> mapped(v -> "{Type:\"" + v + "\"}", "red", "brown");
            case "cat" -> mapped(v -> "{variant:\"minecraft:" + v + "\"}", "tabby", "black", "red", "siamese",
                    "british_shorthair", "calico", "persian", "ragdoll", "white", "jellie", "all_black");
            case "panda" -> mapped(v -> "{MainGene:\"" + v + "\",HiddenGene:\"" + v + "\"}",
                    "normal", "lazy", "worried", "playful", "brown", "weak", "aggressive");
            case "villager", "zombie_villager" -> mapped(v -> "{VillagerData:{profession:\"minecraft:" + v + "\",level:1,type:\"minecraft:plains\"}}",
                    "none", "armorer", "butcher", "cartographer", "cleric", "farmer", "fisherman", "fletcher",
                    "leatherworker", "librarian", "mason", "nitwit", "shepherd", "toolsmith", "weaponsmith");
            case "slime", "magma_cube" -> intVariants("Size", "Tiny", "Small", "Medium", "Large");
            case "pufferfish" -> intVariants("PuffState", "Deflated", "Half Puffed", "Fully Puffed");
            case "snow_golem" -> List.of(new Variant("With Pumpkin", ""), new Variant("Sheared", "{Pumpkin:0b}"));
            case "creeper" -> List.of(new Variant("Normal", ""), new Variant("Charged", "{powered:1b}"));
            default -> List.of();
        };
    }

    private static Entity createProbe(ResourceLocation entityId) {
        Level level = Minecraft.getInstance().level;
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        try {
            return level == null || type == null ? null : type.create(level);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Variant nametag(String name) {
        return new Variant(name, "{CustomName:'\"" + name + "\"'}");
    }

    private static List<Variant> with(List<Variant> base, Variant... extra) {
        List<Variant> list = new ArrayList<>(base);
        list.addAll(Arrays.asList(extra));
        return list;
    }

    private static List<Variant> intVariants(String key, String... labels) {
        List<Variant> list = new ArrayList<>(labels.length);
        for (int i = 0; i < labels.length; i++) {
            list.add(new Variant(labels[i], "{" + key + ":" + i + "}"));
        }
        return list;
    }

    private static List<Variant> mapped(Function<String, String> nbt, String... values) {
        return Arrays.stream(values).map(v -> new Variant(capitalize(v), nbt.apply(v))).toList();
    }

    private static String capitalize(String value) {
        return Arrays.stream(value.split("_"))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
