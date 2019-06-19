package com.esb.plugin.editor.properties.widget.input;

import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ListCellRendererWrapper;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class ConfigSelector extends ComboBox<ConfigMetadata> implements ItemListener {

    private SelectListener listener;

    public ConfigSelector(List<ConfigMetadata> configMetadata) {
        setRenderer(new ConfigMetadataRenderer());
        configMetadata.forEach(this::addItem);
        addItemListener(this);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            ConfigMetadata item = (ConfigMetadata) event.getItem();
            if (listener != null) listener.onSelect(item);
        }
    }

    public void addSelectListener(SelectListener listener) {
        this.listener = listener;
    }

    public interface SelectListener {
        void onSelect(ConfigMetadata configMetadata);
    }

    private class ConfigMetadataRenderer extends ListCellRendererWrapper<ConfigMetadata> {

        @Override
        public void customize(JList list, ConfigMetadata value, int index, boolean selected, boolean hasFocus) {
            String title = value.getTitle();
            if (value.getTitle() == null) {
                title = value.getId();
            }
            this.setText(title);
        }
    }
}
