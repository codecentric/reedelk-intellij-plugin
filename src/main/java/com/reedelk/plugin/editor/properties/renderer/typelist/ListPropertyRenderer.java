package com.reedelk.plugin.editor.properties.renderer.typelist;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBList;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.intellij.util.ui.JBUI.Borders;

public class ListPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        // Copy data into the model
        List<Object> arrayItems = propertyAccessor.get();
        DefaultListModel<Object> model = new DefaultListModel<>();
        arrayItems.forEach(model::addElement);
        model.addListDataListener(new ListModelChangeListener(propertyAccessor));

        // Create list
        JBList<Object> list = new JBList<>(model);
        list.setCellRenderer(new StripedItemCellRenderer());
        list.setBorder(Borders.empty(0, 10));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Layout
        ListControlPanel listControls = new ListControlPanel(list, model);
        ListScrollPane listScrollPane = new ListScrollPane(list);

        DisposablePanel controlsAndListPanel = new DisposablePanel();
        controlsAndListPanel.setLayout(new BorderLayout());
        controlsAndListPanel.add(listControls, BorderLayout.NORTH);
        controlsAndListPanel.add(listScrollPane, BorderLayout.CENTER);
        return controlsAndListPanel;
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        PropertyTitleLabel propertyTitleLabel = new PropertyTitleLabel(descriptor);
        propertyTitleLabel.setBorder(Borders.emptyTop(10));

        // Apply visibility conditions to the label and the rendered component
        applyWhenVisibility(descriptor.getWhens(), context, rendered, propertyTitleLabel);

        // Add the component and its property title label to the parent container.
        FormBuilder.get()
                .addLabelTop(propertyTitleLabel, parent)
                .addLastField(rendered, parent);

        // Add the component to the context.
        context.addComponent(new JComponentHolder(rendered));
    }
}
