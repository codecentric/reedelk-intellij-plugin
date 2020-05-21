package com.reedelk.plugin.service.module.impl.component.completion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class FullyQualifiedName {

    private static final String LIST_SIMPLE_NAME_AND_ITEM_TYPE_FORMAT = "%s : %s";
    private static final String FORMAT_LIST = "List<%s>";

    public static String toSimpleName(String original) {
        if (original == null) return EMPTY;
        String[] splits = original.split(","); // might be multiple types
        List<String> tmp = new ArrayList<>();
        for (String split : splits) {
            String[] segments = split.split("\\.");
            tmp.add(segments[segments.length - 1]);
        }
        return String.join(",", tmp);
    }

    @NotNull
    public static String toSimpleName(@Nullable String type, @NotNull Trie typeTrie, @NotNull TypeAndTries typeAndTries) {
        if (type == null) {
            return EMPTY;
        } else if (isNotBlank(typeTrie.displayName())) {
            return typeTrie.displayName();
        } else if (isNotBlank(typeTrie.listItemType())) {
            // If exists a list item type, it is a list and we want to display it with: List<ItemType>
            String listItemType = typeTrie.listItemType();
            return formatList(listItemType, typeAndTries);
        } else {
            return FullyQualifiedName.toSimpleName(type);
        }
    }

    @NotNull
    public static String toSimpleName(@Nullable String type, @NotNull TypeAndTries allTypesMap) {
        Trie typeTrie = allTypesMap.getOrDefault(type);
        return toSimpleName(type, typeTrie, allTypesMap);
    }

    // Used by join
    public static String formatList(String listItemType, TypeAndTries typeAndTries) {
        Trie listItemTypeTrie = typeAndTries.getOrDefault(listItemType);
        String listItemTrieSimpleName = toSimpleName(listItemType, listItemTypeTrie, typeAndTries);
        return String.format(FORMAT_LIST, listItemTrieSimpleName);
    }

    @NotNull
    public static String formatUnrolledListDisplayType(TypeProxy typeProxy, TypeAndTries typeAndTries) {
        return String.format(LIST_SIMPLE_NAME_AND_ITEM_TYPE_FORMAT,
                typeProxy.toSimpleName(typeAndTries),
                typeProxy.listItemType(typeAndTries).toSimpleName(typeAndTries));
    }
}
