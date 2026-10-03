package org.example.serializer;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

public final class JsonOps {
    private static final Gson GSON = new Gson();
    public static final JsonOps INSTANCE = new JsonOps();

    public <T> T decode(Codec<T> codec, JsonElement input) {
        return codec.decode(toJavaObject(input));
    }

    public <T> T decode(Codec<T> codec, Reader reader) {
        Object rawJavaMap = GSON.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
        return codec.decode(rawJavaMap);
    }

    public <T> String encode(Codec<T> codec, T value) {
        Object rawJavaData = codec.encode(value);
        return GSON.toJson(rawJavaData);
    }

    private static Object toJavaObject(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();

            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }

            if (primitive.isNumber()) {
                Number number = primitive.getAsNumber();

                if (number.doubleValue() == number.intValue()) {
                    return number.intValue();
                }

                return number.doubleValue();
            }

            return primitive.getAsString();
        }

        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();

            List<Object> list = new ArrayList<>();

            for (JsonElement jsonElement : array) {
                list.add(toJavaObject(jsonElement));
            }
            return list;
        }

        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            Map<String, Object> map = new HashMap<>();

            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                map.put(entry.getKey(), toJavaObject(entry.getValue()));
            }

            return map;
        }

        return element;
    }
}
