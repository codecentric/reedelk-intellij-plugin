package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.widget.*;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ComboBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.esb.plugin.commons.Icons.Config.*;

public class TypeObjectPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor descriptor, PropertyAccessor accessor, FlowSnapshot snapshot) {
        TypeObjectDescriptor objectDescriptor = (TypeObjectDescriptor) descriptor.getPropertyType();
        return objectDescriptor.isShareable() ?
                renderShareable(module, objectDescriptor, accessor, snapshot) :
                renderInline(module, accessor, snapshot, objectDescriptor);
    }

    @NotNull
    private JComponent renderInline(Module module, PropertyAccessor propertyAccessor, FlowSnapshot snapshot, TypeObjectDescriptor objectDescriptor) {
        List<ComponentPropertyDescriptor> objectProperties = objectDescriptor.getObjectProperties();

        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();
        objectProperties.forEach(nestedPropertyDescriptor -> {

            final String displayName = nestedPropertyDescriptor.getDisplayName();
            final TypeDescriptor propertyType = nestedPropertyDescriptor.getPropertyType();

            // The accessor of type object returns a TypeObject map.
            ComponentDataHolder dataHolder = (ComponentDataHolder) propertyAccessor.get();

            PropertyAccessor nestedPropertyAccessor = PropertyAccessorFactory.get()
                    .typeDescriptor(nestedPropertyDescriptor.getPropertyType())
                    .dataHolder(dataHolder)
                    .propertyName(nestedPropertyDescriptor.getPropertyName())
                    .build();

            TypeRendererFactory typeRendererFactory = TypeRendererFactory.get();
            JComponent renderedComponent = typeRendererFactory.from(propertyType)
                    .render(module, nestedPropertyDescriptor, nestedPropertyAccessor, snapshot);

            FormBuilder.get()
                    .addLabel(displayName, propertiesPanel)
                    .addLastField(renderedComponent, propertiesPanel);
        });

        return propertiesPanel;
    }


    @NotNull
    private JComponent renderShareable(Module module, TypeObjectDescriptor typeObjectDescriptor, PropertyAccessor accessor, FlowSnapshot snapshot) {

        JComponent panel = renderInline(module, accessor, snapshot, typeObjectDescriptor);

        ActionableCommandButton editConfigCommand = new ActionableCommandButton("Edit", Edit);
        editConfigCommand.addListener(() ->
                SwingUtilities.invokeLater(() ->
                        new DialogEditConfiguration(module.getProject(), panel).show()));

        ActionableCommandButton addConfigCommand = new ActionableCommandButton("Add", Add);
        addConfigCommand.addListener(() ->
                SwingUtilities.invokeLater(() ->
                        new DialogAddConfiguration(module.getProject(), panel).show()));

        ActionableCommandButton deleteConfigCommand = new ActionableCommandButton("Delete", Delete);
        deleteConfigCommand.addListener(() ->
                SwingUtilities.invokeLater(() ->
                        new DialogRemoveConfiguration(module.getProject()).show()));

        JPanel controls = new JPanel();
        controls.add(editConfigCommand);
        controls.add(deleteConfigCommand);
        controls.add(addConfigCommand);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(new ComboBox<>(), BorderLayout.CENTER);
        wrapper.add(controls, BorderLayout.EAST);
        return wrapper;
    }

}
