package de.codecentric.reedelk.plugin.component.type.flowreference.widget;

import com.intellij.openapi.ui.ComboBox;
import de.codecentric.reedelk.plugin.editor.properties.commons.SimpleListItemRenderer;
import de.codecentric.reedelk.plugin.service.module.impl.subflow.SubflowMetadata;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class SubflowSelector extends ComboBox<SubflowMetadata> implements ItemListener {

    private transient SelectListener listener;

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

    private static class SubflowMetadataRenderer extends SimpleListItemRenderer<SubflowMetadata> {
        @Override
        public void customize(@NotNull JList<? extends SubflowMetadata> list, SubflowMetadata value, int index, boolean selected, boolean hasFocus) {
            if (value != null) {
                String title = value.getTitle();
                setText(title);
            }
        }
    }
}