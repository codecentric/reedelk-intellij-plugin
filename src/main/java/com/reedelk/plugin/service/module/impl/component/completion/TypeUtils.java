package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.reedelk.plugin.service.module.impl.component.completion.Default.DEFAULT_RETURN_TYPE;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class TypeUtils {

    private TypeUtils() {
    }

    // Converts a fully qualified type name to a simple name,
    // e.g: com.my.component.MyType > MyType
    public static String toSimpleName(String type) {
        if (type == null) return StringUtils.EMPTY;
        String[] splits = type.split(","); // might be multiple types
        List<String> tmp = new ArrayList<>();
        for (String split : splits) {
            String[] segments = split.split("\\.");
            tmp.add(segments[segments.length - 1]);
        }
        return String.join(",", tmp);
    }

    public static String toSimpleName(String type, Trie typeTrie) {
        if (isNotBlank(typeTrie.listItemType())) {
            // If exists a list item type, it is a list and we want to display it with: List<ItemType>
            String listItemType = typeTrie.listItemType();
            return "List<" + TypeUtils.toSimpleName(listItemType) + ">";
        } else {
            return TypeUtils.toSimpleName(type);
        }
    }

    public static String presentableTypeOf(Suggestion suggestion, String dynamicType, TypeAndTries typeAndTrieMap) {
        String originalType = suggestion.getReturnType();
        if (MessageAttributes.class.getName().equals(originalType)) {
            return MessageAttributes.class.getSimpleName(); // We keep the message attributes.
        } else {
            Trie dynamicTypeTrie = typeAndTrieMap.getOrDefault(dynamicType, Default.UNKNOWN);
            return TypeUtils.toSimpleName(dynamicType, dynamicTypeTrie);
        }
    }

    public static String formatListDisplayType(String type, Trie typeTrie) {
        return TypeUtils.toSimpleName(type, typeTrie) + " : " + TypeUtils.toSimpleName(typeTrie.listItemType());
    }

    @Nullable
    public static String typeDisplayValueOf(@NotNull TypeAndTries allTypesMap, @Nullable String type) {
        if (type == null) return null;
        Trie typeTrie = allTypesMap.getOrDefault(type, Default.UNKNOWN);
        return StringUtils.isNotBlank(typeTrie.displayName()) ?
                typeTrie.displayName() :
                TypeUtils.toSimpleName(type, typeTrie);
    }

    public static String returnTypeOrDefault(String type) {
        return StringUtils.isBlank(type) ? DEFAULT_RETURN_TYPE : type;
    }
}
