package com.reedelk.plugin.editor.properties.renderer.typelist;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBList;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeListDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.plugin.editor.properties.commons.TabLabelHorizontal;
import com.reedelk.plugin.editor.properties.renderer.AbstractTabGroupAwarePropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Optional.ofNullable;

public class ListPropertyRenderer extends AbstractTabGroupAwarePropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final String propertyDisplayName = descriptor.getDisplayName();
        final TypeListDescriptor propertyType = descriptor.getType();

        return ofNullable(propertyType.getTabGroup())
                // Tab group exists
                .map((Function<String, JComponent>) tabGroupName -> {
                    final DisposableTabbedPane tabbedPane = tabbedPaneFrom(descriptor, context);
                    tabbedPane.addTab(propertyDisplayName, createContent(descriptor, propertyAccessor));
                    tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new TabLabelHorizontal(propertyDisplayName));
                    return tabbedPane;
                }).orElseGet(() ->
                        // No tab group
                        createContent(descriptor, propertyAccessor));
    }

    @NotNull
    private JComponent createContent(@NotNull PropertyDescriptor descriptor,
                                     @NotNull PropertyAccessor propertyAccessor) {
        // Copy data into the model
        List<Object> arrayItems = propertyAccessor.get();
        DefaultListModel<Object> model = new DefaultListModel<>();
        if (arrayItems != null) {
            arrayItems.forEach(model::addElement);
        }
        model.addListDataListener(new ListModelChangeListener(propertyAccessor));

        // Create list
        JBList<Object> list = new JBList<>(model);
        list.setCellRenderer(new StripedItemCellRenderer());
        list.setBorder(empty(0, 10));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Layout
        String hint = ofNullable(descriptor.getHintValue())
                .orElse(message("properties.type.list.item.hint"));
        ListControlPanel listControls = new ListControlPanel(list, model, hint);
        ListScrollPane listScrollPane = new ListScrollPane(list);
        listScrollPane.setBorder(empty());

        DisposablePanel controlsAndListPanel = new DisposablePanel();
        controlsAndListPanel.setBorder(empty());
        controlsAndListPanel.setLayout(new BorderLayout());
        controlsAndListPanel.add(listControls, BorderLayout.NORTH);
        controlsAndListPanel.add(listScrollPane, BorderLayout.CENTER);
        return controlsAndListPanel;
    }
}
