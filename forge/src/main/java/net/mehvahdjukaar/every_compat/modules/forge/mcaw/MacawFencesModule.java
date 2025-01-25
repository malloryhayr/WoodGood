package net.mehvahdjukaar.every_compat.modules.forge.mcaw;

import com.mcwfences.kikoz.init.BlockInit;
import com.mcwfences.kikoz.objects.FenceHitbox;
import com.mcwfences.kikoz.objects.WiredFence;
import net.mehvahdjukaar.every_compat.api.RenderLayer;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesType;
import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesTypeRegistry;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;


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

        ResourceLocation tab = modRes("fenceitemgroup");

        picketFences = SimpleEntrySet.builder(WoodType.class, "picket_fence",
                        BlockInit.OAK_PICKET_FENCE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new FenceBlock(Utils.copyPropertySafe(w.planks))
                )
                //TEXTURES: logs, planks
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.BLOCK)
                .addTag(ItemTags.FENCES, Registries.ITEM)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                .build();
        this.addEntry(picketFences);

        stockadeFences = SimpleEntrySet.builder(WoodType.class, "stockade_fence",
                        BlockInit.OAK_STOCKADE_FENCE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new FenceBlock(Utils.copyPropertySafe(w.planks))
                )
                //TEXTURES: logs, planks
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.BLOCK)
                .addTag(ItemTags.FENCES, Registries.ITEM)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                .build();
        this.addEntry(stockadeFences);

        horseFences = SimpleEntrySet.builder(WoodType.class, "horse_fence",
                        BlockInit.OAK_HORSE_FENCE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new FenceBlock(Utils.copyPropertySafe(w.planks))
                )
                //TEXTURES: logs
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.BLOCK)
                .addTag(ItemTags.FENCES, Registries.ITEM)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                .build();
        this.addEntry(horseFences);

        wiredFences = SimpleEntrySet.builder(WoodType.class, "wired_fence",
                        BlockInit.OAK_WIRED_FENCE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new WiredFence(Utils.copyPropertySafe(w.planks))
                )
                //TEXTURES: logs
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.BLOCK)
                .addTag(ItemTags.FENCES, Registries.ITEM)
                .setRenderType(RenderLayer.CUTOUT)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                .build();
        this.addEntry(wiredFences);

        pyramidGates = SimpleEntrySet.builder(WoodType.class, "pyramid_gate",
                        BlockInit.OAK_PYRAMID_GATE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new FenceGateBlock(Utils.copyPropertySafe(w.planks), w.toVanillaOrOak())
                )
                //TEXTURES: logs, planks
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.UNSTABLE_BOTTOM_CENTER, Registries.BLOCK)
                .addTag(BlockTags.FENCE_GATES, Registries.BLOCK)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                .build();
        this.addEntry(pyramidGates);

        highleyGates = SimpleEntrySet.builder(WoodType.class, "highley_gate",
                        BlockInit.OAK_HIGHLEY_GATE, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new FenceGateBlock(Utils.copyPropertySafe(w.planks),w.toVanillaOrOak())
                )
                //TEXTURES: logs
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.UNSTABLE_BOTTOM_CENTER, Registries.BLOCK)
                .addTag(BlockTags.FENCE_GATES, Registries.BLOCK)
                .setTabKey(tab)
                .defaultRecipe()
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                .build();
        this.addEntry(highleyGates);

        hedges = SimpleEntrySet.builder(LeavesType.class, "hedge",
                        BlockInit.OAK_HEDGE, () -> LeavesTypeRegistry.OAK_TYPE,
                        l -> new FenceHitbox(Utils.copyPropertySafe(l.leaves).lightLevel((s) -> 0)
                                .mapColor(l.leaves.defaultMapColor()))
                )
                //TEXTURES: leaves
                .requiresChildren("leaves") // Reason: Recipes
