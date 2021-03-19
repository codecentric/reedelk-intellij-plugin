package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractCompletionTest {

    protected static TypeAndTries typeAndTries;

    @BeforeAll
    static void setUpAll() {
        Map<String, Trie> defaultTypesAndTries = new HashMap<>();
        typeAndTries = new TypeAndTries(defaultTypesAndTries);
        TypeTestUtils.ALL_TYPES.forEach(trieProvider -> trieProvider.register(typeAndTries, defaultTypesAndTries));
    }
}
