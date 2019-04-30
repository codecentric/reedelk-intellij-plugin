package com.esb.plugin.graph.node;

import com.esb.component.Stop;
import com.esb.plugin.component.Component;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.designer.canvas.AbstractGraphNode;

public class StopGraphNode extends AbstractGraphNode {

    private static final ComponentDescriptor descriptor = ComponentDescriptor.create()
            .fullyQualifiedName(Stop.class.getName())
            .displayName(Stop.class.getSimpleName())
            .build();

    public StopGraphNode() {
        super(new Component(descriptor));
    }

}
