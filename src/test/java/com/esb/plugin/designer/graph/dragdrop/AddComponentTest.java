package com.esb.plugin.designer.graph.dragdrop;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.DrawableFactory;
import org.junit.jupiter.api.Test;

import java.awt.*;

class AddComponentTest {

    private FlowGraph graph;


    @Test
    void shouldDoSomething() {
        // Given
        String componentName = "";
        Drawable componentToAdd = DrawableFactory.get(componentName);
        Point dropPoint = new Point(23, 23);

        // When
        AddComponent nodeAdder = new AddComponent(graph, dropPoint, componentToAdd);

        // Then
    }

}