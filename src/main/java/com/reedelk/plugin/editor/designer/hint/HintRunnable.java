package com.reedelk.plugin.editor.designer.hint;

import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.utils.FindClosestPrecedingNode;

import java.awt.*;

import static com.reedelk.plugin.editor.designer.hint.HintResult.EMPTY;

/**
 * Created and started when Drop
 */
public class HintRunnable implements Runnable {

    private static final int deltaTime = 10;

    private final Object wait = new Object();
    private final HintMode hintMode;
    private final Graphics2D graphics;
    private final FlowSnapshot snapshot;
    private final HintResultListener listener;

    private Point hintPoint;
    private long lastTime;
    private boolean running = true;

    public static HintRunnable start(FlowSnapshot snapshot, Graphics2D graphics, HintResultListener listener, HintMode hintMode) {
        HintRunnable hintCalculator = new HintRunnable(snapshot, graphics, listener, hintMode);
        new Thread(hintCalculator).start();
        return hintCalculator;
    }

    private HintRunnable(FlowSnapshot snapshot, Graphics2D graphics, HintResultListener listener, HintMode hintMode) {
        this.hintMode = hintMode;
        this.snapshot = snapshot;
        this.listener = listener;
        this.graphics = graphics;
    }

    @Override
    public void run() {
        while (running) {
            try {
                long currentTime = System.currentTimeMillis();
                if (hintPoint != null && currentTime - lastTime > deltaTime) {
                    synchronized (wait) {
                        if (hintPoint != null) {
                            HintResult hint = FindClosestPrecedingNode.of(snapshot.getGraphOrThrowIfAbsent(), hintPoint, graphics)
                                    .map(hintNode -> new HintResult(hintNode, hintPoint, hintMode))
                                    .orElse(EMPTY);
                            listener.onHintResult(hint);
                            hintPoint = null;
                        }
                    }
                }
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
