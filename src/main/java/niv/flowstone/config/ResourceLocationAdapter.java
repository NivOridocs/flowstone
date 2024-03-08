package niv.flowstone.config;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationAdapter implements JsonSerializer<ResourceLocation>, JsonDeserializer<ResourceLocation> {

    @Override
    public ResourceLocation deserialize(JsonElement value, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return new ResourceLocation(value.getAsString());
        } catch (ResourceLocationException ex) {
            throw new JsonParseException(ex);
        }
    }

    @Override
    public JsonElement serialize(ResourceLocation value, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(value.toString());
    }

}
