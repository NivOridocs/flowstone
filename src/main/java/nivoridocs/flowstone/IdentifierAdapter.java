package nivoridocs.flowstone;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.util.Identifier;

public class IdentifierAdapter implements JsonSerializer<Identifier>, JsonDeserializer<Identifier> {

	@Override
	public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		try {
			return new Identifier(json.getAsString());
		} catch (ClassCastException | IllegalStateException ex) {
			throw new JsonParseException(ex.getMessage(), ex);
		}
	}

	@Override
	public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

}
