package com.reedelk.plugin.editor.properties.context;

import com.reedelk.plugin.graph.FlowSnapshot;

public interface PropertyAccessor {

    FlowSnapshot getSnapshot();

    <T> void set(T object);

    <T> T get();

    String getProperty();

}
