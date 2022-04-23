package niv.flowstone.data;

import static niv.flowstone.FlowstoneMod.MOD_ID;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import niv.flowstone.util.Generator;
import niv.flowstone.util.Generators;

public class GeneratorsResourceReloadListener implements SimpleSynchronousResourceReloadListener {

    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
                .disableHtmlEscaping()
                .create();
    }

    private static final String GENERATORS = "generators";

    @Override
    public Identifier getFabricId() {
        return new Identifier(MOD_ID, GENERATORS);
    }

    @Override
    public void reload(ResourceManager manager) {
        for (var identifier : manager.findResources(GENERATORS, path -> path.endsWith(".json"))) {
            try {
                for (var resource : manager.getAllResources(identifier)) {
                    var reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                    var generatorResource = JsonHelper.deserialize(GSON, reader, GeneratorResource.class);
                    process(generatorResource);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void process(GeneratorResource generatorResource) {
        var blockstate = Registry.BLOCK.get(new Identifier(generatorResource.getBlock())).getDefaultState();
        for (var configurationResource : generatorResource.getConfigurations()) {

            var generator = configurationResource.getPlateau() == null
                    ? new Generator(blockstate, configurationResource.getMin(), configurationResource.getMax(),
                            configurationResource.getDensity())
                    : new Generator(blockstate, configurationResource.getMin(), configurationResource.getMax(),
                            configurationResource.getPlateau(), configurationResource.getDensity());

            if (configurationResource.getBiomes() == null || configurationResource.getBiomes().isEmpty()) {
                Generators.put(generator);
            } else {
                for (var biomeIdentifier : configurationResource.getBiomes()) {
                    var biome = BuiltinRegistries.BIOME.get(new Identifier(biomeIdentifier));
                    Generators.put(biome, generator);
                }
            }
        }
    }

}
