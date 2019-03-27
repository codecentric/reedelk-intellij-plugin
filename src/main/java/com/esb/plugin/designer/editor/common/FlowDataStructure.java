package com.esb.plugin.designer.editor.common;

import com.esb.plugin.graph.handler.Drawable;
import com.google.common.base.Preconditions;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.awt.*;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public class FlowDataStructure {

    private MutableGraph<Drawable> flowGraph;
    private Drawable root;

    private FlowDataStructureListener listener;

    public FlowDataStructure() {
        flowGraph = GraphBuilder.directed().build();
    }

    public void setListener(FlowDataStructureListener listener) {
        this.listener = listener;
    }

    public void add(Drawable before, Drawable current) {
        checkArgument(current != null, "Current node must not be null");
        if (before == null) {
            // Add Root (it is the only node without a predecessor)
            root = current;
            flowGraph.addNode(root);
        } else {
            Set<Drawable> successors = flowGraph.successors(before);
            Iterator<Drawable> it = successors.iterator();


            // TODO: this one throws concurrent modification exception
            // Connect before to current and current to all adjacent nodes
            while (it.hasNext()) {
                Drawable successorNode = it.next();
                flowGraph.removeEdge(before, successorNode);
                flowGraph.putEdge(current, successorNode);
            }
            // Finally we add connection between before and current
            flowGraph.putEdge(before, current);
        }
    }

    public void notifyChange() {
        if (listener != null) {
            listener.onChange();
        }
    }

    public void computeNodesPositions() {
        if (root != null) {
            ComputeGraphNodesPosition.compute(flowGraph, root);
        }
    }

    public void draw(Graphics graphics) {
        flowGraph.nodes().forEach(drawable -> drawable.draw(graphics, null));
    }

    public Drawable closestParentOf(Drawable drawableComponent) {
        if (root == null) return null;
        return findClosestParentOf(root, drawableComponent);
    }

    private Drawable findClosestParentOf(Drawable parent, Drawable target) {
        Preconditions.checkArgument(parent != null, "parent drawable");
        Drawable closest = parent;
        Distance min = distance(parent, target);

        Set<Drawable> successors = flowGraph.successors(parent);
        for (Drawable successor : successors) {
            Drawable current = findClosestParentOf(successor, target);
            Distance currentDistance = distance(current, target);
            if (currentDistance.onRightSide() && min.greaterThan(currentDistance)) {
                min = currentDistance;
                closest = current;
            }
        }
        return closest;
    }

    private Distance distance(Drawable n1, Drawable n2) {
        int deltaX = n2.getPosition().x - n1.getPosition().x;
        int deltaY = n2.getPosition().y - n1.getPosition().y;
        return new Distance(deltaX, deltaY);
    }

    static class Distance {
        int deltaX;
        int deltaY;

        public Distance(int deltaX, int deltaY) {
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }

        public boolean onRightSide() {
            return deltaX >= 0;
        }

        public int get() {
            return deltaX + deltaY;
        }

        public boolean greaterThan(Distance d2) {
            return get() > d2.get();
        }
    }
}
