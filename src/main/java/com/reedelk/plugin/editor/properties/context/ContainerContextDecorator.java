package com.reedelk.plugin.editor.properties.context;

public class ContainerContextDecorator extends ContainerContext {

    private ContainerContextDecorator(String propertyName, ContainerContext parent) {
        super(parent.snapshot, parent.node, parent.getPropertyPath(propertyName));
    }

    public static ContainerContext decorateForProperty(String currentProperty, ContainerContext delegate) {
        return new ContainerContextDecorator(currentProperty, delegate);
    }
}
