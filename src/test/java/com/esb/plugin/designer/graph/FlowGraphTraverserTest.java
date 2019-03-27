package com.esb.plugin.designer.graph;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.Tile;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Set;
import java.util.function.Consumer;

public class FlowGraphTraverserTest {

    private final int PADDING = 5;


    @Test
    void shouldDoSomething() {
        // Given
        String json = readJson(TestJson.LINEAR);
        FlowGraphBuilder builder = new FlowGraphBuilder(json);
        FlowGraph graph = builder.get();

        // When
        int minX = 0;
        Iterable<Drawable> drawables = graph.depthFirstPostOrder();
        for (Drawable drawable : drawables) {
            Set<Drawable> successors = graph.successors(drawable);
            if (successors.isEmpty()) {
                drawable.getPosition().x = 0;
                drawable.getPosition().y = 0;
                continue;
            }

            if (successors.size() == 1) {
                Drawable next = successors.iterator().next();
                int newX = next.getPosition().x - (Math.floorDiv(Tile.INSTANCE.width, 2) + Tile.INSTANCE.width + PADDING);
                int newY = next.getPosition().y;

                minX = Math.min(newX, minX);
                drawable.getPosition().x = newX;
                drawable.getPosition().y = newY;
            } else {
                throw new RuntimeException("Unknown case");
            }

        }

        // Normalize
        final int offset = Math.abs(minX);
        graph.depthFirstPostOrder().forEach(new Consumer<Drawable>() {
            @Override
            public void accept(Drawable drawable) {
                drawable.getPosition().x += offset;
            }
        });


        graph.breadthFirst()
                .forEach(drawable ->
                        System.out.println("Component: " + drawable.component().getName() + ", X: " + drawable.getPosition().x + ", Y: " + drawable.getPosition().y));
    }


    private String readJson(TestJson testJson) {
        URL url = testJson.url();
        return FileUtils.readFrom(url);
    }
}
