package com.esb.plugin;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.choice.ChoiceNode;
import com.esb.plugin.component.generic.GenericComponentNode;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractGraphTest {

    private ComponentData cRoot;
    private ComponentData cn1;
    private ComponentData cn2;
    private ComponentData cn3;
    private ComponentData cn4;
    private ComponentData cn5;
    private ComponentData cn6;
    private ComponentData cn7;
    private ComponentData cn8;
    private ComponentData cn9;
    private ComponentData cn10;
    private ComponentData cn11;
    private ComponentData cc1;
    private ComponentData cc2;
    private ComponentData cc3;
    private ComponentData cc4;
    private ComponentData cc5;

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

    protected ScopedGraphNode choice1;
    protected ScopedGraphNode choice2;
    protected ScopedGraphNode choice3;
    protected ScopedGraphNode choice4;
    protected ScopedGraphNode choice5;

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

    private ComponentData createComponent(String name) {
        return new ComponentData(ComponentDescriptor.create()
                .fullyQualifiedName(name)
                .displayName(name)
                .build());
    }


}
