package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.ui.JBColor;
import com.reedelk.module.descriptor.model.property.CollectionAwareDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.runtime.api.annotation.TabGroup;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.util.Optional;

import static com.intellij.util.ui.JBUI.Borders;
import static com.intellij.util.ui.JBUI.Borders.emptyTop;
import static java.util.Optional.ofNullable;

public abstract class AbstractCollectionAwarePropertyTypeRenderer extends AbstractPropertyTypeRenderer {

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        final CollectionAwareDescriptor propertyType = descriptor.getType();
        final boolean isTabGroupPresent = ofNullable(propertyType.getTabGroup()).isPresent();

        if (isTabGroupPresent) {

            // If exists a group tabbed pane, then we don't add it to the parent because
            // it has been already added to the tabbed pane above in the render method.
            Optional<DisposableTabbedPane> groupTabbedPane = findGroupTabbedPane(descriptor, context);
            if (groupTabbedPane.isPresent()) {
                return;
            }

            // Note that the rendered component is the tabbed pane to be added to the parent container.
            // For now we assume that its visibility is the same for all the tabs present in the tabbed pane.
            WhenVisibilityApplier.on(descriptor.getWhens(), context, rendered);

            // Add the component to the parent container.
            FormBuilder.get().addFullWidthAndHeight(rendered, parent);

            JComponentHolder holder = new JComponentHolder(rendered);

            // Add the component to the container context.
            Optional.ofNullable(propertyType.getTabGroup())
                    .ifPresent(group -> holder.addMetadata(TabGroup.class.getName(), group));

            context.addComponent(holder);

        } else {
            PropertyTitleLabel propertyTitleLabel = new PropertyTitleLabel(descriptor);
            propertyTitleLabel.setBorder(emptyTop(6));

            // Apply visibility conditions to the label and the rendered component
            WhenVisibilityApplier.on(descriptor.getWhens(), context, rendered, propertyTitleLabel);

            // Add the component and its property title label to the parent container.
            FormBuilder.get()
                    .addLabelTop(propertyTitleLabel, parent)
                    .addLastField(rendered, parent);

            // Add the component to the context.
            context.addComponent(new JComponentHolder(rendered));
        }
    }

    @NotNull
    protected DisposableTabbedPane tabbedPaneFrom(@NotNull PropertyDescriptor propertyDescriptor,
                                                  @NotNull ContainerContext context) {
        // Map properties are grouped together into a  Tabbed Pane.
        return findGroupTabbedPane(propertyDescriptor, context).orElseGet(() -> {
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

    protected Optional<DisposableTabbedPane> findGroupTabbedPane(PropertyDescriptor propertyDescriptor, ContainerContext context) {
        CollectionAwareDescriptor propertyType = propertyDescriptor.getType();
        Optional<String> tabGroup = Optional.ofNullable(propertyType.getTabGroup());
        if (tabGroup.isPresent()) {
            // Tab group annotation was present in the property definition. We need to lookup
            // for other components in the panel context having TabGroup metadata with the
            // same value of this tab group name.
            Optional<JComponent> componentMatchingMetadata =
                    context.findComponentMatchingMetadata((key, value) ->
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
