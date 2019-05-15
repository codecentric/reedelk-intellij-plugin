package com.esb.plugin.designer.properties;

public interface PropertyRendererVisitable {

    void accept(PropertyRendererVisitor visitor);
}
