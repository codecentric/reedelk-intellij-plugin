package com.reedelk.plugin.graph.action.add;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.awt.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@MockitoSettings(strictness = Strictness.LENIENT)
class FlowActionNodeAddScopeTest extends BaseFlowActionNodeAddTest {

    @Test
    void shouldAddNodeAtTheEndOfScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, componentNode3);
        routerNode1.addToScope(componentNode1);

        root.setPosition(65, 155);
        routerNode1.setPosition(215, 155);
        componentNode1.setPosition(365, 155);
        componentNode3.setPosition(500, 155); // not in routerNode1 node's scope

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(routerNode1);
        mockDefaultNodeHeight(componentNode1);
        mockDefaultNodeHeight(componentNode3);

        Point dropPoint = new Point(419, 147); // componentNode2 is dropped next to componentNode1

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode2, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .nodesCountIs(5)
                .successorsOf(routerNode1).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(componentNode3)
                .and().successorsOf(componentNode3).isEmpty()
                .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode2);
    }

    @Test
    void shouldCorrectlyAddUpperNodeToFirstRouterWithoutConnectingToSecondRouterLastElement() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode2);

        root.setPosition(55, 80);
        routerNode1.setPosition(195, 80);
        componentNode1.setPosition(335, 80);
        routerNode2.setPosition(480, 80);
        componentNode2.setPosition(625, 80);

        // We drop the new node on top of the 'otherwise'
        Point dropPoint = new Point(310, 14);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).areExactly(routerNode1)
                .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode3)
                .and().successorsOf(componentNode1).areExactly(routerNode2)
                .and().successorsOf(routerNode2).areExactly(componentNode2)
                .and().successorsOf(componentNode3).isEmpty()
                .and().successorsOf(componentNode2).isEmpty()
                .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode3, routerNode2)
                .and().node(routerNode2).scopeContainsExactly(componentNode2);
    }

    @Test
    void shouldCorrectlyAddFollowingLowerNodeToFirstRouterWithoutConnectingToSecondRouterLastElement() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode3);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode3, componentNode4);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode3);
        routerNode1.addToScope(componentNode4);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode2);

        root.setPosition(55, 140);
        routerNode1.setPosition(165, 140);
        routerNode2.setPosition(390, 75);
        componentNode1.setPosition(275, 75);
        componentNode2.setPosition(505, 75);
        componentNode3.setPosition(275, 210);
        componentNode4.setPosition(390, 210);

        Point dropPoint = new Point(479, 196);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode5, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode3)
                .and().successorsOf(componentNode3).isOnly(componentNode4)
                .and().successorsOf(componentNode4).isOnly(componentNode5)
                .and().successorsOf(componentNode5).isEmpty()
                .and().successorsOf(componentNode1).isOnly(routerNode2)
                .and().successorsOf(routerNode2).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty()
                .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode3, componentNode4, componentNode5, routerNode2)
                .and().node(routerNode2).scopeContainsExactly(componentNode2);
    }


    @Test
    void shouldCorrectlyAddFirstNodeOutsideNestedScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode3);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode3, componentNode4);
        graph.add(componentNode4, componentNode5);
        graph.add(componentNode5, componentNode6);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode3);
        routerNode1.addToScope(componentNode4);
        routerNode1.addToScope(componentNode5);
        routerNode1.addToScope(componentNode6);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode2);

        root.setPosition(55, 140);
        routerNode1.setPosition(165, 140);
        componentNode1.setPosition(275, 75);
        componentNode3.setPosition(275, 210);
        routerNode2.setPosition(390, 75);
        componentNode2.setPosition(505, 75);
        componentNode4.setPosition(390, 210);
        componentNode5.setPosition(505, 210);
        componentNode6.setPosition(625, 210);

        Point dropPoint = new Point(621, 60);


        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode7, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode3)
                .and().successorsOf(componentNode1).isOnly(routerNode2)
                .and().successorsOf(routerNode2).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(componentNode7)
                .and().successorsOf(componentNode3).isOnly(componentNode4)
                .and().successorsOf(componentNode4).isOnly(componentNode5)
                .and().successorsOf(componentNode5).isOnly(componentNode6)
                .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode3, componentNode4, componentNode5, componentNode6, componentNode7, routerNode2)
                .and().node(routerNode2).scopeContainsExactly(componentNode2);
    }

    @Test
    void shouldAddSecondSuccessorOfNestedRouterCorrectly() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode3);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, componentNode7);
        graph.add(componentNode3, componentNode4);
        graph.add(componentNode4, componentNode5);
        graph.add(componentNode5, componentNode6);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode3);
        routerNode1.addToScope(componentNode4);
        routerNode1.addToScope(componentNode5);
        routerNode1.addToScope(componentNode6);
        routerNode1.addToScope(componentNode7);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode2);

        root.setPosition(55, 140);
        routerNode1.setPosition(165, 140);
        componentNode1.setPosition(275, 75);
        routerNode2.setPosition(390, 75);
        componentNode2.setPosition(505, 75);
        componentNode3.setPosition(275, 210);
        componentNode4.setPosition(390, 210);
        componentNode5.setPosition(505, 210);
        componentNode6.setPosition(625, 210);
        componentNode7.setPosition(625, 75);

        Point dropPoint = new Point(669, 61);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode8, dropPoint);

        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode3)
                .and().successorsOf(componentNode1).isOnly(routerNode2)
                .and().successorsOf(routerNode2).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(componentNode7)
                .and().successorsOf(componentNode7).isOnly(componentNode8)
                .and().successorsOf(componentNode3).isOnly(componentNode4)
                .and().successorsOf(componentNode4).isOnly(componentNode5)
                .and().successorsOf(componentNode5).isOnly(componentNode6)
                .and().node(routerNode2).scopeContainsExactly(componentNode2)
                .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode3, componentNode4, componentNode5, componentNode6, componentNode7, componentNode8, routerNode2);
    }

    @Test
    void shouldAddNodeOutsideScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        root.setPosition(55, 70);
        routerNode1.setPosition(165, 70);
        componentNode1.setPosition(275, 70);
        routerNode1.addToScope(componentNode1);

        Point dropPoint = new Point(386, 52);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode2, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty()
                .and().node(routerNode1).scopeContainsExactly(componentNode1);
    }

    @Test
    void shouldCorrectlyAddTwoNodesAfterScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, componentNode2);
        routerNode1.addToScope(componentNode1);

        root.setPosition(55, 70);
        routerNode1.setPosition(165, 70);
        componentNode1.setPosition(275, 70);
        componentNode2.setPosition(390, 70);

        Point dropPoint = new Point(478, 45);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(componentNode3)
                .and().successorsOf(componentNode3).isEmpty()
                .and().node(routerNode1).scopeContainsExactly(componentNode1);
    }

    @Test
    void shouldAddNodeRightAfterScopeWhenItContainsTwoNodes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root); // REST Listener
        graph.add(root, routerNode1); // Router1
        graph.add(routerNode1, routerNode2); // Router2
        graph.add(routerNode2, componentNode1); // Component1
        graph.add(routerNode2, componentNode2); // Component2

        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode1);
        routerNode2.addToScope(componentNode2);

        root.setPosition(65, 200);
        routerNode1.setPosition(215, 200);
        routerNode2.setPosition(385, 200);
        componentNode1.setPosition(535, 160);
        componentNode2.setPosition(535, 270);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(routerNode1);
        mockDefaultNodeHeight(routerNode2);
        mockDefaultNodeHeight(componentNode1);
        mockDefaultNodeHeight(componentNode2);

        Point dropPoint = new Point(609, 193);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).isOnly(routerNode2)
                .and().successorsOf(routerNode2).areExactly(componentNode1, componentNode2)
                .and().successorsOf(componentNode1).isOnly(componentNode3)
                .and().successorsOf(componentNode2).isOnly(componentNode3)
                .and().successorsOf(componentNode3).isEmpty()
                .and().node(routerNode1).scopeContainsExactly(routerNode2, componentNode3)
                .and().node(routerNode2).scopeContainsExactly(componentNode1, componentNode2);
    }

    @Test
    void shouldAddNodeRightAfterScopeWhenTopContainsTwoSuccessorsAndBottomJustOneAndDropPointIsAtTheBottom() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode1);
        graph.add(forkNode1, componentNode3);
        graph.add(componentNode1, componentNode2);

        forkNode1.addToScope(componentNode1);
        forkNode1.addToScope(componentNode2);
        forkNode1.addToScope(componentNode3);

        root.setPosition(65, 195);
        forkNode1.setPosition(195, 195);
        componentNode1.setPosition(325, 155);
        componentNode2.setPosition(455, 155);
        componentNode3.setPosition(325, 265);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(forkNode1);
        mockDefaultNodeHeight(componentNode1);
        mockDefaultNodeHeight(componentNode2);
        mockDefaultNodeHeight(componentNode3);

        Point dropPoint = new Point(549, 272);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode4, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(forkNode1)
                .and().successorsOf(forkNode1).areExactly(componentNode1, componentNode3)
                .and().successorsOf(componentNode1).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(componentNode4)
                .and().successorsOf(componentNode3).isOnly(componentNode4)
                .and().successorsOf(componentNode4).isEmpty()
                .and().node(forkNode1).scopeContainsExactly(componentNode1, componentNode2, componentNode3);
    }

    @Test
    void shouldAddNodeInsideScopeWhenUpperNodeIsOutsideNestedScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root); // REST Listener
        graph.add(root, forkNode1); // Fork1
        graph.add(forkNode1, componentNode1); // Component1
        graph.add(forkNode1, forkNode2); // Fork2
        graph.add(forkNode2, componentNode2); // Component2
        graph.add(forkNode2, componentNode3); // Component3

        forkNode1.addToScope(componentNode1);
        forkNode1.addToScope(forkNode2);

        forkNode2.addToScope(componentNode2);
        forkNode2.addToScope(componentNode3);

        root.setPosition(65, 255);
        forkNode1.setPosition(195, 255);
        componentNode1.setPosition(325, 155);
        forkNode2.setPosition(325, 310);
        componentNode2.setPosition(455, 270);
        componentNode3.setPosition(455, 380);

        doReturn(new ScopeBoundaries(260, 200, 265, 220))
                .when(forkNode2).getScopeBoundaries(any(), any());

        Point dropPoint = new Point(421, 211);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode4, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(forkNode1)
                .and().successorsOf(forkNode1).areExactly(componentNode1, forkNode2)
                .and().successorsOf(componentNode1).isEmpty()
                .and().successorsOf(forkNode2).areExactly(componentNode2, componentNode3, componentNode4)
                .and().node(forkNode2).scopeContainsExactly(componentNode2, componentNode3, componentNode4)
                .and().node(forkNode1).scopeContainsExactly(componentNode1, forkNode2)
                .and().successorsOf(componentNode2).isEmpty()
                .and().successorsOf(componentNode3).isEmpty()
                .and().successorsOf(componentNode4).isEmpty();
    }

    @Test
    void shouldAddNodeBetweenPredecessorOutsideScopeAndSuccessorInsideScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        routerNode1.addToScope(componentNode1);


        root.setPosition(65, 155);
        routerNode1.setPosition(215, 155);
        componentNode1.setPosition(370, 155);

        Point dropPoint = new Point(142, 127);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode2, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(routerNode1)
                .and().successorsOf(routerNode1).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isEmpty()
                .and().node(routerNode1).scopeContainsExactly(componentNode1);
    }

    @Test
    void shouldAddNodeBetweenSuccessorInScope1AndBeforeScope2() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        routerNode1.addToScope(componentNode1);
        routerNode2.addToScope(componentNode2);


        root.setPosition(65, 160);
        routerNode1.setPosition(215, 160);
        componentNode1.setPosition(365, 160);
        routerNode2.setPosition(515, 160);
        componentNode2.setPosition(665, 160);

        Point dropPoint = new Point(423, 147);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isOnly(componentNode3)
                .and().successorsOf(componentNode3).isOnly(routerNode2)
                .and().successorsOf(routerNode2).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty()
                .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode3)
                .and().node(routerNode2).scopeContainsExactly(componentNode2);
    }

    @Test
    void shouldAddNodeBetweenPredecessorInScopeAndSuccessorOutsideScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, componentNode2);

        root.setPosition(65, 155);
        routerNode1.setPosition(215, 155);
        componentNode1.setPosition(370, 155);
        componentNode2.setPosition(505, 155);

        routerNode1.addToScope(componentNode1);

        Point dropPoint = new Point(454, 132);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isOnly(componentNode3)
                .and().successorsOf(componentNode3).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty()
                .and().node(routerNode1).scopeContainsExactly(componentNode1);
    }

    @Test
    void shouldAddNodeBetweenPredecessorInMultipleScopeAndSuccessorOutsideScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode2);

        root.setPosition(55, 75);
        routerNode1.setPosition(165, 75);
        componentNode1.setPosition(275, 75);
        routerNode2.setPosition(390, 75);
        componentNode2.setPosition(505, 75);
        componentNode3.setPosition(625, 75);

        Point dropPoint = new Point(583, 61);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode4, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isOnly(routerNode2)
                .and().successorsOf(routerNode2).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(componentNode4)
                .and().successorsOf(componentNode4).isOnly(componentNode3)
                .and().node(routerNode1).scopeContainsExactly(componentNode1, routerNode2)
                .and().node(routerNode2).scopeContainsExactly(componentNode2);
    }

    @Test
    void shouldAddNodeAfterNestedForkScopedNodeInsideForkNodeScope1() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode2, componentNode1);
        graph.add(forkNode2, forkNode3);
        graph.add(componentNode1, componentNode2);
        graph.add(forkNode3, componentNode2);

        forkNode1.addToScope(forkNode2);
        forkNode2.addToScope(componentNode1);
        forkNode2.addToScope(forkNode3);

        root.setPosition(65, 220);
        forkNode1.setPosition(195, 220);
        forkNode2.setPosition(325, 220);
        componentNode1.setPosition(455, 160);
        forkNode3.setPosition(455, 275);
        componentNode2.setPosition(600, 220);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(forkNode1);
        mockDefaultNodeHeight(forkNode2);
        mockDefaultNodeHeight(forkNode3);
        mockDefaultNodeHeight(componentNode1);
        mockDefaultNodeHeight(componentNode2);

        // When we drop the node between fork 2 and fork 1 node scope
        Point dropPoint = new Point(533, 302);
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(forkNode1)
                .and().successorsOf(forkNode1).isOnly(forkNode2)
                .and().node(forkNode1).scopeContainsExactly(forkNode2, componentNode3)
                .and().successorsOf(forkNode2).areExactly(componentNode1, forkNode3)
                .and().node(forkNode2).scopeContainsExactly(componentNode1, forkNode3)
                .and().successorsOf(forkNode3).isOnly(componentNode3)
                .and().successorsOf(componentNode1).isOnly(componentNode3)
                .and().node(forkNode3).scopeIsEmpty()
                .and().successorsOf(componentNode3).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty();
    }

    @Test
    void shouldAddNodeAfterNestedForkScopedNodeInsideForkNodeScope2() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode2, componentNode1);
        graph.add(forkNode2, forkNode3);
        graph.add(componentNode1, componentNode2);
        graph.add(forkNode3, componentNode2);

        forkNode1.addToScope(forkNode2);
        forkNode2.addToScope(componentNode1);
        forkNode2.addToScope(forkNode3);

        root.setPosition(65, 220);
        forkNode1.setPosition(195, 220);
        forkNode2.setPosition(325, 220);
        componentNode1.setPosition(455, 160);
        forkNode3.setPosition(455, 275);
        componentNode2.setPosition(600, 220);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(forkNode1);
        mockDefaultNodeHeight(forkNode2);
        mockDefaultNodeHeight(forkNode3);
        mockDefaultNodeHeight(componentNode1);
        mockDefaultNodeHeight(componentNode2);


        // When we drop the node at the end of fork 2 node scope
        Point dropPoint = new Point(527, 329);
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(forkNode1)
                .and().successorsOf(forkNode1).isOnly(forkNode2)
                .and().node(forkNode1).scopeContainsExactly(forkNode2)
                .and().successorsOf(forkNode2).areExactly(componentNode1, forkNode3)
                .and().node(forkNode2).scopeContainsExactly(componentNode1, forkNode3, componentNode3)
                .and().successorsOf(forkNode3).isOnly(componentNode3)
                .and().successorsOf(componentNode1).isOnly(componentNode2)
                .and().node(forkNode3).scopeIsEmpty()
                .and().successorsOf(componentNode3).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty();
    }

    @Test
    void shouldAddNodeAfterNestedForkScopedNodeInsideForkNodeScope3() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode2, componentNode1);
        graph.add(forkNode2, forkNode3);
        graph.add(componentNode1, componentNode2);
        graph.add(forkNode3, componentNode2);

        forkNode1.addToScope(forkNode2);
        forkNode2.addToScope(componentNode1);
        forkNode2.addToScope(forkNode3);

        root.setPosition(65, 223);
        forkNode1.setPosition(195, 223);
        forkNode2.setPosition(330, 223);
        componentNode1.setPosition(470, 160);
        forkNode3.setPosition(470, 278);
        componentNode2.setPosition(615, 223);

        // When we drop the node at the end of fork 3 node scope
        Point dropPoint = new Point(529, 282);
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(forkNode1)
                .and().successorsOf(forkNode1).isOnly(forkNode2)
                .and().node(forkNode1).scopeContainsExactly(forkNode2)
                .and().successorsOf(forkNode2).areExactly(componentNode1, forkNode3)
                .and().node(forkNode2).scopeContainsExactly(componentNode1, forkNode3)
                .and().successorsOf(forkNode3).isOnly(componentNode3)
                .and().successorsOf(componentNode1).isOnly(componentNode2)
                .and().node(forkNode3).scopeContainsExactly(componentNode3)
                .and().successorsOf(componentNode3).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty();
    }

    @Test
    void shouldAddNodeAfterNestedForkScopedNodeWhenDroppedOutsideAnyScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode2, componentNode1);
        graph.add(forkNode2, forkNode3);
        graph.add(componentNode1, componentNode2);
        graph.add(forkNode3, componentNode2);

        forkNode1.addToScope(forkNode2);
        forkNode2.addToScope(componentNode1);
        forkNode2.addToScope(forkNode3);

        root.setPosition(65, 223);
        forkNode1.setPosition(195, 223);
        forkNode2.setPosition(330, 223);
        componentNode1.setPosition(470, 160);
        forkNode3.setPosition(470, 278);
        componentNode2.setPosition(615, 223);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(forkNode1);
        mockDefaultNodeHeight(forkNode2);
        mockDefaultNodeHeight(forkNode3);
        mockDefaultNodeHeight(componentNode1);
        mockDefaultNodeHeight(componentNode2);

        // When we drop the node outside any scope
        Point dropPoint = new Point(566, 185);
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(forkNode1)
                .and().successorsOf(forkNode1).isOnly(forkNode2)
                .and().node(forkNode1).scopeContainsExactly(forkNode2)
                .and().successorsOf(forkNode2).areExactly(componentNode1, forkNode3)
                .and().node(forkNode2).scopeContainsExactly(componentNode1, forkNode3)
                .and().successorsOf(forkNode3).isOnly(componentNode3)
                .and().successorsOf(componentNode1).isOnly(componentNode3)
                .and().node(forkNode3).scopeIsEmpty()
                .and().successorsOf(componentNode3).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty();
    }

    @Test
    void shouldNotAddNodeAtTheBottomWhenDropPointDoesNotFallBetweenAnyScopeTopBottomBoundaries() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode1, componentNode1);
        graph.add(forkNode2, componentNode2);

        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(componentNode1);
        forkNode2.addToScope(componentNode2);

        root.setPosition(65, 215);
        forkNode1.setPosition(195, 215);
        forkNode2.setPosition(325, 160);
        componentNode1.setPosition(325, 305);
        componentNode2.setPosition(455, 160);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(forkNode1);
        mockDefaultNodeHeight(forkNode2);
        mockDefaultNodeHeight(componentNode1);
        mockDefaultNodeHeight(componentNode2);

        // When we drop the node outside any scope
        Point dropPoint = new Point(523, 297);

        FlowGraphChangeAware changeAwareGraph = new FlowGraphChangeAware(graph);
        FlowGraph updatedGraph = addDrawableToGraph(changeAwareGraph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(changeAwareGraph)
                .isNotChanged()
                .nodesCountIs(5)
                .root().is(root)
                .and().successorsOf(root).isOnly(forkNode1)
                .and().successorsOf(forkNode1).areExactly(forkNode2, componentNode1)
                .and().node(forkNode1).scopeContainsExactly(forkNode2, componentNode1)
                .and().successorsOf(forkNode2).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty();
    }

    @Test
    void shouldAddNodeRightOutsideTheScopesWhenDroppedAtTheBottom() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode1, componentNode1);
        graph.add(forkNode2, componentNode2);

        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(componentNode1);
        forkNode2.addToScope(componentNode2);

        root.setPosition(65, 215);
        forkNode1.setPosition(195, 215);
        forkNode2.setPosition(325, 160);
        componentNode1.setPosition(325, 305);
        componentNode2.setPosition(455, 160);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(forkNode1);
        mockDefaultNodeHeight(forkNode2);
        mockDefaultNodeHeight(componentNode1);
        mockDefaultNodeHeight(componentNode2);

        // When we drop the node outside any scope
        Point dropPoint = new Point(556, 293);

        FlowGraphChangeAware changeAwareGraph = new FlowGraphChangeAware(graph);
        addDrawableToGraph(changeAwareGraph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(changeAwareGraph)
                .nodesCountIs(6)
                .root().is(root)
                .and().successorsOf(root).isOnly(forkNode1)
                .and().successorsOf(forkNode1).areExactly(forkNode2, componentNode1)
                .and().node(forkNode1).scopeContainsExactly(forkNode2, componentNode1)
                .and().successorsOf(forkNode2).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(componentNode3);
    }

    @Test
    void shouldAddNodeBetweenPredecessorInMultipleScopeAndDropPointInsideUpperScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);

        root.setPosition(65, 160);
        routerNode1.setPosition(215, 160);
        componentNode1.setPosition(365, 160);
        routerNode2.setPosition(515, 160);
        componentNode2.setPosition(665, 160);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode2);

        Point dropPoint = new Point(739, 154);

        // When
        FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

        // Then
        PluginAssertion.assertThat(updatedGraph)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isOnly(routerNode2)
                .and().successorsOf(routerNode2).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(componentNode3)
                .and().node(routerNode1).scopeContainsExactly(componentNode1, routerNode2, componentNode3)
                .and().node(routerNode2).scopeContainsExactly(componentNode2);
    }

    @Test
    void shouldCorrectlyAddNodeWhenDroppedAboveTheLastNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode1);
        graph.add(forkNode1, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        root.setPosition(65, 195);
        forkNode1.setPosition(195, 195);
        componentNode1.setPosition(325, 155);
        componentNode2.setPosition(325, 265);
        componentNode3.setPosition(460, 195);

        // Drop it right after componentNode3
        Point dropPoint = new Point(505, 132);

        // When
        FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, componentNode4, dropPoint);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(6)
                .root().is(root)
                .and().successorsOf(root).isOnly(forkNode1)
                .and().successorsOf(forkNode1).areExactly(componentNode1, componentNode2)
                .and().successorsOf(componentNode1).isOnly(componentNode3)
                .and().successorsOf(componentNode2).isOnly(componentNode3)
                .and().successorsOf(componentNode3).isOnly(componentNode4)
                .and().successorsOf(componentNode4).isEmpty();
    }

    @Test
    void shouldNotAddNodeWhenDroppedBelowTheNodeHeight() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        root.setPosition(65, 150);
        componentNode1.setPosition(195, 150);

        Point dropPoint = new Point(268, 201);

        // When
        FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, componentNode2, dropPoint);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isNotChanged()
                .nodesCountIs(2)
                .root().is(root)
                .and().successorsOf(root).isOnly(componentNode1);
    }
}
