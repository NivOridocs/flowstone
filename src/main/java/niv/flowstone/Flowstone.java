package niv.flowstone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class Flowstone implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("Flowstone");

    public static final String MOD_ID;

    public static final ModContainer MOD_CONTAINER;

    public static final ResourceKey<Registry<FlowstoneGenerator>> GENERATOR;

    static {
        MOD_ID = "flowstone";

        MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();

        GENERATOR = ResourceKey.<FlowstoneGenerator>createRegistryKey(new ResourceLocation(MOD_ID, "generators"));
        DynamicRegistries.register(GENERATOR, FlowstoneGenerator.CODEC);
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Initialize");

        registerDefaultDatapack("overworld_ores", "Overworld Ores 1%");
        registerDatapack("crying_obsidian", "Crying Obsidian 25%");
    }

    private static final void registerDefaultDatapack(String path, String name) {
        ResourceManagerHelper.registerBuiltinResourcePack(
                new ResourceLocation(MOD_ID, path),
                MOD_CONTAINER,
                Component.literal(name),
                ResourcePackActivationType.DEFAULT_ENABLED);
    }

    private static final void registerDatapack(String path, String name) {
        ResourceManagerHelper.registerBuiltinResourcePack(
                new ResourceLocation(MOD_ID, path),
                MOD_CONTAINER,
                Component.literal(name),
                ResourcePackActivationType.NORMAL);
    }
}
