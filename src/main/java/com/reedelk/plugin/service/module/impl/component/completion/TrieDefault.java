package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reedelk.runtime.api.commons.Preconditions.checkNotNull;
import static com.reedelk.runtime.api.commons.StringUtils.isBlank;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class TrieDefault extends TrieAbstract {

    protected final String extendsType;
    protected final String displayName;
    protected final String fullyQualifiedName;

    public TrieDefault(String fullyQualifiedName) {
        this(fullyQualifiedName, null, null);
    }

    public TrieDefault(String fullyQualifiedName, String extendsType, String displayName) {
        super();
        checkNotNull(fullyQualifiedName, "fullyQualifiedName");
        this.displayName = displayName;
        this.fullyQualifiedName = fullyQualifiedName;
        this.extendsType = isBlank(extendsType) ? Object.class.getName() : extendsType;
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        if (MessageAttributes.class.getName().equals(extendsType)) {
            // We keep the message attributes type simple name to avoid confusion and always display 'MessageAttributes' type.
            return MessageAttributes.class.getSimpleName();
        } else {
            // In any other case
            return toSimpleName();
        }
    }

    @Override
    public Collection<Suggestion> autocomplete(String token, TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> autocomplete = super.autocomplete(token, typeAndTrieMap);
        // Object is the supertype of all types, therefore we don't look
        // for extends suggestions for object types, otherwise we add suggestions,
        // from the supertype.
        if (!Object.class.getName().equals(fullyQualifiedName) && isNotBlank(extendsType)) {
            Trie currentTypeTrie = typeAndTrieMap.getOrDefault(extendsType);
            Collection<Suggestion> extendsSuggestions = currentTypeTrie.autocomplete(token, typeAndTrieMap);
            autocomplete.addAll(extendsSuggestions);
        }

        return autocomplete;
    }

    protected String toSimpleName() {
        if (isNotBlank(displayName)) {
            return displayName;
        } else {
            String[] splits = fullyQualifiedName.split(","); // might be multiple types
            List<String> tmp = new ArrayList<>();
            for (String split : splits) {
                String[] segments = split.split("\\.");
                tmp.add(segments[segments.length - 1]);
            }
            return String.join(",", tmp);
        }
    }
}
