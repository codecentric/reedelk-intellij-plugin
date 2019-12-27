package com.reedelk.plugin.editor.properties.renderer.typefloat;

import com.reedelk.plugin.commons.PluginValueConverterProvider;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericDocumentFilter;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericInputField;
import com.reedelk.runtime.converter.PluginValueConverter;

import javax.swing.text.DocumentFilter;

public class FloatInputField extends NumericInputField<Float> {

    public FloatInputField(String hint) {
        super(hint);
    }

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Float.parseFloat(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected PluginValueConverter<Float> getConverter() {
        return PluginValueConverterProvider.forType(Float.class);
    }
}
