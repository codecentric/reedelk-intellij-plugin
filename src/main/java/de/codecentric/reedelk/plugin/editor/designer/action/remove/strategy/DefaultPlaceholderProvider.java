package de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy;

import de.codecentric.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.GraphNodeFactory;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.runtime.component.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;

public class DefaultPlaceholderProvider implements PlaceholderProvider {

    private final Module module;

    public DefaultPlaceholderProvider(@NotNull Module module) {
        this.module = module;
    }

    @Override
    public Optional<GraphNode> get() {
        PlaceholderNode placeholderNode = GraphNodeFactory.get(module, Placeholder.class.getName());
        return Optional.of(placeholderNode);
    }

    @Override
    public Optional<GraphNode> get(String description) {
        PlaceholderNode placeholderNode = GraphNodeFactory.get(module, Placeholder.class.getName());
        placeholderNode.componentData().set(Implementor.description(), description);
        return Optional.of(placeholderNode);
    }
}
