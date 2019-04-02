package com.esb.plugin.designer.graph.dnd;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;

import java.util.function.Predicate;

public class DndPredicates {

    public static Predicate<Drawable> byPrecedingNodesOnX(FlowGraph graph, int dropX) {
        return preceding -> {
            // The drop point is before/after the center of the node or the center + next node position.
            if (dropX <= preceding.x() || dropX >= preceding.x() + Tile.WIDTH + Tile.HALF_WIDTH) {
                return false;
            }
            // If exists a successor of the current preceding preceding in the preceding + 1 position,
            // then we restrict the drop position so that we consider valid if and only if its x
            // coordinates are between preceding x and successor x.
            for (Drawable successor : graph.successors(preceding)) {
                if (successor.x() == preceding.x() + Tile.WIDTH) {
                    return dropX > preceding.x() && dropX < successor.x();
                }
            }
            // The next successor is beyond the next position so we consider valid a drop point
            // between preceding x and until the end of preceding + 1 position
            return true;
        };
    }
}
