package de.codecentric.reedelk.plugin.editor.properties.renderer.typeinteger;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.plugin.converter.ValueConverterProvider;
import de.codecentric.reedelk.plugin.editor.properties.commons.NumericDocumentFilter;
import de.codecentric.reedelk.plugin.editor.properties.commons.NumericInputField;

import javax.swing.text.DocumentFilter;

public class IntegerInputField extends NumericInputField<Integer> {

    public IntegerInputField(String hint) {
        super(hint);
    }

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Integer.parseInt(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<Integer> getConverter() {
        return ValueConverterProvider.forType(Integer.class);
    }
}
