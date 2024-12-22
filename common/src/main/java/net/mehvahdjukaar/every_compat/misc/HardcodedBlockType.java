package net.mehvahdjukaar.every_compat.misc;

import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import org.jetbrains.annotations.Nullable;

// ugly mess. Too coupled with WoodTypes|LeavesTypes and too many hardcoded exceptions
public class HardcodedBlockType {

    @Nullable
    public static Boolean isWoodBlockAlreadyRegistered(String name, WoodType woodType, String modId, String shortenedId) {
        String woodNamespace = woodType.getNamespace();
        String woodTypeID = woodType.getId().toString();

        // Unrelated to Quark's ancient_leaves & Alex's Cave (ancient_leaves) should be included
        if (modId.equals("quark") && woodNamespace.equals("alexscaves") && woodTypeID.equals("ancient")) return false;

        // Better Nether & Better End have stripped_bark as stripped_wood but bark from Bewitchment caused EC to skip
        if (woodNamespace.matches("betternether|betterend") && shortenedId.equals("bw")) return false;

        // Discarding Dynamic Trees and its addons
        if (woodNamespace.contains("dynamictrees") || woodNamespace.contains("dt")) return true;

        // Garden-Of-The-dead's whistle must be skipped for branches from Regions-Unexplored
        // Nether's Exoticism & Snifferent already has branches, branches from Regions-Unexplored is not needed
        if ((woodNamespace.matches("gardens_of_the_dead|snifferent") ||
                woodTypeID.equals("nethers_exoticism:jabuticaba")) && name.contains("branch"))
            return true;

        // Quark & Woodworks have chest & trapped_chest.
        //is this needed? shouldnt it be covered by the next statements?
        if (woodNamespace.equals("quark") && shortenedId.equals("abnww") && name.contains("chest")) return true;

        // Create's windows will be skipped blc [Let's do] Blooming Nature & Meadow already has windows
        if ((woodNamespace.equals("bloomingnature") || woodNamespace.equals("meadow")) && name.contains("window")) return false;

        // ArchitectPalette's boards will be skipped blc Upgrade-Aqautic already has boards but have no recipes &
        // no item in CreativeMode
        if (woodNamespace.equals("upgrade_aquatic") && (name.equals("driftwood_boards") || name.equals("river_boards")))
            return false;

        // Similar to above, Architect's Palette - boards will be skipped due to the existing boards in Autumnity
        if (woodNamespace.equals("autumnity") && name.equals("maple_boards")) return false;

        // check if TerraFirmaCraft (tfc) mod exist, then won't discards wood types
        if (woodNamespace.equals("tfc")) return false;

        if (woodTypeID.equals("ecologics:azalea")) {
            if (modId.equals("quark")) return false; //ecologics and quark azalea. tbh not sure why needed
        }
        //also this is wrong
        if (woodTypeID.equals("twilightforest:mangrove") && name.equals("mangrove_chest")) {
            return false;//mangrove waaa so much pain
        }

        if (shortenedId.equals("af")) return false; //hardcoding
        // if (this.shortenedId().equals("ap")) return false; //hardcoding dont remember why i had this. Incase you want o
        if (shortenedId.equals("vs")) return false; //we always register everything for these
        if (shortenedId.equals("abnww") && woodNamespace.equals("architects_palette"))
            return false; //we always register everything for these

        return null;
    }

    @Nullable
    public static Boolean isLeavesBlockAlreadyRegistered(String name, LeavesType woodType, String modId, String shortenedId) {
        String woodFrom = woodType.getNamespace();

        // Macaw's Fences&Walls or MrCrayFish's Furniture - hedges will be skipped because Quark already has hedges
        if (woodFrom.equals("quark") && (shortenedId.equals("mcf") || shortenedId.equals("cfm"))) return false;

        return null;
    }

}
