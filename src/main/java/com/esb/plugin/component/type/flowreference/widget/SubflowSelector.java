package com.esb.plugin.component.type.flowreference.widget;

import com.esb.plugin.service.module.impl.SubflowMetadata;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ListCellRendererWrapper;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class SubflowSelector extends ComboBox<SubflowMetadata> implements ItemListener {

    private SelectListener listener;

    public SubflowSelector(List<SubflowMetadata> subflowsMetadata) {
        setRenderer(new SubflowMetadataRenderer());
        subflowsMetadata.forEach(this::addItem);
        addItemListener(this);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            SubflowMetadata item = (SubflowMetadata) event.getItem();
            if (listener != null) listener.onSelect(item);
        }
    }

    public void addSelectListener(SelectListener listener) {
        this.listener = listener;
    }

    public interface SelectListener {
        void onSelect(SubflowMetadata subflowMetadata);
    }

    private class SubflowMetadataRenderer extends ListCellRendererWrapper<SubflowMetadata> {
        @Override
        public void customize(JList list, SubflowMetadata value, int index, boolean selected, boolean hasFocus) {
            String title = value.getTitle();
            this.setText(title);
        }
    }
}