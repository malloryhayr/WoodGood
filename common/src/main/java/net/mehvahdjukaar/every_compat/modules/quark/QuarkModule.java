package net.mehvahdjukaar.every_compat.modules.quark;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mehvahdjukaar.every_compat.ECPlatformStuff;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.every_compat.api.TabAddMode;
import net.mehvahdjukaar.every_compat.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.every_compat.dynamicpack.ServerDynamicResourcesHandler;
import net.mehvahdjukaar.every_compat.misc.SpriteHelper;
import net.mehvahdjukaar.every_compat.modules.botanypots.BotanyPotsHelper;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.textures.Palette;
import net.mehvahdjukaar.moonlight.api.resources.textures.Respriter;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesType;
import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesTypeRegistry;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.api.util.math.colors.HCLColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.violetmoon.quark.base.QuarkClient;
import org.violetmoon.quark.content.building.block.*;
import org.violetmoon.quark.content.building.client.render.be.VariantChestRenderer;
import org.violetmoon.quark.content.building.module.*;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.client.SimpleWithoutLevelRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//SUPPORT: v4.0-435+
public class QuarkModule extends SimpleModule {

    public final SimpleEntrySet<WoodType, Block> bookshelves;
    public final SimpleEntrySet<WoodType, Block> posts;
    public final SimpleEntrySet<WoodType, Block> strippedPosts;
    public final SimpleEntrySet<WoodType, Block> verticalPlanks;
    public final SimpleEntrySet<WoodType, Block> ladders;
    public final SimpleEntrySet<WoodType, Block> hollowLogs;
    public final SimpleEntrySet<WoodType, ? extends VariantChestBlock> chests;
    public final SimpleEntrySet<WoodType, ? extends VariantTrappedChestBlock> trappedChests;
    public final SimpleEntrySet<LeavesType, Block> hedges;
    public final SimpleEntrySet<LeavesType, Block> leafCarpets;

    public static BlockEntityType<ChestBlockEntity> CHEST_TILE;
    public static BlockEntityType<ChestBlockEntity> TRAPPED_CHEST_TILE;

