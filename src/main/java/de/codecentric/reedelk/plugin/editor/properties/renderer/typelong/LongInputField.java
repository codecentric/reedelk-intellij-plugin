package de.codecentric.reedelk.plugin.editor.properties.renderer.typelong;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.plugin.converter.ValueConverterProvider;
import de.codecentric.reedelk.plugin.editor.properties.commons.NumericDocumentFilter;
import de.codecentric.reedelk.plugin.editor.properties.commons.NumericInputField;

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
    protected ValueConverter<Long> getConverter() {
        return ValueConverterProvider.forType(Long.class);
    }
}
