package com.reedelk.plugin.editor.designer.action.remove.strategy;

import com.reedelk.plugin.component.type.placeholder.PlaceholderNode;

import java.util.Optional;

public class AbsentPlaceholderProvider implements PlaceholderProvider {

    @Override
    public Optional<PlaceholderNode> get() {
        return Optional.empty();
    }

    @Override
    public Optional<PlaceholderNode> get(String description) {
        return Optional.empty();
    }
}
