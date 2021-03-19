package de.codecentric.reedelk.plugin.editor.properties.renderer.typelong;

import de.codecentric.reedelk.plugin.editor.properties.commons.InputField;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractNumericPropertyRenderer;

public class LongPropertyRenderer extends AbstractNumericPropertyRenderer<Long> {
    @Override
    protected InputField<Long> getInputField(String hint) {
        return new LongInputField(hint);
    }
}
