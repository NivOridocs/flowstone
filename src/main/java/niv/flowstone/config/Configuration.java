package niv.flowstone.config;

import java.util.concurrent.atomic.AtomicReference;

public final class Configuration {

    static final AtomicReference<Configuration> INSTANCE = new AtomicReference<>(new Configuration());

    private boolean allowDeepslateGenerators = true;
    private boolean allowWorldlyGenerators = true;
    private boolean allowCustomGenerators = false;

    private Configuration() {
    }

    public static final boolean allowDeepslateGenerators() {
        return INSTANCE.get().allowDeepslateGenerators;
    }

    public static final boolean allowWorldlyGenerators() {
        return INSTANCE.get().allowWorldlyGenerators;
    }

    public static final boolean allowCustomGenerators() {
        return INSTANCE.get().allowCustomGenerators;
    }
}
