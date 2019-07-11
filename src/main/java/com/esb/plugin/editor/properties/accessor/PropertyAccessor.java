package com.esb.plugin.editor.properties.accessor;

import com.esb.plugin.graph.FlowSnapshot;

public interface PropertyAccessor {

    FlowSnapshot getSnapshot();

    void set(Object object);

    <T> T get();

}
