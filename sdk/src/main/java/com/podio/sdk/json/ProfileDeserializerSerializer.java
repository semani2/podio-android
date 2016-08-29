package com.podio.sdk.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.podio.sdk.domain.Profile;

import java.lang.reflect.Type;

/**
 * Created by sai on 8/29/16.
 */
public class ProfileDeserializerSerializer implements JsonDeserializer<Profile>, JsonSerializer<Profile> {
    @Override
    public Profile deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(jsonElement == null || jsonElement.isJsonNull()) {
            return null;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return context.deserialize(jsonObject, Profile.class);
    }

    @Override
    public JsonElement serialize(Profile profile, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(profile, Profile.class);
    }
}
