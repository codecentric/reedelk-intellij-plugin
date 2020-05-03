package com.reedelk.plugin.editor.properties.context;

public class ContainerContextDecorator extends ContainerContextDefault {

    private ContainerContextDecorator(String propertyName, ContainerContext parent) {
        super(ComponentPropertyPath.join(parent.componentPropertyPath(), propertyName));
    }

    public static ContainerContext decorateForProperty(String currentProperty, ContainerContext delegate) {
        return new ContainerContextDecorator(currentProperty, delegate);
    }
}
