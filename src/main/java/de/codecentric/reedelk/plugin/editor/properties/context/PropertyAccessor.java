package de.codecentric.reedelk.plugin.editor.properties.context;

import de.codecentric.reedelk.plugin.graph.FlowSnapshot;

public interface PropertyAccessor {

    FlowSnapshot getSnapshot();

    <T> void set(T object);

    <T> T get();

    String getProperty();

}
