package net.mehvahdjukaar.every_compat;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class UnsafeModuleDisabler {

    private final Properties properties;
    private final File propertiesFile;

    private boolean isSafe = true;

    public UnsafeModuleDisabler() {
        this.properties = new Properties();
        this.propertiesFile = PlatHelper.getGamePath().resolve("config/everycomp-hazardous.properties").toFile();

        try {
            if (propertiesFile.exists()) {
                try (FileInputStream fis = new FileInputStream(propertiesFile)) {
                    properties.load(fis);
                }
            } else {
                propertiesFile.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error initializing EC properties", e);
        }
    }

    public void save() {
        try (FileOutputStream output = new FileOutputStream(propertiesFile)) {
            properties.put("a", "false");
            properties.store(output, "Hard disable entire modules. Use at your own risk and don't ask for support if you use this. Write modid = false to disable modules");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isModuleOn(String modId) {
        try {
            if (properties.getProperty(modId) == null) {
                properties.setProperty(modId, String.valueOf(true));
                save();
            } else {
                // Assuming the values in the properties file are boolean
                var ret = Boolean.parseBoolean(properties.getProperty(modId, "true"));
                if (!ret && isSafe) {
                    isSafe = false;
                    EveryCompat.LOGGER.warn("!!! You are using conditional modules registration. Proceed at your own risk and dont complain if you cant connect to servers !!!");
                }
                return ret;
            }
        } catch (Exception ignored) {
        }
        return true;
    }

}
