package com.esb.plugin.graph.layout;

import com.esb.plugin.component.Component;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.choice.ChoiceDrawable;
import com.esb.plugin.component.generic.GenericComponentDrawable;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.GraphNode;
import com.esb.plugin.graph.drawable.ScopedDrawable;

public class GraphSamples {

    Component cRoot = createComponent("root");
    Component cn1 = createComponent("n1");
    Component cn2 = createComponent("n2");
    Component cn3 = createComponent("n3");
    Component cn4 = createComponent("n4");
    Component cn5 = createComponent("n5");
    Component cn6 = createComponent("n6");
    Component cn7 = createComponent("n7");
    Component cn8 = createComponent("n8");
    Component cn9 = createComponent("n9");
    Component cn10 = createComponent("n10");
    Component cn11 = createComponent("n11");
    Component cc1 = createComponent("c1");
    Component cc2 = createComponent("c2");
    Component cc3 = createComponent("c3");
    Component cc4 = createComponent("c4");
    Component cc5 = createComponent("c5");

    GraphNode root = new GenericComponentDrawable(cRoot);
    GraphNode n1 = new GenericComponentDrawable(cn1);
    GraphNode n2 = new GenericComponentDrawable(cn2);
    GraphNode n3 = new GenericComponentDrawable(cn3);
    GraphNode n4 = new GenericComponentDrawable(cn4);
    GraphNode n5 = new GenericComponentDrawable(cn5);
    GraphNode n6 = new GenericComponentDrawable(cn6);
    GraphNode n7 = new GenericComponentDrawable(cn7);
    GraphNode n8 = new GenericComponentDrawable(cn8);
    GraphNode n9 = new GenericComponentDrawable(cn9);
    GraphNode n10 = new GenericComponentDrawable(cn10);
    GraphNode n11 = new GenericComponentDrawable(cn11);

    ScopedDrawable c1 = new ChoiceDrawable(cc1);
    ScopedDrawable c2 = new ChoiceDrawable(cc2);
    ScopedDrawable c3 = new ChoiceDrawable(cc3);
    ScopedDrawable c4 = new ChoiceDrawable(cc4);


    public static FlowGraph graph1() {
        return new GraphSamples().buildGraph1();
    }

    public static FlowGraph graph1a() {
        return new GraphSamples().buildGraph1a();
    }

    public static FlowGraph graph1b() {
        return new GraphSamples().buildGraph1b();
    }

    public static FlowGraph graph2() {
        return new GraphSamples().buildGraph2();
    }

    public static FlowGraph graph3() {
        return new GraphSamples().buildGraph3();
    }

    public static FlowGraph graph4() {
        return new GraphSamples().buildGraph4();
    }

    public static FlowGraph graph5() {
        return new GraphSamples().buildGraph5();
    }

    public static FlowGraph graph6() {
        return new GraphSamples().buildGraph6();
    }

    public static FlowGraph graph7() {
        return new GraphSamples().buildGraph7();
    }

    public static FlowGraph graph8() {
        return new GraphSamples().buildGraph8();
    }

