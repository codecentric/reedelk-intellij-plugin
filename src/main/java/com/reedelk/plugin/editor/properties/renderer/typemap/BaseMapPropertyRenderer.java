package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.reedelk.component.descriptor.ComponentPropertyDescriptor;
import com.reedelk.component.descriptor.TypeMapDescriptor;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.commons.JComponentHolder;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRenderer;
import com.reedelk.runtime.api.annotation.TabGroup;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.util.Optional;

import static com.reedelk.plugin.commons.Colors.TOOL_WINDOW_PROPERTIES_TEXT;

abstract class BaseMapPropertyRenderer implements PropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        // Map properties are grouped together into a  Tabbed Pane.
        Optional<DisposableTabbedPane> groupTabbedPane = getGroupTabbedPane(propertyDescriptor, context);

        DisposableTabbedPane tabbedPane = groupTabbedPane.orElseGet(() -> {
            DisposableTabbedPane tabbed = new DisposableTabbedPane(JTabbedPane.LEFT);
            tabbed.setPreferredSize(Sizes.TabbedPane.HEIGHT);

            TypeMapDescriptor propertyType = propertyDescriptor.getPropertyType();
            Optional<String> tabGroup = Optional.ofNullable(propertyType.getTabGroup());

            Border border = BorderFactory.createLineBorder(JBColor.LIGHT_GRAY);
            if (tabGroup.isPresent()) {
                TitledBorder titledBorder = BorderFactory.createTitledBorder(border, tabGroup.get());
                titledBorder.setTitleColor(TOOL_WINDOW_PROPERTIES_TEXT);
                border = titledBorder;
            }

            Border top = BorderFactory.createEmptyBorder(5, 0, 0, 0);
            CompoundBorder compoundBorder = new CompoundBorder(top, border);
            tabbed.setBorder(compoundBorder);
            return tabbed;
        });

        JComponent content = getContent(module, propertyAccessor, context);
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
        Optional<DisposableTabbedPane> groupTabbedPane = getGroupTabbedPane(descriptor, context);
        if (groupTabbedPane.isPresent()) {
            return;
        }

        // Add the component to the parent container.
        FormBuilder.get().addLastField(rendered, parent);

        JComponentHolder holder = new JComponentHolder(rendered);

        // Add the component to the container context.
        TypeMapDescriptor propertyType = descriptor.getPropertyType();
        Optional.ofNullable(propertyType.getTabGroup())
                .ifPresent(group -> holder.addMetadata(TabGroup.class.getName(), group));

        context.addComponent(holder);
    }

    protected abstract JComponent getContent(Module module, PropertyAccessor propertyAccessor, @NotNull ContainerContext context);

    private Optional<DisposableTabbedPane> getGroupTabbedPane(ComponentPropertyDescriptor propertyDescriptor, ContainerContext context) {
        TypeMapDescriptor propertyType = propertyDescriptor.getPropertyType();
        Optional<String> tabGroup = Optional.ofNullable(propertyType.getTabGroup());
        if (tabGroup.isPresent()) {
            // Tab group annotation was present in the property definition. We need to lookup
            // for other components in the panel context having TabGroup metadata with the
            // same value of this tab group name.
            Optional<JComponent> componentMatchingMetadata =
                    context.getComponentMatchingMetadata((key, value) ->
                            TabGroup.class.getName().equals(key) && tabGroup.get().equals(value));
            // Exists a group of tabbed pane matching the TabGroup annotation's value.
            if (componentMatchingMetadata.isPresent()) {
                DisposableTabbedPane matchingTabbedPane = (DisposableTabbedPane) componentMatchingMetadata.get();
                return Optional.of(matchingTabbedPane);
            }
        }
        return Optional.empty();
    }
}
