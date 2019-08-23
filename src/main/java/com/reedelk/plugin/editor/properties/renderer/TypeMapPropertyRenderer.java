package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTabbedPane;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeMapDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.editor.properties.widget.JComponentHolder;
import com.reedelk.runtime.api.annotation.TabGroup;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.util.Optional;

public class TypeMapPropertyRenderer implements TypePropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        Optional<JBTabbedPane> groupTabbedPane = getGroupTabbedPane(propertyDescriptor, context);

        JBTabbedPane tabbedPane = groupTabbedPane.orElseGet(() -> {
            JBTabbedPane tabbed = new JBTabbedPane(JTabbedPane.LEFT);

            TypeMapDescriptor propertyType = (TypeMapDescriptor) propertyDescriptor.getPropertyType();
            Optional<String> tabGroup = propertyType.getTabGroup();

            Border border = BorderFactory.createLineBorder(JBColor.LIGHT_GRAY);
            if (tabGroup.isPresent()) {
                border = BorderFactory.createTitledBorder(border, tabGroup.get());
            }

            Border top = BorderFactory.createEmptyBorder(5, 0, 0, 0);
            CompoundBorder compoundBorder = new CompoundBorder(top, border);
            tabbed.setBorder(compoundBorder);
            return tabbed;
        });

        JComponent content = new TypeMapTabContainer(propertyAccessor);

        tabbedPane.addTab(propertyDescriptor.getDisplayName(), content);
        return tabbedPane;
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull ComponentPropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {
        // If exists a group tabbed pane, then we don't add it to the parent
        // because it has been already added to the tabbed pane above in the
        // render method.
        Optional<JBTabbedPane> groupTabbedPane = getGroupTabbedPane(descriptor, context);
        if (groupTabbedPane.isPresent()) {
            return;
        }

        // Add the component to the parent container.
        FormBuilder.get().addLastField(rendered, parent);

        // Add the component to the container context.
        TypeMapDescriptor propertyType = (TypeMapDescriptor) descriptor.getPropertyType();
        Optional<String> tabGroup = propertyType.getTabGroup();

        JComponentHolder holder = new JComponentHolder(rendered);
        tabGroup.ifPresent(group -> holder.addMetadata(TabGroup.class.getName(), group));

        context.addComponent(holder);
    }

    private Optional<JBTabbedPane> getGroupTabbedPane(ComponentPropertyDescriptor propertyDescriptor, ContainerContext context) {
        TypeMapDescriptor propertyType = (TypeMapDescriptor) propertyDescriptor.getPropertyType();
        Optional<String> tabGroup = propertyType.getTabGroup();
        if (tabGroup.isPresent()) {
            // Tab group annotation was present in the property definition. We need to lookup
            // for other components in the panel context having TabGroup metadata with the
            // same value of this tab group name.
            Optional<JComponent> componentMatchingMetadata =
                    context.getComponentMatchingMetadata((key, value) ->
                            TabGroup.class.getName().equals(key) && tabGroup.get().equals(value));
            // Exists a group of tabbed pane matching the TabGroup annotation's value.
            if (componentMatchingMetadata.isPresent()) {
                JBTabbedPane matchingTabbedPane = (JBTabbedPane) componentMatchingMetadata.get();
                return Optional.of(matchingTabbedPane);
            }
        }
        return Optional.empty();
    }
}