    private FlowGraph buildGraph8() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, n1);
        graph.add(n1, c2);
        graph.add(c2, n2);

        c1.addToScope(n1);
        c2.addToScope(n2);

        return graph;
    }

    private FlowGraph buildGraph7() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, c2);
        graph.add(c1, c3);
        graph.add(c2, n1);
        graph.add(c3, n2);

        c1.addToScope(c2);
        c1.addToScope(c3);
        c2.addToScope(n1);
        c3.addToScope(n2);


        return graph;
    }

    private FlowGraph buildGraph6() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, c2);
        graph.add(c1, c3);
        graph.add(c2, n2);
        graph.add(c2, n3);
        graph.add(c3, n4);
        graph.add(c3, n5);

        c1.addToScope(c2);
        c1.addToScope(c3);

        c2.addToScope(n2);
        c2.addToScope(n3);

        c3.addToScope(n4);
        c3.addToScope(n5);

        return graph;
    }

    private FlowGraph buildGraph1() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, n1);
        graph.add(c1, n2);
        graph.add(n1, n3);
        graph.add(n2, n3);
        graph.add(n3, c2);
        graph.add(c2, n4);
        graph.add(c2, n5);
        graph.add(n4, n6);
        graph.add(n5, n6);

        c1.addToScope(n1);
        c1.addToScope(n2);

        c2.addToScope(n4);
        c2.addToScope(n5);
        return graph;
    }

    private FlowGraph buildGraph1a() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, n1);
        graph.add(c1, n2);
        graph.add(n1, n3);
        graph.add(n2, n3);
        graph.add(n3, c2);
        graph.add(c2, n4);
        graph.add(c2, n5);
        graph.add(c2, c3);
        graph.add(n4, n6);
        graph.add(n5, n6);
        graph.add(c3, n7);
        graph.add(n7, n6);

        c1.addToScope(n1);
        c1.addToScope(n2);

        c2.addToScope(n4);
        c2.addToScope(n5);
        c2.addToScope(c3);

        c3.addToScope(n7);
        return graph;
    }

    private FlowGraph buildGraph1b() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, n1);
        graph.add(c1, n2);
        graph.add(c1, c3);
        graph.add(c3, n3);

        graph.add(n1, n4);
        graph.add(n2, n4);
        graph.add(n3, n4);

        c1.addToScope(n1);
        c1.addToScope(n2);
        c1.addToScope(c3);

        c3.addToScope(n3);

        return graph;
    }

    private FlowGraph buildGraph2() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, n1);
        graph.add(c1, n2);
        graph.add(n1, n3);
        graph.add(n2, n3);
        graph.add(n3, c2);
        graph.add(c2, n4);
        graph.add(c2, n5);
        graph.add(c2, n6);
        graph.add(n4, n7);
        graph.add(n5, n7);
        graph.add(n6, n7);

        c1.addToScope(n1);
        c1.addToScope(n2);

        c2.addToScope(n4);
        c2.addToScope(n5);
        c2.addToScope(n6);
        return graph;
    }

    private FlowGraph buildGraph3() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, n1);
        graph.add(c1, c3);
        graph.add(n1, c2);
        graph.add(c2, n4);
        graph.add(c2, n5);
        graph.add(n4, n6);
        graph.add(n5, n6);
        graph.add(n6, n9);
        graph.add(c3, n2);
        graph.add(c3, n3);
        graph.add(n2, n7);
        graph.add(n3, n8);
        graph.add(n8, n9);

        graph.add(n7, c4);
        graph.add(c4, n10);
        graph.add(c4, n11);
        graph.add(n10, n8);
        graph.add(n11, n8);


        c1.addToScope(n1);
        c1.addToScope(c2);
        c1.addToScope(n6);
        c1.addToScope(c3);
        c1.addToScope(n8);

        c2.addToScope(n4);
        c2.addToScope(n5);

        c3.addToScope(n2);
        c3.addToScope(n7);
        c3.addToScope(n3);
        c3.addToScope(c4);

        c4.addToScope(n10);
        c4.addToScope(n11);

        return graph;
    }

    private FlowGraph buildGraph4() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, n1);
        graph.add(c1, n2);
        graph.add(n1, c2);
        graph.add(c2, n3);
        graph.add(c2, n4);

        graph.add(n3, n5);
        graph.add(n4, n5);
        graph.add(n2, n5);

        graph.add(n5, c3);
        graph.add(c3, n6);
        graph.add(c3, n7);
        graph.add(c3, n8);
        graph.add(c3, n9);

        graph.add(n6, n10);
        graph.add(n7, n10);
        graph.add(n8, n10);
        graph.add(n9, n10);

        c1.addToScope(n1);
        c1.addToScope(c2);
        c1.addToScope(n2);

        c2.addToScope(n3);
        c2.addToScope(n4);

        c3.addToScope(n6);
        c3.addToScope(n7);
        c3.addToScope(n8);
        c3.addToScope(n9);

        return graph;
    }

    private FlowGraph buildGraph5() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, n1);
        graph.add(c1, n2);
        graph.add(n1, c2);
        graph.add(c2, n3);
        graph.add(c2, n4);

        graph.add(n3, n5);
        graph.add(n4, n5);
        graph.add(n2, n5);

        graph.add(n5, c3);
        graph.add(c3, n6);
        graph.add(c3, n7);

        graph.add(n6, n10);
        graph.add(n7, n10);

        c1.addToScope(n1);
        c1.addToScope(c2);
        c1.addToScope(n2);

        c2.addToScope(n3);
        c2.addToScope(n4);

        c3.addToScope(n6);
        c3.addToScope(n7);

        return graph;
    }

    private Component createComponent(String name) {
        return new Component(ComponentDescriptor.create()
                .fullyQualifiedName(name)
                .displayName(name)
                .build());
    }
}
