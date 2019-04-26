package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractGraphTest {

    private ComponentDescriptor cRoot;
    private ComponentDescriptor cn1;
    private ComponentDescriptor cn2;
    private ComponentDescriptor cn3;
    private ComponentDescriptor cn4;
    private ComponentDescriptor cn5;
    private ComponentDescriptor cn6;
    private ComponentDescriptor cn7;
    private ComponentDescriptor cn8;
    private ComponentDescriptor cn9;
    private ComponentDescriptor cn10;
    private ComponentDescriptor cn11;
    private ComponentDescriptor cc1;
    private ComponentDescriptor cc2;
    private ComponentDescriptor cc3;
    private ComponentDescriptor cc4;
    private ComponentDescriptor cc5;

    protected Drawable root;
    protected Drawable n1;
    protected Drawable n2;
    protected Drawable n3;
    protected Drawable n4;
    protected Drawable n5;
    protected Drawable n6;
    protected Drawable n7;
    protected Drawable n8;
    protected Drawable n9;
    protected Drawable n10;
    protected Drawable n11;

    protected ScopedDrawable choice1;
    protected ScopedDrawable choice2;
    protected ScopedDrawable choice3;
    protected ScopedDrawable choice4;
    protected ScopedDrawable choice5;

    @BeforeEach
    protected void setUp() {
        cRoot = ComponentDescriptor.create().fullyQualifiedName("root").displayName("root").build();
        cn1 = ComponentDescriptor.create().fullyQualifiedName("n1").displayName("n1").build();
        cn2 = ComponentDescriptor.create().fullyQualifiedName("n2").displayName("n2").build();
        cn3 = ComponentDescriptor.create().fullyQualifiedName("n3").displayName("n3").build();
        cn4 = ComponentDescriptor.create().fullyQualifiedName("n4").displayName("n4").build();
        cn5 = ComponentDescriptor.create().fullyQualifiedName("n5").displayName("n5").build();
        cn6 = ComponentDescriptor.create().fullyQualifiedName("n6").displayName("n6").build();
        cn7 = ComponentDescriptor.create().fullyQualifiedName("n7").displayName("n7").build();
        cn8 = ComponentDescriptor.create().fullyQualifiedName("n8").displayName("n8").build();
        cn9 = ComponentDescriptor.create().fullyQualifiedName("n9").displayName("n9").build();
        cn10 = ComponentDescriptor.create().fullyQualifiedName("n10").displayName("n10").build();
        cn11 = ComponentDescriptor.create().fullyQualifiedName("n11").displayName("n11").build();
        cc1 = ComponentDescriptor.create().fullyQualifiedName("c1").displayName("c1").build();
        cc2 = ComponentDescriptor.create().fullyQualifiedName("c2").displayName("c2").build();
        cc3 = ComponentDescriptor.create().fullyQualifiedName("c3").displayName("c3").build();
        cc4 = ComponentDescriptor.create().fullyQualifiedName("c4").displayName("c4").build();
        cc5 = ComponentDescriptor.create().fullyQualifiedName("c5").displayName("c5").build();

        root = new GenericComponentDrawable(cRoot);
        n1 = new GenericComponentDrawable(cn1);
        n2 = new GenericComponentDrawable(cn2);
        n3 = new GenericComponentDrawable(cn3);
        n4 = new GenericComponentDrawable(cn4);
        n5 = new GenericComponentDrawable(cn5);
        n6 = new GenericComponentDrawable(cn6);
        n7 = new GenericComponentDrawable(cn7);
        n8 = new GenericComponentDrawable(cn8);
        n9 = new GenericComponentDrawable(cn9);
        n10 = new GenericComponentDrawable(cn10);
        n11 = new GenericComponentDrawable(cn11);

        choice1 = new ChoiceDrawable(cc1);
        choice2 = new ChoiceDrawable(cc2);
        choice3 = new ChoiceDrawable(cc3);
        choice4 = new ChoiceDrawable(cc4);
        choice5 = new ChoiceDrawable(cc5);
    }

    protected void assertThatRootIs(FlowGraph graph, Drawable root) {
        assertThat(graph.root()).isEqualTo(root);
    }

    protected void assertThatSuccessorsAreExactly(FlowGraph graph, Drawable target, Drawable... successors) {
        if (successors.length == 0) {
            assertThat(graph.successors(target)).isEmpty();
        } else {
            assertThat(graph.successors(target)).containsExactly(successors);
        }
    }

}
