package com.esb.plugin.graph.layout;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.choice.ChoiceNode;
import com.esb.plugin.component.generic.GenericComponentNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

public class GraphSamples {

    ComponentData cRoot = createComponent("root");
    ComponentData ccomponentNode1 = createComponent("componentNode1");
    ComponentData ccomponentNode2 = createComponent("componentNode2");
    ComponentData ccomponentNode3 = createComponent("componentNode3");
    ComponentData ccomponentNode4 = createComponent("componentNode4");
    ComponentData ccomponentNode5 = createComponent("componentNode5");
    ComponentData ccomponentNode6 = createComponent("componentNode6");
    ComponentData ccomponentNode7 = createComponent("componentNode7");
    ComponentData ccomponentNode8 = createComponent("componentNode8");
    ComponentData ccomponentNode9 = createComponent("componentNode9");
    ComponentData ccomponentNode10 = createComponent("componentNode10");
    ComponentData ccomponentNode11 = createComponent("componentNode11");
    ComponentData cc1 = createComponent("c1");
    ComponentData cc2 = createComponent("c2");
    ComponentData cc3 = createComponent("c3");
    ComponentData cc4 = createComponent("c4");
    ComponentData cc5 = createComponent("c5");

    GraphNode root = new GenericComponentNode(cRoot);
    GraphNode componentNode1 = new GenericComponentNode(ccomponentNode1);
    GraphNode componentNode2 = new GenericComponentNode(ccomponentNode2);
    GraphNode componentNode3 = new GenericComponentNode(ccomponentNode3);
    GraphNode componentNode4 = new GenericComponentNode(ccomponentNode4);
    GraphNode componentNode5 = new GenericComponentNode(ccomponentNode5);
    GraphNode componentNode6 = new GenericComponentNode(ccomponentNode6);
    GraphNode componentNode7 = new GenericComponentNode(ccomponentNode7);
    GraphNode componentNode8 = new GenericComponentNode(ccomponentNode8);
    GraphNode componentNode9 = new GenericComponentNode(ccomponentNode9);
    GraphNode componentNode10 = new GenericComponentNode(ccomponentNode10);
    GraphNode componentNode11 = new GenericComponentNode(ccomponentNode11);

    ScopedGraphNode c1 = new ChoiceNode(cc1);
    ScopedGraphNode c2 = new ChoiceNode(cc2);
    ScopedGraphNode c3 = new ChoiceNode(cc3);
    ScopedGraphNode c4 = new ChoiceNode(cc4);


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
        graph.add(c1, componentNode1);
        graph.add(componentNode1, c2);
        graph.add(c2, componentNode2);

        c1.addToScope(componentNode1);
        c2.addToScope(componentNode2);

