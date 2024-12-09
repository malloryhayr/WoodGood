package net.mehvahdjukaar.every_compat.mixins;

import net.mehvahdjukaar.every_compat.misc.AllWoodItem;
import net.mehvahdjukaar.every_compat.misc.BlockTypeCycleItemRenderer;
import net.mehvahdjukaar.moonlight.api.client.ICustomItemRendererProvider;
import net.mehvahdjukaar.moonlight.api.client.ItemStackRenderer;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Supplier;

//thank you verifier
@Mixin(AllWoodItem.class)
public abstract class DumbMixin implements ICustomItemRendererProvider {

    @Override
    public Supplier<ItemStackRenderer> getRendererFactory() {
        return () -> new BlockTypeCycleItemRenderer<>(WoodType.class);
    }
}
