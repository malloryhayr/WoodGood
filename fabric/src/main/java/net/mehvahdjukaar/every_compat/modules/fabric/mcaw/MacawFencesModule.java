package net.mehvahdjukaar.every_compat.modules.fabric.mcaw;

import net.kikoz.mcwfences.MacawsFences;
import net.kikoz.mcwfences.init.BlockInit;
import net.kikoz.mcwfences.objects.FenceHitbox;
import net.mehvahdjukaar.every_compat.api.RenderLayer;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesType;
import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesTypeRegistry;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

//SUPPORT v1.1.1+
public class MacawFencesModule extends SimpleModule {

    public final SimpleEntrySet<LeavesType, Block> hedges;
    public final SimpleEntrySet<WoodType, Block> highleyGates;
    public final SimpleEntrySet<WoodType, Block> horseFences;
    public final SimpleEntrySet<WoodType, Block> picketFences;
    public final SimpleEntrySet<WoodType, Block> pyramidGates;
    public final SimpleEntrySet<WoodType, Block> stockadeFences;
    public final SimpleEntrySet<WoodType, Block> wiredFences;

    public MacawFencesModule(String modId) {
        super(modId, "mcf");
        ResourceKey<CreativeModeTab> tab = MacawsFences.FENCESGROUP;

        picketFences = SimpleEntrySet.builder(WoodType.class, "picket_fence",
                        () -> BlockInit.OAK_PICKET_FENCE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new FenceBlock(Utils.copyPropertySafe(w.planks)
                                .strength(1.4F, 2.0F).noOcclusion())
                )
                //TEXTURES: logs, planks
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.ITEM)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                //REASON: take a look at their //TEXTURES, you'll see why. Excluded!
                .excludeBlockTypes("terrestria", "sakura", "yucca_palm")
                .excludeBlockTypes("betternether", "nether_mushroom", "nether_reed")
                .build();
        this.addEntry(picketFences);

