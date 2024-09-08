package net.mehvahdjukaar.every_compat.modules.bountiful_fares;

import net.hecco.bountifulfares.block.BFBlocks;
import net.hecco.bountifulfares.block.custom.PicketsBlock;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class BountifulFaresModule extends SimpleModule {
    public final SimpleEntrySet<WoodType, Block> pickets;

    public BountifulFaresModule(String modId) {
        super(modId, "bountifulfares");

        pickets = SimpleEntrySet.builder(WoodType.class, "pickets",
                () -> BFBlocks.OAK_PICKETS, () -> WoodTypeRegistry.OAK_TYPE,
                w -> new PicketsBlock(Utils.copyPropertySafe(w.planks)))
                .copyParentDrop()
                .defaultRecipe()
                .setRenderType(() -> RenderType::cutout)
                .addTexture(modRes("block/oak_pickets"))
                .addTexture(modRes("item/oak_pickets"))
                .build();

        this.addEntry(pickets);
    }
}