//                .addCondition(l -> !l.getId().toString().equals("regions_unexplored:apple_oak")) // there should be a way to fix the color of the leave being grey
                .addTag(BlockTags.MINEABLE_WITH_HOE, Registries.BLOCK)
                .addTag(BlockTags.FENCES, Registries.BLOCK)
                .addTag(BlockTags.WALLS, Registries.BLOCK)
                .addTag(ItemTags.WALLS, Registries.ITEM)
                .setTabKey(tab)
                .copyParentTint()
                .defaultRecipe()
                .addModelTransform(m -> m.addModifier((s, id, l) -> {
                    /*
                    * EveryComp's code don't account for "mcwfences:block/oak_leaves" when the mod could have used
                    * "minecraft:block/oak_leaves" for texturing. Dev said using "minecraft:block/oak_leaves" lead
                    * to problems
                    */
                    String namespace = l.getNamespace();
                    String typeName = l.getTypeName();
                    switch (namespace) {
                        case "dreamy_cottage" -> {
                            if (typeName.equals("white_oak"))
                                return LeavesPath("whiteoakleaves", "", s, l);
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
                        case "autumnity" -> {
                            if (typeName.equals("red_maple")||typeName.equals("orange_maple")||typeName.equals("yellow_maple"))
                                return LeavesPath("maple_leaves", "", s, l);
                        }
                        case "chipped" -> {
                            return LeavesPath("","", s, l, "",  true);
                        }
                        case "blue_skies" -> {
                            if (typeName.equals("comet"))
                                return LeavesPath("comet_leaves_grown", "leaves", s, l);

                            return LeavesPath("", "leaves", s, l);
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
                                case "flowering" -> {
                                    return s.replace("\"mcwfences:block/oak_leaves\"",
                                            "\"regions_unexplored:item/flowering_leaves\"");
                                }
                                case "palm" -> {
                                    return LeavesPath("palm_leaves_side", "", s, l);
                                }
                            }
                        }
                        case "endlessbiomes" -> {
                            switch (typeName) {
                                case "glowing_penumbral" -> {
                                    return LeavesPath("penumbralleavesglowing", "", s, l);
                                }
                                case "penumbral" -> {
                                    return LeavesPath("penumbralleavesnewest", "", s, l);
                                }
                            }
                        }
                        case "aether" -> {
                            return LeavesPath("", "natural", s, l);
                        }
                        case "aether_redux" -> {
                            if (typeName.equals("azure_fieldsproot")) {
                                return LeavesPath("fieldsproot_leaves", "natural", s, l);
                            }
                            return LeavesPath("", "natural", s, l);
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
                        case "twilightforest" -> {
                            switch (typeName) {
                                case "beanstalk" -> {
                                    return LeavesPath("azalea_leaves", "", s, l, "minecraft");
                                }
                                case "canopy" -> {
                                    return LeavesPath("spruce_leaves", "", s, l, "minecraft");
                                }
                                case "dark" -> {
                                    return LeavesPath("darkwood_leaves", "", s, l);
                                }
                                case "mangrove" -> {
                                    return LeavesPath("birch_leaves", "", s, l, "minecraft");
                                }
                                case "rainbow_oak", "twilight_oak", "thorn" -> {
                                    return LeavesPath("oak_leaves", "", s, l, "minecraft");
                                }
                            }
                        }
                        case "integrateddynamics" -> {
                            return LeavesPath("menril_leaves_fancy", "", s, l);
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

    public String LeavesPath(String leavesName, String folderName, String s, LeavesType l, String namespace, boolean has_CHIPPED) {
        String path = (namespace.isBlank()) ? "\"" + l.getNamespace() : "\"" + namespace;
        path += ":block/";

        String LeavesTypeName = l.getTypeName();
        String folder;
        if (!leavesName.isEmpty()) { // unique name for leaves texture
            if (!folderName.isEmpty()) path += folderName + "/";

            return s.replace("\"mcwfences:block/oak_leaves\"",
                    path + leavesName + "\"");
        }
        else if (!folderName.isEmpty()) { // only for blue_skies
            folder = folderName + "/";
        }
        else if (has_CHIPPED) { // only for chipped
            folder = LeavesTypeName.replaceAll(
                    "cherry_|frosted_|dead_|golden_|apple_|magenta_|flower_|red_|white_|orange_",
                    "") + "_leaves/";
        }
        else folder = "";

        return s.replace("\"mcwfences:block/oak_leaves\"",
                path + folder + LeavesTypeName + "_leaves\"");
    }
}
