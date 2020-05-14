package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.ArrayList;
import java.util.List;

import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class PresentableTypeUtils {

    private PresentableTypeUtils() {
    }

    // Converts a fully qualified name type e.g. com.my.component.MyType
    //  to a simple name: MyType. // This is wrong should always go through the method below.
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
            return "List<" + PresentableTypeUtils.from(listItemType) + ">";
        } else {
            return PresentableTypeUtils.from(type);
        }
    }

    public static String presentableTypeOf(Suggestion suggestion, String dynamicType, TypeAndTries typeAndTrieMap) {
        String originalType = suggestion.getReturnType();
        if (MessageAttributes.class.getName().equals(originalType)) {
            return MessageAttributes.class.getSimpleName(); // We keep the message attributes.
        } else {
            Trie dynamicTypeTrie = typeAndTrieMap.getOrDefault(dynamicType, Default.UNKNOWN);
            return PresentableTypeUtils.from(dynamicType, dynamicTypeTrie);
        }
    }

    public static String formatListDisplayType(String type, Trie typeTrie) {
        return PresentableTypeUtils.from(type, typeTrie) + " : " + PresentableTypeUtils.from(typeTrie.listItemType());
    }
}
