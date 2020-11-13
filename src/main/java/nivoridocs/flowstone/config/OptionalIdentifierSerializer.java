package nivoridocs.flowstone.config;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.reflect.TypeToken;

import net.minecraft.util.Identifier;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class OptionalIdentifierSerializer implements TypeSerializer<Optional<Identifier>> {

	@Override
	public @Nullable Optional<Identifier> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode node)
			throws ObjectMappingException {
		return Optional.ofNullable(node.getString()).filter(((Predicate<String>) String::isEmpty).negate())
				.map(Identifier::new);
	}

	@Override
	public void serialize(@NonNull TypeToken<?> type, @Nullable Optional<Identifier> optionalIdentifier,
			@NonNull ConfigurationNode node) throws ObjectMappingException {
		Optional.ofNullable(optionalIdentifier).flatMap(Function.identity()).map(Identifier::toString)
				.ifPresent(node::setValue);
	}

}
