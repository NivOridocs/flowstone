package niv.flowstone.config;

import static niv.flowstone.Flowstone.MOD_ID;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.loader.api.FabricLoader;

public class ConfigurationLoader {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Supplier<File> configurationFile = Suppliers
            .memoize(() -> FabricLoader.getInstance().getGameDir()
                    .resolve("config")
                    .resolve(MOD_ID + ".json")
                    .toFile());

    private static long lastModified = 0;

    private ConfigurationLoader() {
    }

    public static final void load() {
        var file = configurationFile.get();
        if (create(file) || read(file)) {
            write(file);
        }
        lastModified = file.lastModified();
    }

    private static final boolean create(File file) {
        try {
            return (file.getParentFile().isDirectory() || file.getParentFile().mkdirs()) && file.createNewFile();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to create configuration file", ex);
        }
    }

    private static final boolean read(File file) {
        var newLastModified = file.lastModified();
        if (newLastModified <= lastModified) {
            return false;
        }
        try (var reader = new FileReader(file)) {
            return Optional.ofNullable(gson.fromJson(reader, Configuration.class))
                    .map(Configuration.INSTANCE::getAndSet).isPresent();
        } catch (JsonSyntaxException | JsonIOException | IOException ex) {
            throw new IllegalStateException("Failed to read configuration file", ex);
        }
    }

    private static final void write(File file) {
        try (var writer = new FileWriter(file)) {
            Configuration.INSTANCE.getAndUpdate(value -> {
                gson.toJson(value, writer);
                return value;
            });
        } catch (JsonIOException | IOException ex) {
            throw new IllegalStateException("Failed to write configuration file", ex);
        }
    }
}
