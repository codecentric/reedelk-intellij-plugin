package com.reedelk.plugin.editor.properties.renderer.typeenum;

import com.intellij.openapi.ui.ComboBox;
import com.reedelk.plugin.editor.properties.commons.InputChangeListener;
import com.reedelk.plugin.editor.properties.commons.SimpleListItemRenderer;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultMapEntry;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class EnumDropDown extends ComboBox<KeyValue> implements ItemListener {

    private final transient List<KeyValue> keyValues;
    private transient InputChangeListener listener;

    public EnumDropDown(Map<String, String> valueAndDisplayNameMap) {
        this.keyValues = valueAndDisplayNameMap
                .entrySet()
                .stream()
                .map(entry -> new DefaultMapEntry(entry.getKey(), entry.getValue()))
                .collect(toList());

        DefaultComboBoxModel<KeyValue> comboModel = new DefaultComboBoxModel<>();
        keyValues.forEach(comboModel::addElement);
        setModel(comboModel);
        setRenderer(new ItemRenderer());
        addItemListener(this);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            KeyValue item = (KeyValue) event.getItem();
            if (listener != null) {
                listener.onChange(item.getKey());
            }
        }
    }

    public void addListener(InputChangeListener changeListener) {
        this.listener = changeListener;
    }

    public void setValue(Object value) {
        if (value == null) return;

        Optional<KeyValue> matching = keyValues.stream()
                .filter(keyValue -> keyValue.getKey().equals(value))
                .findFirst();

        if (matching.isPresent()) {
            setSelectedItem(matching.get());
        } else if (!keyValues.isEmpty()) {
            // otherwise we take the first one
            setSelectedItem(keyValues.get(0));
        }
    }

    static class ItemRenderer extends SimpleListItemRenderer<KeyValue> {
        @Override
        public void customize(@NotNull JList<? extends KeyValue> list, KeyValue value, int index, boolean selected, boolean hasFocus) {
            if (value != null) {
                setText((String) value.getValue());
            }
        }
    }
}
