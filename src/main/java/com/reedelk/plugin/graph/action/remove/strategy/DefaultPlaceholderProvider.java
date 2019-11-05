package com.reedelk.plugin.graph.action.remove.strategy;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import com.reedelk.plugin.graph.node.GraphNodeFactory;
import com.reedelk.runtime.component.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class DefaultPlaceholderProvider implements PlaceholderProvider {

    private final Module module;

    public DefaultPlaceholderProvider(@NotNull Module module) {
        this.module = module;
    }

    @Override
    public Optional<PlaceholderNode> get() {
        PlaceholderNode placeholderNode = GraphNodeFactory.get(module, Placeholder.class.getName());
        return Optional.of(placeholderNode);
    }

    @Override
    public Optional<PlaceholderNode> get(String description) {
        PlaceholderNode placeholderNode = GraphNodeFactory.get(module, Placeholder.class.getName());
        placeholderNode.componentData().set(Implementor.description(), description);
        return Optional.of(placeholderNode);
    }
}
