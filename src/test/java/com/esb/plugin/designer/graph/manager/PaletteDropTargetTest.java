package com.esb.plugin.designer.graph.manager;

import com.esb.plugin.designer.graph.AbstractGraphTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.spy;

public class PaletteDropTargetTest extends AbstractGraphTest {

    private PaletteDropTarget delegate;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        delegate = spy(new PaletteDropTarget());
    }

    @Test
    void shouldDoSomething() {
        // TODO: Fill me up
    }
}
