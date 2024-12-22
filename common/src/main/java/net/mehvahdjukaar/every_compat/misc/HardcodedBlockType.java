package net.mehvahdjukaar.every_compat.misc;

import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import org.jetbrains.annotations.Nullable;

// ugly mess. Too coupled with WoodTypes|LeavesTypes and too many hardcoded exceptions
public class HardcodedBlockType {

    @Nullable
    public static Boolean isWoodBlockAlreadyRegistered(String blockName, WoodType woodType, String supportedModId, String shortenedId) {
        String woodMod = woodType.getNamespace();
        String woodTypeID = woodType.getId().toString();

        // Better Nether & Better End have stripped_bark as stripped_wood but bark from Bewitchment caused EC to skip
        if (woodMod.matches("betternether|betterend") && shortenedId.equals("bw")) return false;

        // Discarding Dynamic Trees and its addons
        if (woodMod.contains("dynamictrees") || woodMod.contains("dt")) return true;

        // Garden-Of-The-dead's whistle must be skipped for branches from Regions-Unexplored
        // Nether's Exoticism & Snifferent already has branches, branches from Regions-Unexplored is not needed
        if ((woodMod.matches("gardens_of_the_dead|snifferent") ||
                woodTypeID.equals("nethers_exoticism:jabuticaba")) && blockName.contains("branch"))
            return true;

        // Quark & Woodworks have chest & trapped_chest.
        //is this needed? shouldnt it be covered by the next statements?
        if (woodMod.equals("quark") && shortenedId.equals("abnww") && blockName.contains("chest")) return true;

        // Create's windows will be skipped blc [Let's do] Blooming Nature & Meadow already has windows
        if ((woodMod.equals("bloomingnature") || woodMod.equals("meadow")) && blockName.contains("window")) return false;

        // ArchitectPalette's boards will be skipped blc Upgrade-Aqautic already has boards but have no recipes &
        // no item in CreativeMode
        if (woodMod.equals("upgrade_aquatic") && (blockName.equals("driftwood_boards") || blockName.equals("river_boards")))
            return false;

        // Similar to above, Architect's Palette - boards will be skipped due to the existing boards in Autumnity
        if (woodMod.equals("autumnity") && blockName.equals("maple_boards")) return false;

        // check if TerraFirmaCraft (tfc) mod exist, then won't discards wood types
        if (woodMod.equals("tfc")) return false;

        if (woodTypeID.equals("ecologics:azalea")) {
            if (supportedModId.equals("quark")) return false; //ecologics and quark azalea. tbh not sure why needed
        }
        //also this is wrong
        if (woodTypeID.equals("twilightforest:mangrove") && blockName.equals("mangrove_chest")) {
            return false;//mangrove waaa so much pain
        }

        if (shortenedId.equals("af")) return false; //hardcoding
        // if (this.shortenedId().equals("ap")) return false; //hardcoding dont remember why i had this. Incase you want o
        if (shortenedId.equals("vs")) return false; //we always register everything for these
        if (shortenedId.equals("abnww") && woodMod.equals("architects_palette"))
            return false; //we always register everything for these

        return null;
    }

    @Nullable
    public static Boolean isLeavesBlockAlreadyRegistered(String blockName, LeavesType leavesType, String supportedModId, String shortenedId) {
        String leaveMod = leavesType.getNamespace();
        String leaveTypeID = leavesType.getId().toString();

        // Unrelated to Quark's ancient_leaves & Alex's Cave (ancient_leaves) should be included
        if (supportedModId.equals("quark") && leaveTypeID.equals("alexscaves:ancient")) return false;

        // Macaw's Fences&Walls or MrCrayFish's Furniture - hedges will be skipped because Quark already has hedges
        if (leaveMod.equals("quark") && (shortenedId.equals("mcf") || shortenedId.equals("cfm"))) return false;

        return null;
    }

}
