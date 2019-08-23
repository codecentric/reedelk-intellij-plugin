package com.reedelk.plugin.assertion.commons;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MapAssertion {

    private final Map<String, ?> map;

    public MapAssertion(Map<String, ?> map) {
        this.map = map;
    }

    public MapAssertion isEmpty() {
        assertThat(map).isEmpty();
        return this;
    }

    public MapAssertion isNull() {
        assertThat(map).isNull();
        return this;
    }

    public MapAssertion hasKeyWithValue(String key, Object expectedValue) {
        assertThat(map).containsKey(key);
        Object actualValue = map.get(key);
        assertThat(actualValue).isEqualTo(expectedValue);
        return this;
    }

    public MapAssertion containsOnlyKeys(String... keys) {
        assertThat(map).containsOnlyKeys(keys);
        return this;
    }
}
