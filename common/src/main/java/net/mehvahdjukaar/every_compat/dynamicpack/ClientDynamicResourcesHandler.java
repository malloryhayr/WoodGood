package net.mehvahdjukaar.every_compat.dynamicpack;

import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.configs.ECConfigs;
import net.mehvahdjukaar.every_compat.misc.SpriteHelper;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynClientResourcesGenerator;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.Logger;


public class ClientDynamicResourcesHandler extends DynClientResourcesGenerator {

    private static ClientDynamicResourcesHandler INSTANCE;

    public static ClientDynamicResourcesHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientDynamicResourcesHandler();
        }
        return INSTANCE;
    }

    private boolean firstInit = false;

    public ClientDynamicResourcesHandler() {
        super(new DynamicTexturePack(EveryCompat.res("generated_pack")));
        //since we place chests textures in its namespace to use its renderer
        if (PlatHelper.isModLoaded("quark")) getPack().addNamespaces("quark");
    }

    @Override
    public Logger getLogger() {
        return EveryCompat.LOGGER;
    }

    @Override
    public boolean dependsOnLoadedPacks() {
        return ECConfigs.SPEC == null || ECConfigs.DEPEND_ON_PACKS.get();
    }

    @Override
    public void addDynamicTranslations(AfterLanguageLoadEvent lang) {
        EveryCompat.forAllModules(m -> {
            m.addTranslations(this, lang);
        });
    }

    @Override
    public void regenerateDynamicAssets(ResourceManager manager) {
        if (!firstInit) {
            SpriteHelper.addHardcodedSprites();
            firstInit = true;
        }
        this.dynamicPack.setGenerateDebugResources(PlatHelper.isDev() || ECConfigs.DEBUG_RESOURCES.get());
        EveryCompat.forAllModules(m -> {
            try {
                m.addDynamicClientResources(this, manager);
            } catch (Exception e) {
                getLogger().error("Failed to generate client dynamic assets for module {}:", m, e);
                if (PlatHelper.isDev()) throw e;
            }
        });

        ExtraTextureGenerator.generateExtraTextures(this, manager);

    }

}
