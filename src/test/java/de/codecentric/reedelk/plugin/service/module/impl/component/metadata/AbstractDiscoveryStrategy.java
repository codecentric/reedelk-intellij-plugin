package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.service.module.impl.component.completion.Trie;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.ALL_TYPES;

public class AbstractDiscoveryStrategy {

    protected static TypeAndTries typeAndTries;

    @BeforeAll
    static void setUpAll() {
        Map<String, Trie> defaultTypesAndTries = new HashMap<>();
        typeAndTries = new TypeAndTries(defaultTypesAndTries);
        ALL_TYPES.forEach(trieProvider -> trieProvider.register(typeAndTries, defaultTypesAndTries));
    }
}
