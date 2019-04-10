package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.util.Collection;

public class ChoiceConnector implements Connector {

    private final FlowGraph graph;
    private final FlowGraph scopeGraph;

    public ChoiceConnector(final FlowGraph graph, final FlowGraph scopeGraph) {
        this.graph = graph;
        this.scopeGraph = scopeGraph;
        // Need to traverse the scope graph and copy it into graph
        this.graph.add(scopeGraph.root());
        this.graph.add(scopeGraph.root(), scopeGraph.successors(scopeGraph.root()).get(0));
    }

    @Override
    public void addSuccessor(Drawable successor) {
        Collection<Drawable> drawables = ScopeUtilities
                .listLastDrawablesOfScope(graph, (ScopedDrawable) scopeGraph.root());
        drawables.forEach(drawable -> graph.add(drawable, successor));
    }

    @Override
    public void addPredecessor(Drawable predecessor) {
        graph.add(predecessor, scopeGraph.root());
    }

    @Override
    public void add() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void root() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addToScope(ScopedDrawable scope) {
        scope.addToScope(scopeGraph.root());
    }

    @Override
    public void addPredecessor(ScopedDrawable predecessor, int index) {
        graph.add(predecessor, scopeGraph.root(), index);
    }
}
