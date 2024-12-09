package net.mehvahdjukaar.every_compat.api;

import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.every_compat.dynamicpack.ServerDynamicResourcesHandler;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.*;

public class SimpleModule extends CompatModule {

    private final String shortId;
    private final Map<String, EntrySet<?>> entries = new LinkedHashMap<>();
    private final Set<Class<? extends BlockType>> affectedTypes = new HashSet<>();

    protected int bloat = 0;

    public SimpleModule(String modId, String shortId) {
        super(modId);
        this.shortId = shortId;
    }

    public SimpleModule(String modId, String shortId, String myNamespace) {
        super(modId, myNamespace);
        this.shortId = shortId;
    }

    public ResourceLocation makeMyRes(String name) {
        return new ResourceLocation(getMyNamespace(), name);
    }

    @Override
    public Collection<Class<? extends BlockType>> getAffectedTypes() {
        return affectedTypes;
    }

    @Override
    public int bloatAmount() {
        return bloat;
    }

    public <T extends BlockType, S extends EntrySet<T>> S addEntry(S entryHolder) {
        var old = this.entries.put(entryHolder.getName(), entryHolder);
        if (old != null) {
            throw new UnsupportedOperationException(String.format("This module already has an entry set with name %s", entryHolder.getName()));
        }
        this.affectedTypes.add(entryHolder.getTypeClass());
        //ugly
        EveryCompat.trackChildType(entryHolder.getTypeClass(), entryHolder.getChildKey(this));
        return entryHolder;
    }

    public Collection<EntrySet<?>> getEntries() {
        return entries.values();
    }

    public EntrySet<?> getEntry(String name) {
        var e = entries.get(name);
        if (e == null)
            throw new UnsupportedOperationException(String.format("This module does not have entries of type %s", name));
        return e;
    }

    @Override
    public String shortenedId() {
        return shortId;
    }

    @Override
    public void addTranslations(ClientDynamicResourcesHandler clientDynamicResourcesHandler, AfterLanguageLoadEvent lang) {
        getEntries().forEach(e -> e.addTranslations(this, lang));
    }

    public <T extends BlockType> void registerBlocks(Class<T> typeClass,
                                                     Registrator<Block> registry, Collection<T> types) {
        int blockCount = 0;
        for (var e : getEntries()) {
            if (e.getTypeClass().isAssignableFrom(typeClass)) {
                registerBlocksTyped(registry, types, e);
                blockCount += e.getBlockCount();
            }
        }
        bloat += blockCount;
        if (blockCount > 0) {
            EveryCompat.LOGGER.info("{}: registered {} {} blocks", this, blockCount, typeClass.getSimpleName());
        }
    }

    private <T extends BlockType> void registerBlocksTyped(Registrator<Block> registry,
                                                           Collection<?> types, EntrySet<T> e) {
        e.registerBlocks(this, registry, (Collection<T>) types);
    }

    @Override
    public void registerItems(Registrator<Item> registry) {
        getEntries().forEach(e -> e.registerItems(this, registry));
    }

    @Override
    public void registerTiles(Registrator<BlockEntityType<?>> registry) {
        getEntries().forEach(e -> e.registerTiles(this, registry));
    }

    @Override
    public void addDynamicServerResources(ServerDynamicResourcesHandler handler, ResourceManager manager) {
        getEntries().forEach(e -> {
            e.generateLootTables(this, handler.dynamicPack, manager);
            e.generateRecipes(this, handler.dynamicPack, manager);
            e.generateTags(this, handler.dynamicPack, manager);
        });
    }

    @Override
    public void addDynamicClientResources(ClientDynamicResourcesHandler handler, ResourceManager manager) {
        getEntries().forEach(e -> {
            try {
                e.generateModels(this, handler, manager);
            } catch (Exception ex) {
                EveryCompat.LOGGER.error("Failed to generate models for entry set {}:", e, ex);
                if (PlatHelper.isDev()) throw ex;
            }
        });
        getEntries().forEach(e -> {
            try {
                e.generateTextures(this, handler, manager);
            } catch (Exception ex) {
                EveryCompat.LOGGER.error("Failed to generate textures for entry set {}:", e, ex);
                if (PlatHelper.isDev()) throw ex;
            }
        });
    }

    @Override
    public void registerBlockColors(ClientHelper.BlockColorEvent event) {
        getEntries().forEach(e -> e.registerBlockColors(event));
    }

    @Override
    public void registerItemColors(ClientHelper.ItemColorEvent event) {
        getEntries().forEach(e -> e.registerItemColors(event));
    }

    @Override
    public void onClientSetup() {
        getEntries().forEach(EntrySet::setRenderLayer);
    }

    @Override
    public void onModSetup() {
        getEntries().forEach(EntrySet::setupExistingTiles);
    }

    public static void appendTileEntityBlocks(BlockEntityType<?> be, Collection<? extends Block> blocks) {
        be.validBlocks = new HashSet<>(be.validBlocks);
        be.validBlocks.addAll(blocks);
    }

    @Override
    public void registerItemsToExistingTabs(RegHelper.ItemToTabEvent event) {
        getEntries().forEach(e -> e.registerItemsToExistingTabs(this, event));
    }

