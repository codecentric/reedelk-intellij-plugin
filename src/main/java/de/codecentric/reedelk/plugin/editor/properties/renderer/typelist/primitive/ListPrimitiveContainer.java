package de.codecentric.reedelk.plugin.editor.properties.renderer.typelist.primitive;

import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.intellij.ui.components.JBList;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Optional.ofNullable;

public class ListPrimitiveContainer extends DisposablePanel {

    public ListPrimitiveContainer(@NotNull PropertyDescriptor descriptor,
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

        setBorder(empty());
        setLayout(new BorderLayout());
        add(listControls, BorderLayout.NORTH);
        add(listScrollPane, BorderLayout.CENTER);
    }
}
