package com.reedelk.plugin.assertion.component;

import com.reedelk.plugin.component.domain.ComponentDataHolder;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentDataHolderAssertion {

    private final ComponentDataHolder dataHolder;

    public ComponentDataHolderAssertion(ComponentDataHolder dataHolder) {
        this.dataHolder = dataHolder;
    }

    public ComponentDataHolderAssertion containsExactly(String... keys) {
        assertThat(dataHolder.keys()).containsExactlyInAnyOrder(keys);
        return this;
    }

    public ComponentDataHolderAssertion hasKeyWithValue(String key, Object expectedValue) {
        Object actualValue = dataHolder.get(key);
        assertThat(actualValue).isEqualTo(expectedValue);
        return this;
    }

    public ComponentDataHolderAssertion isEmpty() {
        assertThat(dataHolder.keys()).isEmpty();
        return this;
    }
}
