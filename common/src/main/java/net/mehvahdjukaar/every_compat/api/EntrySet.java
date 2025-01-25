package net.mehvahdjukaar.every_compat.api;

import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynClientResourcesGenerator;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicDataPack;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface EntrySet<T extends BlockType> {


    String getName();

    @NotNull
    default String getChildKey(SimpleModule module) {
        return module.getModId() + ":" + getName();
    }

    Class<T> getTypeClass();

    void addTranslations(SimpleModule module, AfterLanguageLoadEvent lang);

    void registerBlocks(SimpleModule module, Registrator<Block> registry, Collection<T> woodTypes);

    void registerItems(SimpleModule module, Registrator<Item> registry);

    void registerTiles(SimpleModule module, Registrator<BlockEntityType<?>> registry);

    void setRenderLayer();

    void generateTags(SimpleModule module, DynamicDataPack pack, ResourceManager manager);

    void generateLootTables(SimpleModule module, DynamicDataPack pack, ResourceManager manager);

    void generateRecipes(SimpleModule module, DynamicDataPack pack, ResourceManager manager);

    void generateModels(SimpleModule module, DynClientResourcesGenerator handler, ResourceManager manager);

    void generateTextures(SimpleModule module, DynClientResourcesGenerator handler, ResourceManager manager);

    default void setupExistingTiles() {
    }

    //used for tabs
    @Nullable
    default Item getItemOf(T type) {
        return null;
    }

    void registerItemColors(ClientHelper.ItemColorEvent event);

    void registerBlockColors(ClientHelper.BlockColorEvent event);

    void registerItemsToExistingTabs(SimpleModule module, RegHelper.ItemToTabEvent event);

    @Nullable
    default Item getItemForECTab(T type) {
        return this.getItemOf(type);
    }

    int getBlockCount();
}
