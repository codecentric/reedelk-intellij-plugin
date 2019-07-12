package com.esb.plugin.editor.properties.accessor;

import com.esb.plugin.graph.FlowSnapshot;

public interface PropertyAccessor {

    FlowSnapshot getSnapshot();

    <T> void set(T object);

    <T> T get();

    String getProperty();

}
