package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelHolder;

import java.awt.*;
import java.util.List;

import static java.awt.BorderLayout.CENTER;

public class MapTableCustomObjectPanel extends DisposablePanel {

    public MapTableCustomObjectPanel(Module module, ComponentDataHolder dataHolder, TypeObjectDescriptor objectDescriptor) {
        List<PropertyDescriptor> descriptors = objectDescriptor.getObjectProperties();

        String typeFullyQualifiedName = objectDescriptor.getTypeFullyQualifiedName();

        PropertiesPanelHolder propertiesPanel =
                new PropertiesPanelHolder(module, typeFullyQualifiedName, dataHolder, descriptors);

        setLayout(new BorderLayout());
        add(ContainerFactory.pushTop(propertiesPanel), CENTER);
    }
}