        stockadeFences = SimpleEntrySet.builder(WoodType.class, "stockade_fence",
                        () -> BlockInit.OAK_STOCKADE_FENCE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new FenceBlock(Utils.copyPropertySafe(w.planks)
                                .strength(1.4F, 2.0F).noOcclusion())
                )
                //TEXTURES: logs, planks
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.ITEM)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                //REASON: take a look at their //TEXTURES, you'll see why. Excluded!
                .excludeBlockTypes("terrestria", "sakura", "yucca_palm")
                .excludeBlockTypes("betternether", "nether_mushroom", "nether_reed")
                .build();
        this.addEntry(stockadeFences);

        horseFences = SimpleEntrySet.builder(WoodType.class, "horse_fence",
                        () -> BlockInit.OAK_HORSE_FENCE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new FenceBlock(Utils.copyPropertySafe(w.planks)
                                .strength(1.4F, 2.0F).noOcclusion())
                )
                //TEXTURES: logs
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.ITEM)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                //REASON: take a look at their //TEXTURES, you'll see why. Excluded!
                .excludeBlockTypes("terrestria", "sakura", "yucca_palm")
                .excludeBlockTypes("betternether", "nether_mushroom", "nether_reed")
                .build();
        this.addEntry(horseFences);

        wiredFences = SimpleEntrySet.builder(WoodType.class, "wired_fence",
                        () -> BlockInit.OAK_WIRED_FENCE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new WiredFence(Utils.copyPropertySafe(w.planks)
                                .strength(1.5F, 2.5F).noOcclusion())
                )
                //TEXTURES: logs
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.ITEM)
                .setTabKey(tab)
                .defaultRecipe()
                .setRenderType(RenderLayer.CUTOUT)
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                //REASON: take a look at their //TEXTURES, you'll see why. Excluded!
                .excludeBlockTypes("terrestria", "sakura", "yucca_palm")
                .excludeBlockTypes("betternether", "nether_mushroom", "nether_reed")
                .build();
        this.addEntry(wiredFences);

        pyramidGates = SimpleEntrySet.builder(WoodType.class, "pyramid_gate",
                        () -> BlockInit.OAK_PYRAMID_GATE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new FenceGateBlock(Utils.copyPropertySafe(w.planks)
                                .strength(1.4F, 2.0F).noOcclusion(), w.toVanillaOrOak())
                )
                //TEXTURES: logs, planks
                .addTag(BlockTags.FENCE_GATES, Registries.BLOCK)
                .addTag(BlockTags.UNSTABLE_BOTTOM_CENTER, Registries.BLOCK)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                //REASON: take a look at their //TEXTURES, you'll see why. Excluded!
                .excludeBlockTypes("terrestria", "sakura", "yucca_palm")
                .excludeBlockTypes("betternether", "nether_mushroom", "nether_reed")
                .build();
        this.addEntry(pyramidGates);

        highleyGates = SimpleEntrySet.builder(WoodType.class, "highley_gate",
                        () -> BlockInit.OAK_HIGHLEY_GATE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new FenceGateBlock(Utils.copyPropertySafe(w.planks)
                                .strength(1.4F, 2.0F).noOcclusion(), w.toVanillaOrOak())
                )
                //TEXTURES: logs
                .addTag(BlockTags.FENCE_GATES, Registries.BLOCK)
                .addTag(BlockTags.UNSTABLE_BOTTOM_CENTER, Registries.BLOCK)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                //REASON: take a look at their //TEXTURES, you'll see why. Excluded!
                .excludeBlockTypes("terrestria", "sakura", "yucca_palm")
                .excludeBlockTypes("betternether", "nether_mushroom", "nether_reed")
                .build();
        this.addEntry(highleyGates);

        hedges = SimpleEntrySet.builder(LeavesType.class, "hedge",
                        () -> BlockInit.OAK_HEDGE, () -> LeavesTypeRegistry.OAK_TYPE,
                        l -> {
                            // Reason: Below have no leave texture
                            if (l.getId().toString().equals("regions_unexplored:flowering")) return null;
                            return new FenceHitbox(Utils.copyPropertySafe(l.leaves).lightLevel((s) -> 0)
                                    .strength(0.2F, 0.3F).noOcclusion()
                                    .mapColor(l.leaves.defaultMapColor()));
                        })
                //TEXTURES: leaves
                .requiresChildren("leaves") //REASON: recipes
                .addTag(BlockTags.MINEABLE_WITH_HOE, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.BLOCK)
                .addTag(BlockTags.WALLS, Registries.BLOCK)
                .addTag(ItemTags.WALLS, Registries.ITEM)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentTint()
                .addModelTransform(m -> m.addModifier((s, id, l) -> {
                    /*
                     * EveryComp's code don't account for "mcwfences:block/oak_leaves" when the mod could have used
                     * "minecraft:block/oak_leaves" for texturing. Dev said using "minecraft:block/oak_leaves" lead
                     * to problems
                     */
                    String namespace = l.getNamespace();
                    String typeName = l.getTypeName();
                    switch (namespace) {
                        case "betterend" -> {
                            if (typeName.equals("lucernia"))
                                return LeavesPath("lucernia_leaves_1", "", s, l);
                        }
                        case "culturaldelights" -> {
                            if (typeName.equals("fruiting_avocado"))
                                return LeavesPath("fruiting_avocado_leaves_0", "", s, l);
                        }
                        case "fruitfulfun" -> {
                            if (typeName.equals("apple"))
                                return LeavesPath("oak_leaves", "", s, l, "minecraft");
                        }
                        case "vinery" -> {
                            if (typeName.equals("apple")) {
                                return LeavesPath("apple_leaves_0", "", s, l);
                            }
                        }
                        case "meadow" -> {
                            if (typeName.equals("pine"))
                                return LeavesPath("pine_leaves_1", "", s, l);
                        }
                        case "chipped" -> {
                            return LeavesPath("","", s, l, "", true);
                        }
                        case "regions_unexplored" -> {
                            switch (typeName) {
                                case "alpha" -> {
                                    return LeavesPath("alpha_oak_leaves", "", s, l);
                                }
                                case "apple_oak" -> {
                                    return LeavesPath("apple_oak_leaves_stage_0", "", s, l);
                                }
                                case "small_oak" -> {
                                    return s.replace("\"mcwfences:block/oak_leaves\"",
                                            "\"minecraft:block/oak_leaves\"");
                                }
                                case "palm" -> {
                                    return LeavesPath("palm_leaves_side", "", s, l);
                                }
                            }
                        }
                        case "malum" -> {
                            switch (typeName) {
                                case "budding_soulwood" -> {
                                    return LeavesPath("","soulwood", s, l);
                                }
                                case "azure_runewood" -> {
                                    return LeavesPath("", "runewood", s, l);
                                }
                            }
                            return LeavesPath("", l.getTypeName(), s, l);
                        }
                    }
                    return LeavesPath("", "", s, l);
                }))
                .setRenderType(RenderLayer.CUTOUT_MIPPED)
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                .build();
        this.addEntry(hedges);
    }

    public String LeavesPath(String leavesName, String folderName, String s, LeavesType l) {
        return LeavesPath(leavesName, folderName, s, l,"", false);
    }

    public String LeavesPath(String leavesName, String folderName, String s, LeavesType l, String namespace) {
        return LeavesPath(leavesName, folderName, s, l, namespace,false);
    }

    public String LeavesPath(String leavesName, String folderName, String s, LeavesType l, String namespace,  boolean has_CHIPPED) {
        String path = (namespace.isBlank()) ? "\"" + l.getNamespace() : "\"" + namespace;
        path += ":block/";

        String LeavesTypeName = l.getTypeName();
        String folder;
        if (!leavesName.isEmpty()) {
            if (!folderName.isEmpty()) path += folderName + "/";

            return s.replace("\"mcwfences:block/oak_leaves\"",
                    path + leavesName + "\"");
        }
        else if (!folderName.isEmpty()) { // only for blue_skies
            folder = folderName + "/";
        }
        else if (has_CHIPPED) { // only for chipped
            folder = LeavesTypeName.replaceAll("cherry_|frosted_|dead_|golden_|apple_|magenta_|flower_|red_|white_|orange_",
                    "") + "_leaves/";
        }
        else folder = "";

        return s.replace("\"mcwfences:block/oak_leaves\"",
                path + folder + LeavesTypeName + "_leaves"+ "\"");
    }

    public static class WiredFence extends FenceBlock {

        public WiredFence(Properties properties) {
            super(properties);
        }

        //changed. I couldn't help myself
        @Override
        public void entityInside(BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Entity entityIn) {
            if (state.getValue(NORTH) || state.getValue(SOUTH) || state.getValue(EAST) || state.getValue(WEST))
                entityIn.hurt(level.damageSources().generic(), 2.0F);
        }
    }

}
