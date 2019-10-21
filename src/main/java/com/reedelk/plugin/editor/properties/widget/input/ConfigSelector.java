package com.reedelk.plugin.editor.properties.widget.input;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ListCellRendererWrapper;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Optional;

public class ConfigSelector extends ComboBox<ConfigMetadata> implements ItemListener {

    private SelectListener listener;

    public ConfigSelector() {
        setRenderer(new ConfigMetadataRenderer());
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
            String configTitle = Optional.ofNullable(value.getTitle()).orElse(StringUtils.EMPTY);
            StringBuilder renderedValue = new StringBuilder(configTitle);
            if (renderedValue.length() == 0) {
                renderedValue.append(value.getId());
            }
            if (StringUtils.isNotBlank(value.getFileName())) {
                renderedValue
                        .append(" ")
                        .append("(")
                        .append(value.getFileName())
                        .append(")");
            }
            setText(renderedValue.toString());
        }
    }
}
