package com.reedelk.plugin.service.module.impl.completion;

import java.util.List;

public class ComponentIO {

    private final List<Suggestion> attributes;

    public ComponentIO(List<Suggestion> attributes) {
        this.attributes = attributes;
    }

    public List<Suggestion> getOutputAttributes() {
        return attributes;
    }
}
