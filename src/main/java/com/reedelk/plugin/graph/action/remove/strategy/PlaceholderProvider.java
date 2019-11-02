package com.reedelk.plugin.graph.action.remove.strategy;

import com.reedelk.plugin.component.type.placeholder.PlaceholderNode;

import java.util.Optional;

public interface PlaceholderProvider {
    Optional<PlaceholderNode> get();
}
