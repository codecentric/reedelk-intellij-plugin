package de.codecentric.reedelk.plugin.editor.designer.action.remove;

import de.codecentric.reedelk.plugin.editor.designer.action.Action;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowGraphChangeAware;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;

public class RemoveActionHandler {

    private final FlowSnapshot snapshot;
    private final Action removeAction;

    public RemoveActionHandler(@NotNull FlowSnapshot snapshot,
                               @NotNull Action removeAction) {
        this.removeAction = removeAction;
        this.snapshot = snapshot;
    }

    public void handle() {

        FlowGraph original = snapshot.getGraphOrThrowIfAbsent();

        FlowGraph copy = original.copy();

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        removeAction.execute(modifiableGraph);

        if (modifiableGraph.isChanged()) {

            snapshot.updateSnapshot(this, modifiableGraph);

        }
    }
}
