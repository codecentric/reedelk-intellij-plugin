package com.reedelk.plugin.editor.properties.renderer.typelong;

import com.reedelk.plugin.commons.PluginValueConverterProvider;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericDocumentFilter;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericInputField;
import com.reedelk.runtime.converter.PluginValueConverter;

import javax.swing.text.DocumentFilter;

public class LongInputField extends NumericInputField<Long> {

    public LongInputField(String hint) {
        super(hint);
    }

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Long.parseLong(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected PluginValueConverter<Long> getConverter() {
        return PluginValueConverterProvider.forType(Long.class);
    }

}