    @Override
    public <T extends BlockType> List<Item> getAllItemsOfType(T type) {
        List<Item> l = new ArrayList<>();
        for (EntrySet<?> entrySet : entries.values()) {
            if (entrySet.getTypeClass().isAssignableFrom(type.getClass())) {
                var itemOfType = ((EntrySet<T>) entrySet).getItemForECTab(type);
                if (itemOfType != null) l.add(itemOfType);
            }
        }
        return l;
    }


    // ugly mess. Too coupled with wood types and too many hardcoded exceptions
    //TODO: improve
    public boolean isEntryAlreadyRegistered(String name, BlockType woodType, Registry<?> registry) {
        //ec:twigs/bop/willow_table
        name = name.substring(name.lastIndexOf("/") + 1); //gets the base name

        String woodFrom = woodType.getNamespace();

        String n1 = woodFrom + "/" + name; //quark/blossom_chair
        String n2 = woodFrom + "_" + name; //quark_blossom_chair

        if (this.getAlreadySupportedMods().contains(woodFrom)) return true;

        // Better Nether & Better End have stripped_bark as stripped_wood but bark from Bewitchment caused EC to skip
        if (woodFrom.matches("betternether|betterend") && shortenedId().equals("bw")) return false;

        // Discarding Dynamic Trees and its addons
        if (woodFrom.contains("dynamictrees") || woodFrom.contains("dt")) return true;

        // Garden-Of-The-dead's whistle must be skipped for branches from Regions-Unexplored
        // Nether's Exoticism & Snifferent already has branches, branches from Regions-Unexplored is not needed
        if ((woodFrom.matches("gardens_of_the_dead|snifferent") ||
                woodType.getId().toString().equals("nethers_exoticism:jabuticaba")) && name.contains("branch"))
            return true;

        // Quark & Woodworks have chest & trapped_chest
        if (woodFrom.equals("quark") && shortenedId().equals("abnww") && name.contains("chest")) return true;

        // Macaw's Fences&Walls or MrCrayFish's Furniture - hedges will be skipped because Quark already has hedges
        if (woodFrom.equals("quark") && (shortenedId().equals("mcf") || shortenedId().equals("cfm"))) return false;

        // Create's windows will be skipped blc [Let's do] Blooming Nature & Meadow already has windows
        if ((woodFrom.equals("bloomingnature") || woodFrom.equals("meadow")) && name.contains("window")) return false;

        // ArchitectPalette's boards will be skipped blc Upgrade-Aqautic already has boards but have no recipes &
        // no item in CreativeMode
        if (woodFrom.equals("upgrade_aquatic") && (name.equals("driftwood_boards") || name.equals("river_boards")))
            return false;

        // Similar to above, Architect's Palette - boards will be skipped due to the existing boards in Autumnity
        if (woodFrom.equals("autumnity") && name.equals("maple_boards")) return false;

        // check if TerraFirmaCraft (tfc) mod exist, then won't discards wood types
        if (woodFrom.equals("tfc")) return false;

        //discards wood types from this mod
        if (woodFrom.equals(modId)) return true; //quark, blossom

        if (woodType.getId().toString().equals("ecologics:azalea")) {
            if (modId.equals("quark")) return false; //ecologics and quark azalea. tbh not sure why needed
        }
        if (woodType.getId().toString().equals("twilightforest:mangrove")) {
            return name.equals("mangrove_chest");//mangrove waaa so much pain
        }

        if (woodType.getId().toString().equals("architects_palette:twisted")) {
            return name.equals("vct:twisted_crafting_table");
        }
        if (woodType.getId().toString().equals("biomesoplenty:fir")) {
            return name.equals("vct:fir_crafting_table");
        }
        if (woodType.getId().toString().equals("biomesoplenty:jacaranda")) {
            return name.equals("vct:jacaranda_crafting_table");
        }
        if (woodType.getId().toString().equals("biomesoplenty:maple")) {
            return name.equals("vct:maple_crafting_table");
        }
        if (woodType.getId().toString().equals("ecologics:azalea")) {
            return name.equals("vct:azalea_crafting_table");
        }
        if (woodType.getId().toString().equals("ecologics:walnut")) {
            return name.equals("vct:walnut_crafting_table");
        }

        if (registry.containsKey(new ResourceLocation(modId, name)) || //ones from the mod they are from. usually include vanilla types
                registry.containsKey(new ResourceLocation(modId, n2))) return true;
        if (this.shortenedId().equals("af")) return false; //hardcoding
        // if (this.shortenedId().equals("ap")) return false; //hardcoding dont remember why i had this. Incase you want o
        if (this.shortenedId().equals("vs")) return false; //we always register everything for these
        if (this.shortenedId().equals("abnww") && woodFrom.equals("architects_palette"))
            return false; //we always register everything for these

        if (registry.containsKey(new ResourceLocation(woodFrom, name))) return true;

        for (var c : EveryCompat.getCompatMods()) {
            String compatModId = c.modId();  //bopcomp : bop->quark, twigs
            //if the wood is from the mod this adds compat for && it supports this block type
            if (c.woodsFrom().contains(woodFrom) && c.blocksFrom().contains(modId)) {
                if (registry.containsKey(new ResourceLocation(compatModId, name))) return true;
                if (registry.containsKey(new ResourceLocation(compatModId, n1))) return true;
                if (registry.containsKey(new ResourceLocation(compatModId, n2))) return true;
            }
        }
        return false;
    }

}