        return graph;
    }

    private FlowGraph buildGraph7() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, c2);
        graph.add(c1, c3);
        graph.add(c2, componentNode1);
        graph.add(c3, componentNode2);

        c1.addToScope(c2);
        c1.addToScope(c3);
        c2.addToScope(componentNode1);
        c3.addToScope(componentNode2);


        return graph;
    }

    private FlowGraph buildGraph6() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, c2);
        graph.add(c1, c3);
        graph.add(c2, componentNode2);
        graph.add(c2, componentNode3);
        graph.add(c3, componentNode4);
        graph.add(c3, componentNode5);

        c1.addToScope(c2);
        c1.addToScope(c3);

        c2.addToScope(componentNode2);
        c2.addToScope(componentNode3);

        c3.addToScope(componentNode4);
        c3.addToScope(componentNode5);

        return graph;
    }

    private FlowGraph buildGraph1() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, componentNode1);
        graph.add(c1, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode3, c2);
        graph.add(c2, componentNode4);
        graph.add(c2, componentNode5);
        graph.add(componentNode4, componentNode6);
        graph.add(componentNode5, componentNode6);

        c1.addToScope(componentNode1);
        c1.addToScope(componentNode2);

        c2.addToScope(componentNode4);
        c2.addToScope(componentNode5);
        return graph;
    }

    private FlowGraph buildGraph1a() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, componentNode1);
        graph.add(c1, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode3, c2);
        graph.add(c2, componentNode4);
        graph.add(c2, componentNode5);
        graph.add(c2, c3);
        graph.add(componentNode4, componentNode6);
        graph.add(componentNode5, componentNode6);
        graph.add(c3, componentNode7);
        graph.add(componentNode7, componentNode6);

        c1.addToScope(componentNode1);
        c1.addToScope(componentNode2);

        c2.addToScope(componentNode4);
        c2.addToScope(componentNode5);
        c2.addToScope(c3);

        c3.addToScope(componentNode7);
        return graph;
    }

    private FlowGraph buildGraph1b() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, componentNode1);
        graph.add(c1, componentNode2);
        graph.add(c1, c3);
        graph.add(c3, componentNode3);

        graph.add(componentNode1, componentNode4);
        graph.add(componentNode2, componentNode4);
        graph.add(componentNode3, componentNode4);

        c1.addToScope(componentNode1);
        c1.addToScope(componentNode2);
        c1.addToScope(c3);

        c3.addToScope(componentNode3);

        return graph;
    }

    private FlowGraph buildGraph2() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, componentNode1);
        graph.add(c1, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode3, c2);
        graph.add(c2, componentNode4);
        graph.add(c2, componentNode5);
        graph.add(c2, componentNode6);
        graph.add(componentNode4, componentNode7);
        graph.add(componentNode5, componentNode7);
        graph.add(componentNode6, componentNode7);

        c1.addToScope(componentNode1);
        c1.addToScope(componentNode2);

        c2.addToScope(componentNode4);
        c2.addToScope(componentNode5);
        c2.addToScope(componentNode6);
        return graph;
    }

    private FlowGraph buildGraph3() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, componentNode1);
        graph.add(c1, c3);
        graph.add(componentNode1, c2);
        graph.add(c2, componentNode4);
        graph.add(c2, componentNode5);
        graph.add(componentNode4, componentNode6);
        graph.add(componentNode5, componentNode6);
        graph.add(componentNode6, componentNode9);
        graph.add(c3, componentNode2);
        graph.add(c3, componentNode3);
        graph.add(componentNode2, componentNode7);
        graph.add(componentNode3, componentNode8);
        graph.add(componentNode8, componentNode9);

        graph.add(componentNode7, c4);
        graph.add(c4, componentNode10);
        graph.add(c4, componentNode11);
        graph.add(componentNode10, componentNode8);
        graph.add(componentNode11, componentNode8);


        c1.addToScope(componentNode1);
        c1.addToScope(c2);
        c1.addToScope(componentNode6);
        c1.addToScope(c3);
        c1.addToScope(componentNode8);

        c2.addToScope(componentNode4);
        c2.addToScope(componentNode5);

        c3.addToScope(componentNode2);
        c3.addToScope(componentNode7);
        c3.addToScope(componentNode3);
        c3.addToScope(c4);

        c4.addToScope(componentNode10);
        c4.addToScope(componentNode11);

        return graph;
    }

    private FlowGraph buildGraph4() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, componentNode1);
        graph.add(c1, componentNode2);
        graph.add(componentNode1, c2);
        graph.add(c2, componentNode3);
        graph.add(c2, componentNode4);

        graph.add(componentNode3, componentNode5);
        graph.add(componentNode4, componentNode5);
        graph.add(componentNode2, componentNode5);

        graph.add(componentNode5, c3);
        graph.add(c3, componentNode6);
        graph.add(c3, componentNode7);
        graph.add(c3, componentNode8);
        graph.add(c3, componentNode9);

        graph.add(componentNode6, componentNode10);
        graph.add(componentNode7, componentNode10);
        graph.add(componentNode8, componentNode10);
        graph.add(componentNode9, componentNode10);

        c1.addToScope(componentNode1);
        c1.addToScope(c2);
        c1.addToScope(componentNode2);

        c2.addToScope(componentNode3);
        c2.addToScope(componentNode4);

        c3.addToScope(componentNode6);
        c3.addToScope(componentNode7);
        c3.addToScope(componentNode8);
        c3.addToScope(componentNode9);

        return graph;
    }

    private FlowGraph buildGraph5() {
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, c1);
        graph.add(c1, componentNode1);
        graph.add(c1, componentNode2);
        graph.add(componentNode1, c2);
        graph.add(c2, componentNode3);
        graph.add(c2, componentNode4);

        graph.add(componentNode3, componentNode5);
        graph.add(componentNode4, componentNode5);
        graph.add(componentNode2, componentNode5);

        graph.add(componentNode5, c3);
        graph.add(c3, componentNode6);
        graph.add(c3, componentNode7);

        graph.add(componentNode6, componentNode10);
        graph.add(componentNode7, componentNode10);

        c1.addToScope(componentNode1);
        c1.addToScope(c2);
        c1.addToScope(componentNode2);

        c2.addToScope(componentNode3);
        c2.addToScope(componentNode4);

        c3.addToScope(componentNode6);
        c3.addToScope(componentNode7);

        return graph;
    }

    private ComponentData createComponent(String name) {
        return new ComponentData(ComponentDescriptor.create()
                .fullyQualifiedName(name)
                .displayName(name)
                .build());
    }
}
