package net.mehvahdjukaar.every_compat.modules.fabric.wilder_wild;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.frozenblock.wilderwild.block.HollowedLogBlock;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.api.RenderLayer;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.every_compat.dynamicpack.ServerDynamicResourcesHandler;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static net.mehvahdjukaar.every_compat.common_classes.TagUtility.createAndAddCustomTags;

//SUPPORT: v2.4.6+
//NOTE: using methods to get ID as a string, this support 3.0.0+ without any issues
public class WilderWildModule extends SimpleModule {

    public final SimpleEntrySet<WoodType, HollowedLogBlock> hollow_log;
    public final SimpleEntrySet<WoodType, HollowedLogBlock> stripped_hollow_log;

    public WilderWildModule(String modId) {
        super(modId, "ww");
        ResourceKey<CreativeModeTab> tab = CreativeModeTabs.BUILDING_BLOCKS;

        hollow_log = SimpleEntrySet.builder(WoodType.class, "log", "hollowed",
                        getModBlock("hollowed_oak_log", HollowedLogBlock.class), () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new HollowedLogBlock(Utils.copyPropertySafe(getModBlock("hollowed_oak_log").get()))
                )
                .requiresChildren("stripped_log") // Textures
                .createPaletteFromChild("log")
                .addTexture(modRes("block/hollowed_oak_log"))
                //TEXTURES: stripped_hollowed_log, log_top
                .setRenderType(RenderLayer.CUTOUT)
                .setTabKey(tab)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE, Registries.BLOCK)
                .addTag(BlockTags.LOGS_THAT_BURN, Registries.BLOCK)
                .addTag(BlockTags.LOGS, Registries.BLOCK)
                .addTag(BlockTags.COMPLETES_FIND_TREE_TUTORIAL, Registries.BLOCK)
                .addTag(BlockTags.PARROTS_SPAWNABLE_ON, Registries.BLOCK)
                .addTag(modRes("hollowed_logs"), Registries.BLOCK)
                .addTag(modRes("splits_coconut"), Registries.BLOCK)
                //TAG: wilderwild:hollowed_<type>_logs
                .addTag(modRes("hollowed_logs"), Registries.ITEM)
                .addTag(modRes("hollowed_logs_that_burn"), Registries.ITEM)
                .addTag(ItemTags.LOGS_THAT_BURN, Registries.ITEM)
                .addTag(ItemTags.LOGS, Registries.ITEM)
                .addTag(ItemTags.COMPLETES_FIND_TREE_TUTORIAL, Registries.ITEM)
                .addRecipe(modRes("oak_wood_from_hollowed"))
                //REASON: The top texture is not a standard 16x16. Take a look, you'll see why
                .addCondition(w -> !w.getId().toString().matches("terrestria:(yucca_palm|sakura)"))
                .build();
        this.addEntry(hollow_log);

        stripped_hollow_log = SimpleEntrySet.builder(WoodType.class, "log", "stripped_hollowed",
                        getModBlock("stripped_hollowed_oak_log", HollowedLogBlock.class), () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new HollowedLogBlock(Utils.copyPropertySafe(getModBlock("stripped_hollowed_oak_log").get()))
                )
                .requiresChildren("stripped_log") // Textures
                .createPaletteFromChild("stripped_log")
                //TEXTURES: stripped_log, stripped_log_top
                .addTexture(modRes("block/stripped_hollowed_oak_log"))
                .addTexture(modRes("block/stripped_hollowed_oak_log"))
                .setRenderType(RenderLayer.CUTOUT)
                .setTabKey(tab)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE, Registries.BLOCK)
                .addTag(BlockTags.LOGS_THAT_BURN, Registries.BLOCK)
                .addTag(BlockTags.LOGS, Registries.BLOCK)
                .addTag(BlockTags.COMPLETES_FIND_TREE_TUTORIAL, Registries.BLOCK)
                .addTag(BlockTags.PARROTS_SPAWNABLE_ON, Registries.BLOCK)
                .addTag(modRes("hollowed_logs"), Registries.BLOCK)
                .addTag(modRes("stripped_hollowed_logs"), Registries.BLOCK)
                .addTag(modRes("splits_coconut"), Registries.BLOCK)
                //TAG: wilderwild:hollowed_<type>_logs
                .addTag(modRes("hollowed_logs"), Registries.ITEM)
                .addTag(modRes("hollowed_logs_that_burn"), Registries.ITEM)
                .addTag(ItemTags.LOGS_THAT_BURN, Registries.ITEM)
                .addTag(ItemTags.LOGS, Registries.ITEM)
                .addTag(ItemTags.COMPLETES_FIND_TREE_TUTORIAL, Registries.ITEM)
                .addRecipe(modRes("stripped_oak_wood_from_hollowed"))
                //REASON: The top texture is not a standard 16x16. Take a look, you'll see why
                .addCondition(w -> !w.getId().toString().matches("terrestria:(yucca_palm|sakura)"))
                .build();
        this.addEntry(stripped_hollow_log);

    }

    @Override
    public void onModSetup() {
        super.onModSetup();

        hollow_log.blocks.forEach((wood, block) -> {
            StrippableBlockRegistry.register(block, stripped_hollow_log.blocks.get(wood));

            HollowedLogBlock.registerAxeHollowBehavior(wood.log, hollow_log.blocks.get(wood));
            HollowedLogBlock.registerAxeHollowBehavior(wood.getBlockOfThis("stripped_log"), stripped_hollow_log.blocks.get(wood));
        });
    }

    @Override
    // Recipes & Tags
    public void addDynamicServerResources(ServerDynamicResourcesHandler handler, ResourceManager manager) {
        super.addDynamicServerResources(handler, manager);

        hollow_log.blocks.forEach((wood, block) -> {
            // Variables
            ResourceLocation recipeLoc = ResType.RECIPES.getPath("wilderwild:oak_planks_from_hollowed");
            ResourceLocation newRecipeLoc = EveryCompat.res(wood.getTypeName() + "_planks_from_hollowed");
            ResourceLocation tagRLoc = EveryCompat.res(shortenedId() + "/" + wood.getNamespace() + "/" + "hollowed_" +
                    wood.getTypeName() + "_logs");

// TAGS ================================================================================================
            boolean hasAddedNewTag = createAndAddCustomTags(tagRLoc, handler, block, stripped_hollow_log.blocks.get(wood));

// RECIPE ==============================================================================================
            try (InputStream recipeStream = manager.getResource(recipeLoc)
                    .orElseThrow(() -> new FileNotFoundException("ResourceLocation: " + recipeLoc)).open()) {
                JsonObject recipe = RPUtils.deserializeJson(recipeStream);

                // Editing the recipe
                recipe.getAsJsonArray("ingredients").get(0).getAsJsonObject()
                        .addProperty("tag", tagRLoc.toString());

                recipe.getAsJsonObject("result")
                        .addProperty("item", Utils.getID(wood.planks).toString());

                // Adding to the resources
                if (hasAddedNewTag) handler.dynamicPack.addJson(newRecipeLoc, recipe, ResType.RECIPES);


            } catch (IOException e) {
                handler.getLogger().error("Failed to open the recipe file: ", e);
            }
        });

    }
}