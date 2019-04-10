package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class AbstractGraphTest {

    protected Drawable root;
    protected Drawable n1;
    protected Drawable n2;
    protected Drawable n3;

    protected ScopedDrawable choice1;
    protected ScopedDrawable choice2;
    protected ScopedDrawable choice3;

    @BeforeEach
    void setUp() {
        root = new GenericComponentDrawable(new Component("root"));
        n1 = new GenericComponentDrawable(new Component("n1"));
        n2 = new GenericComponentDrawable(new Component("n2"));
        n3 = new GenericComponentDrawable(new Component("n3"));
        choice1 = new ChoiceDrawable(new Component("choice1"));
        choice2 = new ChoiceDrawable(new Component("choice2"));
        choice3 = new ChoiceDrawable(new Component("choice3"));
    }

}
