package net.mehvahdjukaar.every_compat;

import net.mehvahdjukaar.every_compat.configs.ECConfigs;
import net.mehvahdjukaar.every_compat.misc.AllWoodItem;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ECRegistry {

    public static void init(){

    }

    public static final Supplier<AllWoodItem> ALL_WOODS = RegHelper.registerItem(EveryCompat.res("all_woods"), AllWoodItem::new);

    @Nullable
    public static final RegSupplier<CreativeModeTab> MOD_TAB = ECConfigs.TAB_ENABLED.get() ?
                    RegHelper.registerCreativeModeTab(EveryCompat.res(EveryCompat.MOD_ID),
                            true,
                            builder -> builder.icon(() -> ALL_WOODS.get().getDefaultInstance())
                                    .backgroundSuffix("item_search.png")
                                    .title(Component.translatable("itemGroup.everycomp.everycomp"))
                                    .build())
                    : null;
}
