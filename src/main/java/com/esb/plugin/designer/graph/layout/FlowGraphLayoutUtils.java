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

    public static int computeSubTreeHeight(FlowGraph graph, Drawable root, Graphics2D graphics) {
        return computeMaxHeight(graphics, graph, root, Optional.empty(), 0);
    }

    public static int findLayer(Drawable current, java.util.List<java.util.List<Drawable>> layers) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).contains(current)) {
                return i;
            }
        }
        throw new RuntimeException("Could not find layer for node " + current);
    }

    public static int sumLayerWidthForLayersPreceding(int layerNumber, java.util.List<java.util.List<Drawable>> layers, Graphics2D graphics, FlowGraph graph) {
        int sum = 0;
        for (int i = 0; i < layerNumber; i++) {
            sum += maxLayerWidth(i, layers, graphics, graph);
        }
        return sum;
    }

    public static int maxLayerWidth(int layerNumber, java.util.List<java.util.List<Drawable>> layers, Graphics2D graphics, FlowGraph graph) {
        List<Drawable> layerDrawables = layers.get(layerNumber);
        int max = 0;
        for (Drawable layerDrawable : layerDrawables) {
            int nestedScopes = ScopeUtilities.countNumberOfNestedScopes(graph, layerDrawable);
            int total = layerDrawable.width(graphics) + nestedScopes * HORIZONTAL_PADDING;
            if (total > max) max = total;
        }
        return max;
    }

    public static Drawable findCommonParent(FlowGraph graph, Collection<Drawable> drawables) {
        Set<Drawable> commonParents = new HashSet<>();
        drawables.forEach(drawable -> commonParents.addAll(graph.predecessors(drawable)));
        checkState(commonParents.size() == 1, "Common parent must be one");
        return commonParents.stream().findFirst().get();
    }

    public static int computeMaxHeight(Graphics2D graphics, FlowGraph graph, Drawable start, Optional<Drawable> end, int currentMax) {
        if (start instanceof ScopedDrawable) {
            ScopedDrawable scope = (ScopedDrawable) start;
            List<Drawable> successors = graph.successors(scope);
            Optional<Drawable> firstNodeOutsideScope = ScopeUtilities.getFirstNodeOutsideScope(graph, scope);

            int sum = 0;
            for (Drawable successor : successors) {
                sum += computeMaxHeight(graphics, graph, successor, firstNodeOutsideScope, currentMax);
            }

            int subMaxHeight = 0;
            if (firstNodeOutsideScope.isPresent()) {
                subMaxHeight = computeMaxHeight(graphics, graph, firstNodeOutsideScope.get(), end, sum);
            }

            return sum > subMaxHeight ?
                    sum + VERTICAL_PADDING + VERTICAL_PADDING :
                    subMaxHeight + VERTICAL_PADDING + VERTICAL_PADDING;

        } else {

            if (end.isPresent()) {
                if (start == end.get()) {
                    return start.height(graphics);
                }
            }

            int newMax = currentMax > start.height(graphics) ? currentMax : start.height(graphics);
            List<Drawable> successors = graph.successors(start);
            checkState(successors.size() == 1 || successors.isEmpty(),
                    "Only ScopedDrawables might have more than one successor");

            if (successors.isEmpty()) return newMax;

            Drawable successor = successors.get(0);
            return computeMaxHeight(graphics, graph, successor, end, newMax);
        }
    }
}
