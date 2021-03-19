package de.codecentric.reedelk.plugin.editor.designer.action.add;

import de.codecentric.reedelk.plugin.editor.designer.action.Action;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowGraphChangeAware;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;

public class AddActionHandler {

    private final FlowSnapshot snapshot;
    private final Action addAction;

    public AddActionHandler(@NotNull FlowSnapshot snapshot,
                            @NotNull Action addAction) {
        this.addAction = addAction;
        this.snapshot = snapshot;
    }

    public boolean handle() {

        FlowGraph original = snapshot.getGraphOrThrowIfAbsent();

        FlowGraph copy = original.copy();

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        addAction.execute(modifiableGraph);

        if (modifiableGraph.isChanged()) {

            snapshot.updateSnapshot(this, modifiableGraph);

            return true;

        }

        return false;
    }
}
