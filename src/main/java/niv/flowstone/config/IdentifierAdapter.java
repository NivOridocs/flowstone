package niv.flowstone.config;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class IdentifierAdapter implements JsonSerializer<Identifier>, JsonDeserializer<Identifier> {

    @Override
    public Identifier deserialize(JsonElement value, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            return Identifier.tryParse(value.getAsString());
        } catch (InvalidIdentifierException ex) {
            throw new JsonParseException(ex);
        }
    }

    @Override
    public JsonElement serialize(Identifier value, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(value.toString());
    }

}
