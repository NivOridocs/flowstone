package niv.flowstone.recipe;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import niv.flowstone.Flowstone;

public class FlowstoneRecipe implements Recipe<FlowstoneRecipe.Context> {

    private static final String REPLACE = "replace";
    private static final String WITH = "with";
    private static final String CHANCE = "chance";

    static record Context(Block block) implements NullContainer {
    }

    private final Block replace;

    private final Block with;

    private final double chance;

    public FlowstoneRecipe(Block replace, Block with, double chance) {
        this.replace = requireNonNull(replace);
        this.with = requireNonNull(with);
        this.chance = chance;
    }

    @Override
    public boolean matches(Context context, Level world) {
        return replace.equals(context.block());
    }

    @Override
    public ItemStack assemble(Context var1, RegistryAccess var2) {
        return getResultItem(var2).copy();
    }

    @Override
    public boolean canCraftInDimensions(int var1, int var2) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess var1) {
        return with.asItem().getDefaultInstance();
    }

    @Override
    public ItemStack getToastSymbol() {
        return replace.asItem().getDefaultInstance();
    }

    private Stream<Block> compute(RandomSource random) {
        return random.nextDouble() <= this.chance ? Stream.of(with) : Stream.empty();
    }

    @Override
    public RecipeSerializer<FlowstoneRecipe> getSerializer() {
        return Flowstone.FLOWSTONE_SERIALIZER;
    }

    @Override
    public RecipeType<FlowstoneRecipe> getType() {
        return Flowstone.FLOWSTONE;
    }

    public static Optional<Block> findReplace(Block target, Level level) {
        var blocks = level.getRecipeManager()
                .getRecipesFor(Flowstone.FLOWSTONE, new Context(target), level).stream()
                .map(RecipeHolder::value).flatMap(recipe -> recipe.compute(level.getRandom())).toList();
        return blocks.isEmpty() ? Optional.empty() : Optional.of(blocks.get(level.getRandom().nextInt(blocks.size())));
    }

    public static final class Serializer implements RecipeSerializer<FlowstoneRecipe> {

        static final Codec<FlowstoneRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf(REPLACE).forGetter(r -> r.replace),
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf(WITH).forGetter(r -> r.with),
                Codec.doubleRange(0d, 1d).fieldOf(CHANCE).forGetter(r -> r.chance))
                .apply(instance, FlowstoneRecipe::new));

        @Override
        public Codec<FlowstoneRecipe> codec() {
            return CODEC;
        }

        @Override
        public FlowstoneRecipe fromNetwork(FriendlyByteBuf buf) {
            var replace = BuiltInRegistries.BLOCK.get(buf.readResourceLocation());
            var with = BuiltInRegistries.BLOCK.get(buf.readResourceLocation());
            var chance = buf.readDouble();
            return new FlowstoneRecipe(replace, with, chance);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FlowstoneRecipe recipe) {
            buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(recipe.replace));
            buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(recipe.with));
            buf.writeDouble(recipe.chance);
        }

    }
}
