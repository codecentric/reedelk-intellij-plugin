package com.reedelk.plugin.editor.properties.renderer.typescript;

import com.intellij.openapi.ui.ComboBox;
import com.reedelk.plugin.editor.properties.commons.InputChangeListener;
import com.reedelk.plugin.editor.properties.commons.SimpleListItemRenderer;
import com.reedelk.plugin.service.module.impl.script.ScriptResource;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ScriptSelectorCombo extends ComboBox<ScriptResource> implements ItemListener {

    private transient InputChangeListener listener;

    ScriptSelectorCombo() {
        setRenderer(new ScriptSelectorRenderer());
        addItemListener(this);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED && listener != null) {
            ScriptResource item = (ScriptResource) event.getItem();
            listener.onChange(item);
        }
    }

    void removeListener() {
        this.listener = null;
    }

    void addListener(InputChangeListener changeListener) {
        this.listener = changeListener;
    }

    private static class ScriptSelectorRenderer extends SimpleListItemRenderer<ScriptResource> {
        @Override
        public void customize(@NotNull JList<? extends ScriptResource> list, ScriptResource value, int index, boolean selected, boolean hasFocus) {
            if (value != null) {
                if (StringUtils.isBlank(value.getPath())) {
                    setText(value.getDisplayName());
                } else {
                    setText(value.getDisplayName() + " (" + value.getPath() + ")");
                }
            }
        }
    }
}
