package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.editor.component.Component;
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
        root = new GenericComponentDrawable(new Component("root"));
        n1 = new GenericComponentDrawable(new Component("n1"));
        n2 = new GenericComponentDrawable(new Component("n2"));
        n3 = new GenericComponentDrawable(new Component("n3"));
        n4 = new GenericComponentDrawable(new Component("n4"));
        n5 = new GenericComponentDrawable(new Component("n5"));
        n6 = new GenericComponentDrawable(new Component("n6"));
        n7 = new GenericComponentDrawable(new Component("n7"));
        n8 = new GenericComponentDrawable(new Component("n8"));
        n9 = new GenericComponentDrawable(new Component("n9"));
        n10 = new GenericComponentDrawable(new Component("n10"));
        n11 = new GenericComponentDrawable(new Component("n11"));

        choice1 = new ChoiceDrawable(new Component("choice1"));
        choice2 = new ChoiceDrawable(new Component("choice2"));
        choice3 = new ChoiceDrawable(new Component("choice3"));
        choice4 = new ChoiceDrawable(new Component("choice4"));
        choice5 = new ChoiceDrawable(new Component("choice5"));
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
