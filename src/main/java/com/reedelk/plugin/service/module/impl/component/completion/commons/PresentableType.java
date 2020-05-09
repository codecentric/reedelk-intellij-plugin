package com.reedelk.plugin.service.module.impl.component.completion.commons;

import com.reedelk.plugin.service.module.impl.component.completion.Trie;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class PresentableType {

    private PresentableType() {
    }

    // Converts a fully qualified name type e.g. com.my.component.MyType
    //  to a simple name: MyType.
    public static String from(String originalType) {
        if (originalType == null) return StringUtils.EMPTY;
        String[] splits = originalType.split(","); // might be multiple types
        List<String> tmp = new ArrayList<>();
        for (String split : splits) {
            String[] segments = split.split("\\.");
            tmp.add(segments[segments.length - 1]);
        }
        return String.join(",", tmp);
    }

    public static String from(String type, Trie typeTrie) {
        if (isNotBlank(typeTrie.listItemType())) {
            // If exists a list item type, it is a list and we want to display it with: List<ItemType>
            String listItemType = typeTrie.listItemType();
            return "List<" + PresentableType.from(listItemType) + ">";
        } else {
            return PresentableType.from(type);
        }
    }

    public static String formatListDisplayType(String type, Trie typeTrie) {
        return PresentableType.from(type, typeTrie) + " : " + PresentableType.from(typeTrie.listItemType());
    }
}
