package com.reedelk.plugin.service.module.impl.component.completion;

import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.ALL_TYPES;

abstract class AbstractCompletionTest {

    protected static TypeAndTries typeAndTries;

    @BeforeAll
    static void setUpAll() {
        Map<String, Trie> defaultTypesAndTries = new HashMap<>();
        typeAndTries = new TypeAndTries(defaultTypesAndTries);
        ALL_TYPES.forEach(trieProvider -> trieProvider.register(typeAndTries, defaultTypesAndTries));
    }
}
