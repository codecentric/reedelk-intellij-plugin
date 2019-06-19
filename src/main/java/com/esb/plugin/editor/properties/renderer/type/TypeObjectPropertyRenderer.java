package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class TypeObjectPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, PropertyAccessor accessor, FlowSnapshot snapshot) {
        TypeObjectDescriptor objectDescriptor = (TypeObjectDescriptor) descriptor.getPropertyType();
        return objectDescriptor.isShareable() ?
                renderShareable(objectDescriptor, accessor, snapshot) :
                renderInline(accessor, snapshot, objectDescriptor);
    }

    @NotNull
    private JComponent renderInline(PropertyAccessor accessor, FlowSnapshot snapshot, TypeObjectDescriptor objectDescriptor) {
        List<ComponentPropertyDescriptor> objectProperties = objectDescriptor.getObjectProperties();

        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();
        objectProperties.forEach(nestedPropertyDescriptor -> {

            final String displayName = nestedPropertyDescriptor.getDisplayName();
            final TypeDescriptor propertyType = nestedPropertyDescriptor.getPropertyType();

            // The accessor of type object returns a TypeObject map.
            ComponentDataHolder dataHolder = (ComponentDataHolder) accessor.get();

            PropertyAccessor acc = PropertyAccessorFactory.get()
                    .typeDescriptor(nestedPropertyDescriptor.getPropertyType())
                    .dataHolder(dataHolder)
                    .propertyName(nestedPropertyDescriptor.getPropertyName())
                    .build();

            TypeRendererFactory typeRendererFactory = TypeRendererFactory.get();
            JComponent renderedComponent = typeRendererFactory.from(propertyType)
                    .render(nestedPropertyDescriptor, acc, snapshot);

            FormBuilder.get()
                    .addLabel(displayName, propertiesPanel)
                    .addLastField(renderedComponent, propertiesPanel);
        });
        return propertiesPanel;
    }

    @NotNull
    private JComponent renderShareable(TypeObjectDescriptor typeObjectDescriptor, PropertyAccessor accessor, FlowSnapshot snapshot) {
        JPanel controls = new JPanel();

        JLabel editLabel = new JLabel(Icons.Config.Edit);
        editLabel.setText("Edit");
        controls.add(editLabel);

        JLabel deleteLabel = new JLabel(Icons.Config.Delete);
        deleteLabel.setText("Delete");
        controls.add(deleteLabel);

        JLabel addLabel = new JLabel(Icons.Config.Add);
        addLabel.setText("Add");
        controls.add(addLabel);
        addLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Project project = null;
                        DialogWrapper wrapper = new DialogWrapper(project, false) {
                            {
                                this.init();
                            }

                            @Nullable
                            @Override
                            protected JComponent createCenterPanel() {
                                JBPanel panel = new JBPanel();
                                panel.add(new JLabel("Test"));
                                panel.add(new JLabel("Test1"));
                                panel.add(new JLabel("Test2"));
                                return panel;
                            }

                            @Override
                            protected JComponent createSouthPanel() {
                                JBPanel panel = new JBPanel();
                                panel.add(new JLabel("soutch1"));
                                panel.add(new JLabel("south"));
                                panel.add(new JLabel("soucht2"));
                                return panel;
                            }
                        };
                        wrapper.show();
                    }
                });
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(new ComboBox<>(), BorderLayout.CENTER);
        wrapper.add(controls, BorderLayout.EAST);
        return wrapper;
    }
}
