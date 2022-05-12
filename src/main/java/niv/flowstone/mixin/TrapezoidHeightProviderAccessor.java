package niv.flowstone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;

@Mixin(TrapezoidHeightProvider.class)
public interface TrapezoidHeightProviderAccessor {

    @Accessor("minOffset")
    YOffset getMinOffset();

    @Accessor("maxOffset")
    YOffset getMaxOffset();

    @Accessor("plateau")
    int getPlateau();

}