    public QuarkModule(String modId) {
        super(modId, "q");

        bookshelves = QuarkSimpleEntrySet.builder(WoodType.class, "bookshelf",
                        VariantBookshelvesModule.class,
                        getModBlock("acacia_bookshelf"),
                        () -> WoodTypeRegistry.getValue(new ResourceLocation("acacia")),
                        (w) -> new VariantBookshelfBlock(shortenedId() + "/" + w.getAppendableId(),
                                null, w.canBurn(), w.getSound()))
                .setTabKey(CreativeModeTabs.BUILDING_BLOCKS)
                .setTabMode(TabAddMode.AFTER_SAME_WOOD)
                .copyParentDrop()
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(new ResourceLocation("forge:bookshelves"), Registries.BLOCK)
                .addTag(new ResourceLocation("forge:bookshelves"), Registries.ITEM)
                .addRecipe(modRes("building/crafting/acacia_bookshelf"))
                .addTextureM(EveryCompat.res("block/acacia_bookshelf"), EveryCompat.res("block/acacia_bookshelf_m"))
                .setPalette(this::bookshelfPalette)
                .build();

        this.addEntry(bookshelves);

        posts = QuarkSimpleEntrySet.builder(WoodType.class, "post",
                        WoodenPostsModule.class,
                        getModBlock("oak_post"),
                        () -> WoodTypeRegistry.OAK_TYPE,
                        (w) -> {
                            Block fence = w.getBlockOfThis("fence");
                            return new WoodPostBlock(null, fence, shortenedId() + "/" + w.getNamespace() + "/",
                                    Objects.requireNonNull(fence).getSoundType(fence.defaultBlockState()));
                        })
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(modRes("posts"), Registries.BLOCK)
                .addTag(modRes("posts"), Registries.ITEM)
                .requiresChildren("fence")
                .setTabKey(CreativeModeTabs.BUILDING_BLOCKS)
                .setTabMode(TabAddMode.AFTER_SAME_WOOD)
                .addRecipe(modRes("building/crafting/oak_post"))
                .setRenderType(() -> RenderType::cutout)
                .build();

        this.addEntry(posts);

        strippedPosts = QuarkSimpleEntrySet.builder(WoodType.class, "post", "stripped",
                        WoodenPostsModule.class,
                        getModBlock("stripped_oak_post"),
                        () -> WoodTypeRegistry.OAK_TYPE,
                        (w) -> {
                            if (w.getNamespace().equals("malum") || w.getNamespace().equals("twigs")) return null;
                            Block fence = w.getBlockOfThis("fence");
                            // required stripped_log texture & fence as an ingredients
                            return new WoodPostBlock(null, fence, shortenedId() + "/" + w.getNamespace() + "/stripped_",
                                    Objects.requireNonNull(fence).getSoundType(fence.defaultBlockState()));
                        })
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(modRes("posts"), Registries.BLOCK)
                .addTag(modRes("posts"), Registries.ITEM)
                .requiresChildren("fence", "stripped_log")
                .setTabKey(CreativeModeTabs.BUILDING_BLOCKS)
                .setTabMode(TabAddMode.AFTER_SAME_WOOD)
                .addRecipe(modRes("building/crafting/stripped_oak_post"))
                .setRenderType(() -> RenderType::cutout)
                .build();

        this.addEntry(strippedPosts);

        verticalPlanks = QuarkSimpleEntrySet.builder(WoodType.class, "planks", "vertical",
                        VerticalPlanksModule.class,
                        getModBlock("vertical_oak_planks"),
                        () -> WoodTypeRegistry.OAK_TYPE,
                        (w) -> {
                            String name = shortenedId() + "/" + w.getVariantId("planks", "vertical");
                            return new ZetaBlock(name, null, Utils.copyPropertySafe(w.planks));
                        })
                .setTabKey(CreativeModeTabs.BUILDING_BLOCKS)
                .setTabMode(TabAddMode.AFTER_SAME_WOOD)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(BlockTags.PLANKS, Registries.BLOCK)
                .addTag(BlockTags.PLANKS, Registries.ITEM)
                .addRecipe(modRes("building/crafting/vertplanks/vertical_oak_planks"))
                .build();

        this.addEntry(verticalPlanks);

        ladders = QuarkSimpleEntrySet.builder(WoodType.class, "ladder",
                        VariantLaddersModule.class,
                        getModBlock("spruce_ladder"),
                        () -> WoodTypeRegistry.getValue(new ResourceLocation("spruce")),
                        (w) -> new VariantLadderBlock(shortenedId() + "/" + w.getAppendableId(),
                                null, BlockBehaviour.Properties.copy(Blocks.LADDER).sound(w.getSound()), w.canBurn()))
                .setTabKey(CreativeModeTabs.BUILDING_BLOCKS)
                .setTabMode(TabAddMode.AFTER_SAME_WOOD)
                .addTag(BlockTags.CLIMBABLE, Registries.BLOCK)
                .addTag(modRes("ladders"), Registries.BLOCK)
                .addTag(modRes("ladders"), Registries.ITEM)
                .addTexture(EveryCompat.res("block/spruce_ladder"))
                .addRecipe(modRes("building/crafting/spruce_ladder"))
                .setRenderType(() -> RenderType::translucent)
                .build();

        this.addEntry(ladders);

        hollowLogs = QuarkSimpleEntrySet.builder(WoodType.class, "log", "hollow",
                        HollowLogsModule.class,
                        getModBlock("hollow_oak_log"),
                        () -> WoodTypeRegistry.OAK_TYPE,
                        (w) -> new HollowLogBlock(shortenedId() + "/" + w.getAppendableId(),
                                w.log, null, w.canBurn()))
                .requiresChildren("stripped_log") // Texture
                .setTabKey(CreativeModeTabs.BUILDING_BLOCKS)
                .setTabMode(TabAddMode.AFTER_SAME_WOOD)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(modRes("hollow_logs"), Registries.BLOCK)
                .addTag(modRes("hollow_logs"), Registries.ITEM)
                .addRecipe(modRes("building/crafting/hollowlogs/hollow_oak_log"))
                .build();

        this.addEntry(hollowLogs);

        chests = QuarkSimpleEntrySet.builder(WoodType.class, "chest",
                        VariantChestsModule.class,
                        getModBlock("oak_chest", VariantChestBlock.class),
                        () -> WoodTypeRegistry.OAK_TYPE,
                        (w) -> new CompatChestBlock(w,
                                shortenedId() + "/" + w.getAppendableId(),
                                Utils.copyPropertySafe(w.planks)))
                .setTabKey(CreativeModeTabs.BUILDING_BLOCKS)
                .setTabMode(TabAddMode.AFTER_SAME_WOOD)
                .addTag(new ResourceLocation("forge:chests/wooden"), Registries.BLOCK)
                .addTag(new ResourceLocation("forge:chests/wooden"), Registries.ITEM)
                .addTag(modRes("revertable_chests"), Registries.ITEM)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(new ResourceLocation("quark:revertable_chests"), Registries.ITEM)
                .addTile(CompatChestBlockTile::new)
                .addCondition(w -> !w.getId().toString().equals("twilightforest:dark"))
                .addRecipe(modRes("building/crafting/chests/oak_chest"))
                .build();

        this.addEntry(chests);

        trappedChests = QuarkSimpleEntrySet.builder(WoodType.class, "trapped_chest",
                        VariantChestsModule.class,
                        getModBlock("oak_trapped_chest", VariantTrappedChestBlock.class),
                        () -> WoodTypeRegistry.OAK_TYPE,
                        (w) -> {
                            boolean isNamespaceLoaded = w.getNamespace().equals("twilightforest")
                                    || w.getNamespace().equals("blue_skies");
                            if (!chests.blocks.containsKey(w) && !isNamespaceLoaded) return null;
                            String name = shortenedId() + "/" + w.getAppendableId();
                            return new CompatTrappedChestBlock(w, name, Utils.copyPropertySafe(w.planks));
                        })
                .setTabKey(CreativeModeTabs.BUILDING_BLOCKS)
                .setTabMode(TabAddMode.AFTER_SAME_WOOD)
                .addTag(new ResourceLocation("forge:chests/trapped"), Registries.BLOCK)
                .addTag(new ResourceLocation("forge:chests/trapped"), Registries.ITEM)
                .addTag(new ResourceLocation("forge:chests/wooden"), Registries.ITEM)
                .addTag(new ResourceLocation("forge:chests/wooden"), Registries.BLOCK)
                .addTag(modRes("revertable_trapped_chests"), Registries.ITEM)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTile(CompatTrappedChestBlockTile::new)
                .addRecipe(modRes("building/crafting/chests/oak_trapped_chest"))
                .build();

        this.addEntry(trappedChests);

        //doing it this way because for some reason its nuking whatever block item I throw in here
        hedges = QuarkSimpleEntrySet.builder(LeavesType.class, "hedge",
                        HedgesModule.class,
                        getModBlock("oak_hedge"),
                        () -> LeavesTypeRegistry.OAK_TYPE,
                        (w) -> new HedgeBlock("", null, Blocks.OAK_FENCE, w.leaves))
                .addModelTransform(m -> m.replaceWithTextureFromChild("minecraft:block/oak_leaves",
                        "leaves", SpriteHelper.LOOKS_LIKE_LEAF_TEXTURE))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(modRes("hedges"), Registries.BLOCK)
                .addTag(modRes("hedges"), Registries.ITEM)
                .setTabKey(CreativeModeTabs.BUILDING_BLOCKS)
                .setTabMode(TabAddMode.AFTER_SAME_WOOD)
                .copyParentTint()
                .addCondition(l -> l.getWoodType() != null)
//              Recipe being created below blc the recipe has a tag as an ingredient
                .setRenderType(() -> RenderType::cutout)
                .build();
        this.addEntry(hedges);


        //doing it this way because for some reason its nuking whatever block item I throw in here
        leafCarpets = QuarkSimpleEntrySet.builder(LeavesType.class, "leaf_carpet",
                        LeafCarpetModule.class,
                        getModBlock("oak_leaf_carpet"),
                        () -> LeavesTypeRegistry.OAK_TYPE,
                        (w) -> {
                            String name = shortenedId() + "/" + w.getVariantId("%s_leaf_carpet");
                            return new LeafCarpetBlock(name, w.leaves, null);
                        })
                .addModelTransform(m -> m.replaceWithTextureFromChild("minecraft:block/oak_leaves",
                        "leaves", s -> !s.contains("/snow") && !s.contains("_snow")))
                .addTag(modRes("leaf_carpets"), Registries.BLOCK)
                .addTag(modRes("leaf_carpets"), Registries.ITEM)
                .setTabKey(CreativeModeTabs.BUILDING_BLOCKS)
                .setTabMode(TabAddMode.AFTER_SAME_WOOD)
                .addRecipe(modRes("building/crafting/oak_leaf_carpet"))
                .setRenderType(() -> RenderType::cutout)
                .copyParentTint()
                .build();
        this.addEntry(leafCarpets);

    }

