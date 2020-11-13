package nivoridocs.flowstone.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.reflect.TypeToken;

import net.minecraft.util.Identifier;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class IdentifierSerializer implements TypeSerializer<Identifier> {

	@Override
	public @Nullable Identifier deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode node)
			throws ObjectMappingException {
		return new Identifier(node.getString());
	}

	@Override
	public void serialize(@NonNull TypeToken<?> type, @Nullable Identifier identifier, @NonNull ConfigurationNode node)
			throws ObjectMappingException {
		node.setValue(identifier == null ? "" : identifier.toString());
	}

}
