package com.esb.plugin.graph.serializer;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class SerializerUtilities {

    /**
     * Instantiates a new JSON Object with an ordered HashMap. This
     * is needed so that properties are serialized in the same order
     * they are added during the serialization process.
     *
     * @return a new JSONObject instance with an internal representation
     * which keeps the order in which properties are added to the Object.
     */
    public static JSONObject newJSONObject() {
        Map<String, String> orderedMap = new LinkedHashMap<>();
        return new JSONObject(orderedMap);
    }
}