    @Override
    public void onModSetup() {
        posts.blocks.forEach((w, post) -> {
            Block stripped = strippedPosts.blocks.get(w);
            if (stripped != null) ECPlatformStuff.registerStripping(post, stripped);
        });
        leafCarpets.blocks.forEach((w, leaf) -> ComposterBlock.COMPOSTABLES.put(leaf, 0.2F));
    }

    @Override
    public void registerTiles(Registrator<BlockEntityType<?>> registry) {
        super.registerTiles(registry);
        CHEST_TILE = chests.getTile(ChestBlockEntity.class);
        TRAPPED_CHEST_TILE = trappedChests.getTile(ChestBlockEntity.class);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onClientSetup() {
        super.onClientSetup();

        for (var b : chests.blocks.values())
            QuarkClient.ZETA_CLIENT.setBlockEntityWithoutLevelRenderer(b.asItem(), new SimpleWithoutLevelRenderer(CHEST_TILE, b.defaultBlockState()));
        for (var b : trappedChests.blocks.values())
            QuarkClient.ZETA_CLIENT.setBlockEntityWithoutLevelRenderer(b.asItem(), new SimpleWithoutLevelRenderer(TRAPPED_CHEST_TILE, b.defaultBlockState()));

    }

    @Environment(EnvType.CLIENT)
    @Override
    public void registerBlockEntityRenderers(ClientHelper.BlockEntityRendererEvent event) {
        super.registerBlockEntityRenderers(event);
        event.register(CHEST_TILE, context -> new VariantChestRenderer(context, false));
        event.register(TRAPPED_CHEST_TILE, context -> new VariantChestRenderer(context, true));
    }

    private Pair<List<Palette>, AnimationMetadataSection> bookshelfPalette(BlockType w, ResourceManager m) {
        try (TextureImage plankTexture = TextureImage.open(m,
                RPUtils.findFirstBlockTextureLocation(m, ((WoodType) w).planks))) {

            List<Palette> targetPalette = Palette.fromAnimatedImage(plankTexture);
            targetPalette.forEach(p -> {
                var l0 = p.getDarkest();
                p.increaseDown();
                p.increaseDown();
                p.increaseDown();
                p.increaseDown();
                p.remove(l0);
            });
            return Pair.of(targetPalette, plankTexture.getMetadata());
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to generate palette for %s : %s", w, e));
        }
    }

    @Override
    // Textures
    public void addDynamicClientResources(ClientDynamicResourcesHandler handler, ResourceManager manager) {
        super.addDynamicClientResources(handler, manager);

        try (TextureImage normal = TextureImage.open(manager, modRes("quark_variant_chests/oak/normal"));
             TextureImage normal_m = TextureImage.open(manager, EveryCompat.res("model/oak_chest_normal_m"));
             TextureImage normal_o = TextureImage.open(manager, EveryCompat.res("model/oak_chest_normal_o"));
             TextureImage left = TextureImage.open(manager, modRes("quark_variant_chests/oak/left"));
             TextureImage left_m = TextureImage.open(manager, EveryCompat.res("model/oak_chest_left_m"));
             TextureImage left_o = TextureImage.open(manager, EveryCompat.res("model/oak_chest_left_o"));
             TextureImage right = TextureImage.open(manager, modRes("quark_variant_chests/oak/right"));
             TextureImage right_m = TextureImage.open(manager, EveryCompat.res("model/oak_chest_right_m"));
             TextureImage right_o = TextureImage.open(manager, EveryCompat.res("model/oak_chest_right_o"));
             TextureImage left_t = TextureImage.open(manager, EveryCompat.res("model/trapped_chest_left"));
             TextureImage right_t = TextureImage.open(manager, EveryCompat.res("model/trapped_chest_right"));
             TextureImage normal_t = TextureImage.open(manager, EveryCompat.res("model/trapped_chest_normal"))
        ) {

            Respriter respriterNormal = Respriter.masked(normal, normal_m);
            Respriter respriterLeft = Respriter.masked(left, left_m);
            Respriter respriterRight = Respriter.masked(right, right_m);

            Respriter respriterNormalO = Respriter.of(normal_o);
            Respriter respriterLeftO = Respriter.of(left_o);
            Respriter respriterRightO = Respriter.of(right_o);

            trappedChests.blocks.forEach((wood, block) -> {

                CompatTrappedChestBlock b = (CompatTrappedChestBlock) block;

                try (TextureImage plankTexture = TextureImage.open(manager,
                        RPUtils.findFirstBlockTextureLocation(manager, wood.planks))) {

                    List<Palette> targetPalette = Palette.fromAnimatedImage(plankTexture);
                    AnimationMetadataSection meta = plankTexture.getMetadata();

                    List<Palette> overlayPalette = new ArrayList<>();
                    for (var p : targetPalette) {
                        var d1 = p.getDarkest();
                        p.remove(d1);
                        var d2 = p.getDarkest();
                        p.remove(d2);
                        var n1 = new HCLColor(d1.hcl().hue(), d1.hcl().chroma() * 0.75f, d1.hcl().luminance() * 0.4f, d1.hcl().alpha());
                        var n2 = new HCLColor(d2.hcl().hue(), d2.hcl().chroma() * 0.75f, d2.hcl().luminance() * 0.6f, d2.hcl().alpha());
                        var pal = Palette.ofColors(List.of(n1, n2));
                        overlayPalette.add(pal);
                    }

                    {
                        ResourceLocation res = modRes(b.getTextureFolder() + "/" + b.getTexturePath() + "/normal");
                        if (!handler.alreadyHasTextureAtLocation(manager, res)) {
                            ResourceLocation trappedRes = modRes(b.getTextureFolder() + "/" + b.getTexturePath() + "/trap");

                            createChestTextures(handler, normal_t, respriterNormal, respriterNormalO, meta, targetPalette, overlayPalette, res, trappedRes, wood);
                        }
                    }
                    {
                        ResourceLocation res = modRes(b.getTextureFolder() + "/" + b.getTexturePath() + "/left");
                        if (!handler.alreadyHasTextureAtLocation(manager, res)) {
                            ResourceLocation trappedRes = modRes(b.getTextureFolder() + "/" + b.getTexturePath() + "/trap_left");

                            createChestTextures(handler, left_t, respriterLeft, respriterLeftO, meta, targetPalette, overlayPalette, res, trappedRes, wood);
                        }
                    }
                    {
                        ResourceLocation res = modRes(b.getTextureFolder() + "/" + b.getTexturePath() + "/right");
                        if (!handler.alreadyHasTextureAtLocation(manager, res)) {
                            ResourceLocation trappedRes = modRes(b.getTextureFolder() + "/" + b.getTexturePath() + "/trap_right");

                            createChestTextures(handler, right_t, respriterRight, respriterRightO, meta, targetPalette, overlayPalette, res, trappedRes, wood);
                        }
                    }


                } catch (Exception ex) {
                    handler.getLogger().error("Failed to generate Chest block texture for for {} : {}", b, ex);
                }
            });
        } catch (Exception ex) {
            handler.getLogger().error("Could not generate any Chest block texture : ", ex);
        }
    }

    private void createChestTextures(ClientDynamicResourcesHandler handler, TextureImage trappedOverlay,
                                     Respriter respriterLeft, Respriter respriterLeftO,
                                     AnimationMetadataSection baseMeta, List<Palette> basePalette,
                                     List<Palette> overlayPalette, ResourceLocation res, ResourceLocation trappedRes,
                                     WoodType wood) {

        TextureImage recoloredBase = respriterLeft.recolorWithAnimation(basePalette, baseMeta);
        TextureImage recoloredOverlay = respriterLeftO.recolorWithAnimation(overlayPalette, baseMeta);
        recoloredBase.applyOverlay(recoloredOverlay);
        TextureImage trapped = recoloredBase.makeCopy();

        if (!wood.getNamespace().equals("blue_skies") || (wood.getNamespace().equals("blue_skies") && wood.getTypeName().equals("crystallized")))
            handler.dynamicPack.addAndCloseTexture(res, recoloredBase);

        trapped.applyOverlay(trappedOverlay.makeCopy());
        handler.dynamicPack.addAndCloseTexture(trappedRes, trapped);
    }

    @Override
    // Recipes & Tags
    public void addDynamicServerResources(ServerDynamicResourcesHandler handler, ResourceManager manager) {
        super.addDynamicServerResources(handler, manager);

        if (PlatHelper.isModLoaded("botanypots")) {
            hedges.items.forEach((leaves, item) -> {
                var leavesItem = leaves.leaves.asItem();
                BotanyPotsHelper.crop_quarkhedge_recipe(this, item, leavesItem, handler, manager, leaves);
            });
        }

        // is tags generated for logs from FruitFul Fun?
        boolean isGenerated = false;

        // hedge's recipe & logs' tags
        for (Map.Entry<LeavesType, Block> entry : hedges.blocks.entrySet()) {
            LeavesType leavesType = entry.getKey();
            Block block = entry.getValue();

            // general
            if (!leavesType.getNamespace().equals("fruitfulfun"))
                generalHedgeRecipe(leavesType, block, handler, manager);

            // Fruitful Fun
            if (leavesType.getNamespace().equals("fruitfulfun")) {
                switch (leavesType.getTypeName()) {
                    case "apple" -> specialHedgeRecipe("oak", leavesType, block, handler, manager);
                    case "grapefruit", "lemon", "tangerine", "lime", "citron", "pomelo", "orange" ->
                            specialHedgeRecipe("citrus", leavesType, block, handler, manager);
                    case "pomegranate" -> specialHedgeRecipe("jungle", leavesType, block, handler, manager);
                    case "redlove" -> specialHedgeRecipe("redlove", leavesType, block, handler, manager);
                }
            }
            // Creating tags for FruitFul Fun's logs blc generalHedgeRecipe() can't do the job
            if (!isGenerated && leavesType.getNamespace().equals("fruitfulfun")) {
                createTags(new ResourceLocation("minecraft:oak"), handler);
                createTags(new ResourceLocation("minecraft:jungle"), handler);
                createTags(new ResourceLocation("fruitfulfun:citrus"), handler);
                createTags(new ResourceLocation("fruitfulfun:redlove"), handler);
                isGenerated = true;
            }
        }
    }

    // Correcting logs used to craft hedges
    public void generalHedgeRecipe(LeavesType leavesType, Block block, ServerDynamicResourcesHandler handler, ResourceManager manager) {
        // Creating tags for logs
        SimpleTagBuilder tagBuilder = SimpleTagBuilder.of(
                EveryCompat.res(shortenedId() + "/" + leavesType.getWoodType().getAppendableId() + "_logs"));

        Block[] allLogs = {
                leavesType.getWoodType().log, leavesType.getWoodType().getBlockOfThis("stripped_log"),
                leavesType.getWoodType().getBlockOfThis("wood"), leavesType.getWoodType().getBlockOfThis("stripped_wood")
        };

        // adding log, stripped_log, wood, stripped_wood
        for (var log : allLogs) {
            Item item = log.asItem();

            //noinspection ConstantValue
            if (item != null) {
                tagBuilder.addEntry(item);
            }
            else handler.getLogger().warn("{} has no logs for hedge in QuarkModule/generalHedgeRecipe", leavesType.getTypeName());
        }

        handler.dynamicPack.addTag(tagBuilder, Registries.BLOCK);
        handler.dynamicPack.addTag(tagBuilder, Registries.ITEM);

// =====================================================================================================================

        //noinspection OptionalGetWithoutIsPresent
        try (InputStream recipeStream =
                     manager.getResource(modRes("recipes/building/crafting/oak_hedge.json")).get().open()) {
            JsonObject recipe = RPUtils.deserializeJson(recipeStream);

            JsonObject underKey = recipe.getAsJsonObject("key");
            JsonObject underResult = recipe.getAsJsonObject("result");

            // Editing JSON
            // Leaves
            underKey.getAsJsonObject("L")
                    .addProperty("item", Utils.getID(leavesType.leaves).toString());
            // WoodTypes
            underKey.getAsJsonObject("W").addProperty("tag",
                    EveryCompat.MOD_ID + ":" + shortenedId() + "/" + leavesType.getWoodType().getAppendableId() + "_logs");
            // Hedges
            underResult.addProperty("item", Utils.getID(block).toString());


            // Adding the finished recipe to ResourceLocation
            String path = this.shortenedId() + "/" + leavesType.getNamespace() + "/";
            handler.dynamicPack.addJson(EveryCompat.res(path + leavesType.getTypeName() + "_hedge"), recipe,
                    ResType.RECIPES);

        } catch (IOException e) {
            handler.getLogger().error("QuarkModule - Failed to open the recipe file: {0}", e);
        }
    }

    // Creating tags for logs
    public void createTags(ResourceLocation woodType, ServerDynamicResourcesHandler handler) {
        SimpleTagBuilder tagBuilder = SimpleTagBuilder.of(
                EveryCompat.res(shortenedId() + "/" + woodType.getPath() + "_logs"));

        Block[] allLogs = {
                Objects.requireNonNull(WoodTypeRegistry.getValue(woodType)).log,
                Objects.requireNonNull(WoodTypeRegistry.getValue(woodType)).getBlockOfThis("stripped_log"),
                Objects.requireNonNull(WoodTypeRegistry.getValue(woodType)).getBlockOfThis("wood"),
                Objects.requireNonNull(WoodTypeRegistry.getValue(woodType)).getBlockOfThis("stripped_wood")
        };

        // adding log, stripped_log, wood, stripped_wood
        for (var log : allLogs) {
            Item item = log.asItem();

            //noinspection ConstantValue
            if (item != null) {
                tagBuilder.addEntry(item);
            }
            else handler.getLogger().warn("{} has no logs for hedge in QuarkModule/specialHedgeRecipe", woodType);
        }

        handler.dynamicPack.addTag(tagBuilder, Registries.BLOCK);
        handler.dynamicPack.addTag(tagBuilder, Registries.ITEM);
    }

    public void specialHedgeRecipe(String woodType, LeavesType leavesType, Block block, ServerDynamicResourcesHandler handler, ResourceManager manager) {
        //noinspection OptionalGetWithoutIsPresent
        try (InputStream recipeStream =
                     manager.getResource(modRes("recipes/building/crafting/oak_hedge.json")).get().open()) {
            JsonObject recipe = RPUtils.deserializeJson(recipeStream);

            JsonObject underKey = recipe.getAsJsonObject("key");
            JsonObject underResult = recipe.getAsJsonObject("result");

            // Editing JSON
                // Leaves
            underKey.getAsJsonObject("L")
                    .addProperty("item", Utils.getID(leavesType.leaves).toString());
                // WoodTypes
            underKey.getAsJsonObject("W").addProperty("tag",
                    EveryCompat.MOD_ID + ":" + shortenedId() + "/" + woodType + "_logs");
                // Hedges
            underResult.addProperty("item", Utils.getID(block).toString());

            // Adding the finished recipe to ResourceLocation
            String path = this.shortenedId() + "/" + leavesType.getNamespace() + "/";
            handler.dynamicPack.addJson(EveryCompat.res(path + leavesType.getTypeName() + "_hedge"), recipe,
                    ResType.RECIPES);

        } catch (IOException e) {
            handler.getLogger().error("QuarkModule - Failed to open the recipe file: {0}", e);
        }
    }
}
