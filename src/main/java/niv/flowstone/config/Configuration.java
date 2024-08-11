package niv.flowstone.config;

import java.util.concurrent.atomic.AtomicReference;

public final class Configuration {

    static final AtomicReference<Configuration> INSTANCE = new AtomicReference<>(new Configuration());

    private boolean allowDeepslateGenerators = true;
    private boolean allowWorldlyGenerators = true;
    private boolean allowCustomGenerators = false;

    private Boolean debugMode = null;

    private Configuration() {
    }

    private boolean getDebugMode() {
        return this.debugMode != null && this.debugMode;
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

    public static final boolean debugMode() {
        return INSTANCE.get().getDebugMode();
    }
}
