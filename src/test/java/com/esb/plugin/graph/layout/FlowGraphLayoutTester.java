package com.esb.plugin.graph.layout;

import com.esb.plugin.graph.FlowGraph;

import javax.swing.*;
import java.awt.*;

import static com.esb.plugin.graph.layout.GraphSamples.*;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class FlowGraphLayoutTester extends JFrame {

    private FlowGraphLayoutTester() {
        new GraphVisualizer(graph1(), "Graph1");
        new GraphVisualizer(graph1a(), "Graph1a");
        new GraphVisualizer(graph1b(), "Graph1b");
        new GraphVisualizer(graph2(), "Graph2");
        new GraphVisualizer(graph3(), "Graph3");
        new GraphVisualizer(graph4(), "Graph4");
        new GraphVisualizer(graph5(), "Graph5");
        new GraphVisualizer(graph6(), "Graph6");
        new GraphVisualizer(graph7(), "Graph7");
        new GraphVisualizer(graph8(), "Graph8");
    }

    class GraphVisualizer extends JFrame {
        GraphVisualizer(FlowGraph graph, String title) {
            setTitle(title);
            setVisible(true);
            setSize(1000, 600);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            add(new GraphPanel(graph));
        }
    }

    class GraphPanel extends JPanel {

        private final FlowGraph graph;

        GraphPanel(FlowGraph graph) {
            this.graph = graph;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            setBackground(Color.WHITE);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

            FlowGraphLayout.compute(graph, g2, 0);
            graph.nodes()
                    .forEach(drawable -> {
                        drawable.draw(graph, g2, this);
                        g2.setColor(Color.DARK_GRAY);
                        g2.drawString(String.format("x:%d,y:%d",
                                drawable.x(),
                                drawable.y()),
                                drawable.x() - 50,
                                drawable.y() - 60);
                    });
        }
    }

    public static void main(String[] args) {
        new FlowGraphLayoutTester();
    }
}
