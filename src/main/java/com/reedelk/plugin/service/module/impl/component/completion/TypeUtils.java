package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.reedelk.plugin.service.module.impl.component.completion.Default.DEFAULT_RETURN_TYPE;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class TypeUtils {

    private static final String LIST_SIMPLE_NAME_FORMAT = "List<%s>";
    private static final String LIST_SIMPLE_NAME_AND_ITEM_TYPE_FORMAT = "%s : %s";

    private TypeUtils() {
    }

    // Converts a fully qualified type name to a simple name, e.g: com.my.component.MyType > MyType.
    public static String toSimpleName(String type) {
        if (type == null) return EMPTY;
        String[] splits = type.split(","); // might be multiple types
        List<String> tmp = new ArrayList<>();
        for (String split : splits) {
            String[] segments = split.split("\\.");
            tmp.add(segments[segments.length - 1]);
        }
        return String.join(",", tmp);
    }

    @NotNull
    public static String toSimpleName(@Nullable String type, @NotNull Trie typeTrie) {
        if (type == null) {
            return EMPTY;
        } else if (isNotBlank(typeTrie.displayName())) {
            return typeTrie.displayName();
        } else if (isNotBlank(typeTrie.listItemType())) {
            // If exists a list item type, it is a list and we want to display it with: List<ItemType>
            String listItemType = typeTrie.listItemType();
            return String.format(LIST_SIMPLE_NAME_FORMAT, TypeUtils.toSimpleName(listItemType));
        } else {
            return TypeUtils.toSimpleName(type);
        }
    }

    @NotNull
    public static String toSimpleName(@Nullable String type, @NotNull TypeAndTries allTypesMap) {
        Trie typeTrie = allTypesMap.getOrDefault(type, Default.UNKNOWN);
        return TypeUtils.toSimpleName(type, typeTrie);
    }

    @NotNull
    public static String toSimpleName(@Nullable String type, @NotNull TypeAndTries allTypesMap, @NotNull Suggestion suggestion) {
        // Used to create dynamic suggestions. For dynamic suggestions we always keep as simple name 'MessageAttributes'
        // type without using the fully qualified name of the specific type.
        String originalType = suggestion.getReturnType();
        if (MessageAttributes.class.getName().equals(originalType)) {
            // We keep the message attributes type simple name to avoid confusion and always display 'MessageAttributes' type.
            return MessageAttributes.class.getSimpleName();
        } else {
            // If the type is MessagePayload, we lookup the type and convert it to a simple name.
            return toSimpleName(type, allTypesMap);
        }
    }

    @NotNull
    public static String formatListDisplayType(String type, Trie typeTrie) {
        return String.format(LIST_SIMPLE_NAME_AND_ITEM_TYPE_FORMAT,
                TypeUtils.toSimpleName(type, typeTrie),
                TypeUtils.toSimpleName(typeTrie.listItemType()));
    }

    @NotNull
    public static String returnTypeOrDefault(String type) {
        return isNotBlank(type) ? type : DEFAULT_RETURN_TYPE;
    }
}
