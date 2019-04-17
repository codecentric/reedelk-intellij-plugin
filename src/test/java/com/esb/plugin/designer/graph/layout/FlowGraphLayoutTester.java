package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.graph.FlowGraph;

import javax.swing.*;
import java.awt.*;

import static com.esb.plugin.designer.graph.layout.GraphSamples.*;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class FlowGraphLayoutTester extends JFrame {


    private FlowGraphLayoutTester() {
        new GraphVisualizer(graph1());
        new GraphVisualizer(graph1a());
        new GraphVisualizer(graph1b());
        new GraphVisualizer(graph2());
        new GraphVisualizer(graph3());
        new GraphVisualizer(graph4());
        new GraphVisualizer(graph5());
        new GraphVisualizer(graph6());
        new GraphVisualizer(graph7());
    }

    class GraphVisualizer extends JFrame {
        GraphVisualizer(FlowGraph graph) {
            setTitle("Graph Layout test");
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

            FlowGraphLayout.compute(graph, g2);
            graph.nodes()
                    .forEach(drawable -> {
                        drawable.draw(graph, g2, this);
                        g2.setColor(Color.DARK_GRAY);
                        g2.drawString(String.format("x:%d,y:%d",
                                drawable.x(),
                                drawable.y()),
                                drawable.x() - 40,
                                drawable.y() - 50);
                    });
        }
    }

    public static void main(String[] args) {
        new FlowGraphLayoutTester();
    }
}
