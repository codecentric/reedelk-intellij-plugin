package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.ui.JBColor;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.commons.JComponentHolder;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.reedelk.runtime.api.annotation.TabGroup;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.util.Optional;

import static com.intellij.util.ui.JBUI.Borders;

public abstract class BaseMapPropertyRenderer extends AbstractPropertyTypeRenderer {

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        // If exists a group tabbed pane, then we don't add it to the parent because
        // it has been already added to the tabbed pane above in the render method.
        Optional<DisposableTabbedPane> groupTabbedPane = getGroupTabbedPane(descriptor, context);
        if (groupTabbedPane.isPresent()) {
            return;
        }

        // Add the component to the parent container.
        FormBuilder.get().addFullWidthAndHeight(rendered, parent);

        JComponentHolder holder = new JComponentHolder(rendered);

        // Add the component to the container context.
        TypeMapDescriptor propertyType = descriptor.getType();
        Optional.ofNullable(propertyType.getTabGroup())
                .ifPresent(group -> holder.addMetadata(TabGroup.class.getName(), group));

        context.addComponent(holder);
    }

    @NotNull
    protected DisposableTabbedPane tabbedPaneFrom(@NotNull PropertyDescriptor propertyDescriptor, @NotNull ContainerContext context, TypeMapDescriptor propertyType) {
        // Map properties are grouped together into a  Tabbed Pane.
        return getGroupTabbedPane(propertyDescriptor, context).orElseGet(() -> {
            Border outerBorder = Borders.empty(5, 0, 0, 3);
            Border innerBorder = Borders.customLine(JBColor.LIGHT_GRAY);
            CompoundBorder compoundBorder = new CompoundBorder(outerBorder, innerBorder);

            DisposableTabbedPane tabbed = new DisposableTabbedPane(JTabbedPane.TOP);
            tabbed.setPreferredSize(Sizes.TabbedPane.HEIGHT_TOP_PLACEMENT);
            tabbed.setPreferredSize(Sizes.Table.TABBED);
            tabbed.setBorder(compoundBorder);
            return tabbed;
        });
    }

    protected Optional<DisposableTabbedPane> getGroupTabbedPane(PropertyDescriptor propertyDescriptor, ContainerContext context) {
        TypeMapDescriptor propertyType = propertyDescriptor.getType();
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
