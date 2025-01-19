package net.mehvahdjukaar.every_compat.modules.create;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.decoration.palettes.ConnectedGlassPaneBlock;
import com.simibubi.create.content.decoration.palettes.WindowBlock;
import com.simibubi.create.foundation.block.connected.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.api.RenderLayer;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.every_compat.dynamicpack.ServerDynamicResourcesHandler;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Objects;

// SUPPORT: v0.5.1+
public class CreateModule extends SimpleModule {

    public final SimpleEntrySet<WoodType, Block> windows;
    public final SimpleEntrySet<WoodType, Block> windowPanes;

    public CreateModule(String modId) {
        super(modId, "c");
        var tab = modRes("palettes");

        windows = SimpleEntrySet.builder(WoodType.class, "window",
                        getModBlock("oak_window"), () -> WoodTypeRegistry.OAK_TYPE, //AllPaletteBlocks.OAK_WINDOW
                        this::makeWindow)
                .addTag(BlockTags.IMPERMEABLE, Registries.BLOCK)
                .setTabKey(tab)
                .defaultRecipe()
                .setRenderType(RenderLayer.CUTOUT_MIPPED)
                .createPaletteFromPlanks(p -> p.remove(p.getDarkest()))
                .addTextureM(modRes("block/palettes/oak_window"), EveryCompat.res("block/palettes/oak_window_m"))
                .addTextureM(modRes("block/palettes/oak_window_connected"), EveryCompat.res("block/palettes/oak_window_connected_m"))
                .build();
        this.addEntry(windows);

        windowPanes = SimpleEntrySet.builder(WoodType.class, "window_pane",
                        getModBlock("oak_window_pane"), () -> WoodTypeRegistry.OAK_TYPE, //AllPaletteBlocks.OAK_WINDOW_PANE
                        s -> new ConnectedGlassPaneBlock(Utils.copyPropertySafe(Blocks.GLASS_PANE)))
                .addTag(new ResourceLocation("c:glass_panes"), Registries.BLOCK)
                .addTag(new ResourceLocation("c:glass_panes"), Registries.ITEM)
                .setTabKey(tab)
                .defaultRecipe()
                .setRenderType(RenderLayer.CUTOUT_MIPPED)
                .copyParentDrop() //REASON: ensure blocks's dropping when Diagonal Fences is installed
                .build();
        this.addEntry(windowPanes);

    }

    private WindowBlock makeWindow(WoodType w) {
        return new WindowBlock(Utils.copyPropertySafe(Blocks.GLASS)
                .isValidSpawn((s, l, ps, t) -> false).isRedstoneConductor((s, l, ps) -> false)
                .isSuffocating((s, l, ps) -> false).isViewBlocking((s, l, ps) -> false), false);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onClientSetup() {
        super.onClientSetup();
        CreateClientModule.clientStuff(this);
    }

    @Environment(EnvType.CLIENT)
    private static class CreateClientModule {
        private static void clientStuff(CreateModule module) {
            module.windows.blocks.forEach((w, b) -> {
                String path = "block/" + module.shortenedId() + "/" + w.getNamespace() + "/palettes/" + w.getTypeName() + "_window";

                CTSpriteShiftEntry spriteShift = CTSpriteShifter.getCT(AllCTTypes.VERTICAL,
                        EveryCompat.res(path), EveryCompat.res(path + "_connected"));

                CreateClient.MODEL_SWAPPER.getCustomBlockModels().register(Utils.getID(b),
                        (model) -> new CTModel(model, new HorizontalCTBehaviour(spriteShift)));
                CreateClient.MODEL_SWAPPER.getCustomBlockModels().register(Utils.getID(module.windowPanes.blocks.get(w)),
                        (model) -> new CTModel(model, new GlassPaneCTBehaviour(spriteShift)));
            });
        }
    }

    @Override
    // Recipes
    public void addDynamicServerResources(ServerDynamicResourcesHandler handler, ResourceManager manager) {
        super.addDynamicServerResources(handler, manager);
        if (!PlatHelper.isModLoaded("sawmill")) {
            for (WoodType w : WoodTypeRegistry.getTypes()) {
                if (w.isVanilla()) continue;
                if (w.getBlockOfThis("slab") != null)
                    sawRecipe(2, w.planks.asItem(), Objects.requireNonNull(w.getBlockOfThis("slab")).asItem(), null, w, handler);

                if (w.getBlockOfThis("stairs") != null)
                    sawRecipe(1, w.planks.asItem(), Objects.requireNonNull(w.getBlockOfThis("stairs")).asItem(), null, w, handler);

                sawRecipe(6, w.planks.asItem(), null, new ResourceLocation("minecraft", "stick"), w, handler);
            }
        }

    }

    public void sawRecipe(int amount, Item input, Item output, ResourceLocation item, WoodType woodType, ServerDynamicResourcesHandler handler) {
        String blank = """
                {
                    "type": "minecraft:stonecutting",
                    "count": [amount],
                    "ingredient": {
                        "item": "[input]"
                    },
                    "result": "[output]"
                }
                """;

        ResourceLocation resloc;
        String recipe;
        if (output != null) {
            recipe = blank.replace("[amount]", String.valueOf(amount))
                    .replace("[input]", Utils.getID(input).toString())
                    .replace("[output]", Utils.getID(output).toString());
            resloc = EveryCompat.res(
                    shortenedId() + "/" + woodType.getNamespace() + "/" + output + "_from_" + input + "_stonecutting");
        } else { // item != null
            recipe = blank.replace("[amount]", String.valueOf(amount))
                    .replace("[input]", Utils.getID(input).toString())
                    .replace("[output]", item.toString());
            resloc = EveryCompat.res(
                    shortenedId() + "/" + woodType.getNamespace() + "/" + item.getPath() + "_from_" + input + "_stonecutting");
        }

        handler.dynamicPack.addBytes(resloc, recipe.getBytes(), ResType.RECIPES);

    }
}