package com.reedelk.plugin.editor.designer.hint;

import com.reedelk.plugin.commons.LiesBetweenTopAndBottom;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.utils.FindClosestPrecedingNode;

import java.awt.*;

import static com.reedelk.plugin.editor.designer.hint.HintResult.EMPTY;

/**
 * Created and started when Drop
 */
public class HintRunnable implements Runnable {

    private static final int WAIT_TIME = 5;
    private static final int DELTA_ACTIVATION_TIME = 10;

    private final Object wait = new Object();
    private final Graphics2D graphics;
    private final FlowSnapshot snapshot;
    private final HintResultListener listener;

    private Point hintPoint;
    private long lastTime;
    private boolean running = true;

    public static HintRunnable start(FlowSnapshot snapshot, Graphics2D graphics, HintResultListener listener) {
        HintRunnable hintCalculator = new HintRunnable(snapshot, graphics, listener);
        new Thread(hintCalculator).start();
        return hintCalculator;
    }

    private HintRunnable(FlowSnapshot snapshot, Graphics2D graphics, HintResultListener listener) {
        this.snapshot = snapshot;
        this.listener = listener;
        this.graphics = graphics;
    }

    @Override
    public void run() {
        while (running) {

            try {

                long currentTime = System.currentTimeMillis();

                if (hintPoint != null && currentTime - lastTime > DELTA_ACTIVATION_TIME) {

                    synchronized (wait) {

                        if (hintPoint == null) continue;

                        FlowGraph graph = snapshot.getGraphOrThrowIfAbsent();
                        GraphNode root = graph.root();

                        HintResult hint = FindClosestPrecedingNode.of(graph, hintPoint, graphics)
                                .map(preceding -> preceding == root ?
                                        HintResult.ROOT : HintResult.from(preceding, hintPoint))
                                .orElseGet(() -> root != null &&
                                        hintPoint.x <= root.x() &&
                                        LiesBetweenTopAndBottom.of(root, hintPoint.y, graphics) ?
                                        HintResult.ROOT : EMPTY);

                        listener.onHintResult(hint);
                        hintPoint = null;
                    }
                }

                Thread.sleep(WAIT_TIME);

            } catch (InterruptedException e) {
                running = false;
            }

        }

        listener.onHintResult(EMPTY);
    }

    public void point(Point hintPoint) {
        synchronized (wait) {
            this.hintPoint = hintPoint;
            this.lastTime = System.currentTimeMillis();
        }
    }

    public void stop() {
        running = false;
    }
}
