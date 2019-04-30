package com.esb.plugin.graph;

import com.esb.plugin.component.Component;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.choice.ChoiceNode;
import com.esb.plugin.component.generic.GenericComponentNode;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedDrawable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractGraphTest {

    private Component cRoot;
    private Component cn1;
    private Component cn2;
    private Component cn3;
    private Component cn4;
    private Component cn5;
    private Component cn6;
    private Component cn7;
    private Component cn8;
    private Component cn9;
    private Component cn10;
    private Component cn11;
    private Component cc1;
    private Component cc2;
    private Component cc3;
    private Component cc4;
    private Component cc5;

    protected GraphNode root;
    protected GraphNode n1;
    protected GraphNode n2;
    protected GraphNode n3;
    protected GraphNode n4;
    protected GraphNode n5;
    protected GraphNode n6;
    protected GraphNode n7;
    protected GraphNode n8;
    protected GraphNode n9;
    protected GraphNode n10;
    protected GraphNode n11;

    protected ScopedDrawable choice1;
    protected ScopedDrawable choice2;
    protected ScopedDrawable choice3;
    protected ScopedDrawable choice4;
    protected ScopedDrawable choice5;

    @BeforeEach
    protected void setUp() {
        cRoot = createComponent("root");
        cn1 = createComponent("n1");
        cn2 = createComponent("n2");
        cn3 = createComponent("n3");
        cn4 = createComponent("n4");
        cn5 = createComponent("n5");
        cn6 = createComponent("n6");
        cn7 = createComponent("n7");
        cn8 = createComponent("n8");
        cn9 = createComponent("n9");
        cn10 = createComponent("n10");
        cn11 = createComponent("n11");
        cc1 = createComponent("c1");
        cc2 = createComponent("c2");
        cc3 = createComponent("c3");
        cc4 = createComponent("c4");
        cc5 = createComponent("c5");

        root = new GenericComponentNode(cRoot);
        n1 = new GenericComponentNode(cn1);
        n2 = new GenericComponentNode(cn2);
        n3 = new GenericComponentNode(cn3);
        n4 = new GenericComponentNode(cn4);
        n5 = new GenericComponentNode(cn5);
        n6 = new GenericComponentNode(cn6);
        n7 = new GenericComponentNode(cn7);
        n8 = new GenericComponentNode(cn8);
        n9 = new GenericComponentNode(cn9);
        n10 = new GenericComponentNode(cn10);
        n11 = new GenericComponentNode(cn11);

        choice1 = new ChoiceNode(cc1);
        choice2 = new ChoiceNode(cc2);
        choice3 = new ChoiceNode(cc3);
        choice4 = new ChoiceNode(cc4);
        choice5 = new ChoiceNode(cc5);
    }

    private Component createComponent(String name) {
        return new Component(ComponentDescriptor.create()
                .fullyQualifiedName(name)
                .displayName(name)
                .build());
    }

    protected void assertThatRootIs(FlowGraph graph, GraphNode root) {
        assertThat(graph.root()).isEqualTo(root);
    }

    protected void assertThatSuccessorsAreExactly(FlowGraph graph, GraphNode target, GraphNode... successors) {
        if (successors.length == 0) {
            assertThat(graph.successors(target)).isEmpty();
        } else {
            assertThat(graph.successors(target)).containsExactly(successors);
        }
    }

}
