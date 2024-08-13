package niv.flowstone.config;

import static niv.flowstone.Flowstone.MOD_ID;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.loader.api.FabricLoader;

public final class ConfigurationLoader {

    private static final long DELAY = 5000; // ms (5s)

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Supplier<File> configurationFile = Suppliers
            .memoize(() -> FabricLoader.getInstance().getGameDir()
                    .resolve("config")
                    .resolve(MOD_ID + ".json")
                    .toFile());

    private static Configuration configuration = new Configuration();

    private static long timestamp = 0;

    private static long lastModified = 0;

    private static boolean fireOnReturn = true;

    private ConfigurationLoader() {
    }

    static final Configuration getConfiguration() {
        synchConfiguration();
        if (fireOnReturn) {
            Configuration.LOADED.invoker().run();
            fireOnReturn = false;
        }
        return configuration;
    }

    private static final synchronized void synchConfiguration() {
        var now = System.currentTimeMillis();
        if (timestamp + DELAY < now) {
            var file = configurationFile.get();
            if (create(file) || read(file)) {
                write(file);
            }
            timestamp = now;
        }
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
        } else {
            lastModified = newLastModified;
        }
        try (var reader = new FileReader(file)) {
            var result = gson.fromJson(reader, Configuration.class);
            if (result != null) {
                configuration = result;
                fireOnReturn = true;
            }
            return true;
        } catch (JsonSyntaxException | JsonIOException | IOException ex) {
            throw new IllegalStateException("Failed to read configuration file", ex);
        }
    }

    private static final void write(File file) {
        try (var writer = new FileWriter(file)) {
            gson.toJson(configuration, writer);
            lastModified = file.lastModified();
        } catch (JsonIOException | IOException ex) {
            throw new IllegalStateException("Failed to write configuration file", ex);
        }
    }
}
