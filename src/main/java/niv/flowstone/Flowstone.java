package niv.flowstone;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import niv.flowstone.config.FlowstoneConfig;
import niv.flowstone.config.IdentifierAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Flowstone implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("Flowstone");

    private static Gson gson;

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Initialize");

        FlowstoneConfig.load(getGson());
    }

    private static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Identifier.class, new IdentifierAdapter())
                    .setPrettyPrinting()
                    .create();
        }
        return gson;
    }
}
