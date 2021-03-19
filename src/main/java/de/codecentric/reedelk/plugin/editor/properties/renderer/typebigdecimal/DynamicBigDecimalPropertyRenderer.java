package de.codecentric.reedelk.plugin.editor.properties.renderer.typebigdecimal;

import de.codecentric.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldAdapter;
import de.codecentric.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldBaseAdapter;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;

public class DynamicBigDecimalPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldBaseAdapter<>(new BigDecimalInputField(hint));
    }
}
