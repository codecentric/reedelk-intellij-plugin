package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.ui.JBColor;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.module.descriptor.model.WhenDescriptor;
import com.reedelk.plugin.commons.AtLeastOneWhenConditionIsTrue;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.runtime.api.annotation.TabGroup;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.util.List;
import java.util.Optional;

import static com.intellij.util.ui.JBUI.Borders;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.groupingBy;

public abstract class AbstractPropertyTypeRenderer implements PropertyTypeRenderer {

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        PropertyTitleLabel propertyTitleLabel = new PropertyTitleLabel(descriptor);

        // Apply visibility conditions to the label and the rendered component
        applyWhenVisibility(descriptor.getWhens(), context, rendered, propertyTitleLabel);

        // Add the component and its property title label to the parent container.
        FormBuilder.get()
                .addLabel(propertyTitleLabel, parent)
                .addLastField(rendered, parent);

        // Add the component to the context.
        context.addComponent(new JComponentHolder(rendered));
    }

    /**
     * A component is visible when there are no when definitions, or if there is
     * any when definition defined for the property then at least one ot them
     * must be satisfied.
     */
    protected void applyWhenVisibility(@NotNull List<WhenDescriptor> whens,
                                       @NotNull ContainerContext context,
                                       @NotNull JComponent... components) {
        // If there are no when definitions, then the component is visible (default visibility).
        if (whens.isEmpty()) return;

        // Apply visibility
        boolean isVisible = AtLeastOneWhenConditionIsTrue.of(whens, context::propertyValueFrom);
        setVisible(isVisible, components);

        // We need to group all the when definitions by property.
        // Apply on change listener
        whens.stream()
                .collect(groupingBy(WhenDescriptor::getPropertyName))
                .forEach((propertyName, whensForPropertyName) ->
                        context.subscribePropertyChange(propertyName, newValue -> {
                            boolean shouldBeVisible =
                                    AtLeastOneWhenConditionIsTrue.of(whensForPropertyName, pName -> newValue);
                            setVisible(shouldBeVisible, components);
                        }));
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

    /**
     * Sets the visibility of the provided components.
     * Note that we just swap the visibility if it has changed,
     * otherwise we don't change it and we save a call to revalidate.
     */
    private void setVisible(boolean visible, JComponent... components) {
        stream(components).forEach(component -> {
            if (component.isVisible() != visible) {
                component.setVisible(visible);
                component.revalidate();
            }
        });
    }
}
