package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.ScopeUtilities;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.util.List;
import java.util.*;

import static com.esb.plugin.designer.graph.drawable.ScopedDrawable.HORIZONTAL_PADDING;
import static com.esb.plugin.designer.graph.drawable.ScopedDrawable.VERTICAL_PADDING;
import static com.google.common.base.Preconditions.checkState;

public class FlowGraphLayoutUtils {

    static int computeSubTreeHeight(FlowGraph graph, Graphics2D graphics, Drawable start) {
        return computeSubTreeHeight(graph, graphics, start, null);
    }

    public static int computeSubTreeHeight(FlowGraph graph, Graphics2D graphics, Drawable start, Drawable end) {
        return computeMaxHeight(graphics, graph, start, end, 0);
    }

    static int findContainingLayer(List<List<Drawable>> layers, Drawable current) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).contains(current)) {
                return i;
            }
        }
        throw new RuntimeException("Could not find containing layer for node " + current);
    }

    static int layerWidthSumPreceding(FlowGraph graph, Graphics2D graphics, List<List<Drawable>> layers, int precedingLayerIndex) {
        int sum = 0;
        for (int i = 0; i < precedingLayerIndex; i++) {
            List<Drawable> layerDrawables = layers.get(i);
            sum += maxLayerWidth(graph, graphics, layerDrawables);
        }
        return sum;
    }

    static Drawable findCommonParent(FlowGraph graph, Collection<Drawable> drawables) {
        Set<Drawable> commonParents = new HashSet<>();
        drawables.forEach(drawable -> commonParents.addAll(graph.predecessors(drawable)));
        checkState(commonParents.size() == 1, "Common parent must be one");
        return commonParents.stream().findFirst().get();
    }

    private static int computeMaxHeight(Graphics2D graphics, FlowGraph graph, Drawable start, Drawable end, int currentMax) {

        if (start == end) return currentMax;

        if (start instanceof ScopedDrawable) {
            ScopedDrawable scope = (ScopedDrawable) start;
            List<Drawable> successors = graph.successors(scope);
            Optional<Drawable> optionalFirstNodeOutsideScope = ScopeUtilities.getFirstNodeOutsideScope(graph, scope);
            Drawable firstNodeOutsideScope = optionalFirstNodeOutsideScope.orElse(null);

            int sum = 0;
            for (Drawable successor : successors) {
                // We are looking for the max in the subtree starting from this successor.
                sum += computeMaxHeight(graphics, graph, successor, firstNodeOutsideScope, 0);
            }

            if (successors.isEmpty()) {
                sum += scope.height(graphics);
            }

            int newCurrentMax = sum + VERTICAL_PADDING + VERTICAL_PADDING > currentMax ?
                    sum + VERTICAL_PADDING + VERTICAL_PADDING :
                    currentMax;

            int newOuterMax = 0;
            if (firstNodeOutsideScope != end && firstNodeOutsideScope != null) {
                newOuterMax = computeMaxHeight(graphics, graph, firstNodeOutsideScope, null, 0);
            }

            return newOuterMax > newCurrentMax ? newOuterMax : newCurrentMax;

        } else {

            int newMax = currentMax > start.height(graphics) ? currentMax : start.height(graphics);
            List<Drawable> successors = graph.successors(start);
            checkState(successors.size() == 1 || successors.isEmpty(),
                    "Only ScopedDrawables might have more than one successor");

            if (successors.isEmpty()) {
                return newMax;
            }

            Drawable successor = successors.get(0);
            return computeMaxHeight(graphics, graph, successor, end, newMax);
        }
    }

    private static int maxLayerWidth(FlowGraph graph, Graphics2D graphics, List<Drawable> layerDrawables) {
        int max = 0;
        for (Drawable layerDrawable : layerDrawables) {
            int nestedScopes = ScopeUtilities.countNumberOfNestedScopes(graph, layerDrawable);
            int total = layerDrawable.width(graphics) + nestedScopes * HORIZONTAL_PADDING;
            if (total > max) max = total;
        }
        return max;
    }
}
