package net.mehvahdjukaar.every_compat.modules.forge.kubejs;

import net.mehvahdjukaar.every_compat.api.ItemOnlyEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.moonlight.api.resources.textures.SpriteUtils;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class KubeJSModule extends SimpleModule {
    public final HackyItemEntrySet<WoodType, Item> planks;
    public final HackyItemEntrySet<WoodType, Item> boards;
    public final HackyItemEntrySet<WoodType, Item> wedges;
    public final HackyItemEntrySet<WoodType, Item> rods;

    public KubeJSModule(String modId) {
        super(modId, "kubejs");

        planks = HackyItemEntrySet.builder(WoodType.class, "plank",
                    getModItem("oak_plank"), () -> WoodTypeRegistry.OAK_TYPE,
                    w -> new Item(new Item.Properties())
                ).addTexture(modRes("item/oak_plank"))
                .createPaletteFromOak(SpriteUtils::extrapolateWoodItemPalette)
                .addRecipe(new ResourceLocation("kubejs", "oak_plank"))
                .addRecipe(new ResourceLocation("kubejs", "oak_plank_from_wood"))
                .build();
        this.addEntry(planks);

        boards = HackyItemEntrySet.builder(WoodType.class, "board",
                        getModItem("oak_board"), () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new Item(new Item.Properties())
                ).addTexture(modRes("item/oak_board"))
                .createPaletteFromOak(SpriteUtils::extrapolateWoodItemPalette)
                .addRecipe(new ResourceLocation("kubejs", "oak_board"))
                .addRecipe(new ResourceLocation("kubejs", "oak_board_from_wood"))
                .build();
        this.addEntry(boards);

        wedges = HackyItemEntrySet.builder(WoodType.class, "wedge",
                        getModItem("oak_wedge"), () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new Item(new Item.Properties())
                ).addTexture(modRes("item/oak_wedge"))
                .createPaletteFromOak(SpriteUtils::extrapolateWoodItemPalette)
                .addRecipe(new ResourceLocation("kubejs", "oak_wedge"))
                .build();
        this.addEntry(wedges);

        rods = HackyItemEntrySet.builder(WoodType.class, "rod",
                        getModItem("oak_rod"), () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new Item(new Item.Properties())
                ).addTexture(modRes("item/oak_rod"))
                .createPaletteFromOak(SpriteUtils::extrapolateWoodItemPalette)
                .addRecipe(new ResourceLocation("kubejs", "oak_rod"))
                .build();
        this.addEntry(rods);

    }
}
