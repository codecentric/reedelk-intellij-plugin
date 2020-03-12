package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.intellij.openapi.ui.ComboBox;
import com.reedelk.plugin.editor.properties.commons.SimpleListItemRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.InputChangeListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CustomObjectSelectorCombo extends ComboBox<CustomObjectMetadata> implements ItemListener {

    private transient InputChangeListener listener;

    public CustomObjectSelectorCombo() {
        setRenderer(new CustomObjectMetadataRenderer());
        addItemListener(this);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            CustomObjectMetadata item = (CustomObjectMetadata) event.getItem();
            if (listener != null) {
                listener.onChange(item);
            }
        }
    }

    public void removeListener() {
        this.listener = null;
    }

    public void addListener(InputChangeListener changeListener) {
        this.listener = changeListener;
    }

    private static class CustomObjectMetadataRenderer extends SimpleListItemRenderer<CustomObjectMetadata> {
        @Override
        public void customize(@NotNull JList<? extends CustomObjectMetadata> list, CustomObjectMetadata value, int index, boolean selected, boolean hasFocus) {
            if (value == null) return;
            setText("sadf");
        }
    }
}
