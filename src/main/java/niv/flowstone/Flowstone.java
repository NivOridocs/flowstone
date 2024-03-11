package niv.flowstone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import niv.flowstone.config.FlowstoneConfig;
import niv.flowstone.config.ResourceLocationAdapter;
import niv.flowstone.recipe.FlowstoneRecipe;

public class Flowstone implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("Flowstone");

    public static final String MOD_ID;
    private static final ResourceLocation ID;
    public static final RecipeType<FlowstoneRecipe> FLOWSTONE;
    public static final RecipeSerializer<FlowstoneRecipe> FLOWSTONE_SERIALIZER;

    static {
        MOD_ID = "flowstone";
        ID = new ResourceLocation(MOD_ID, MOD_ID);
        FLOWSTONE = new RecipeType<FlowstoneRecipe>() {
            @Override
            public String toString() {
                return Flowstone.ID.toString();
            }
        };
        FLOWSTONE_SERIALIZER = new FlowstoneRecipe.Serializer();
        Registry.register(BuiltInRegistries.RECIPE_TYPE, ID, FLOWSTONE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ID, FLOWSTONE_SERIALIZER);
    }

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
                    .registerTypeAdapter(ResourceLocation.class, new ResourceLocationAdapter())
                    .setPrettyPrinting()
                    .create();
        }
        return gson;
    }
}
