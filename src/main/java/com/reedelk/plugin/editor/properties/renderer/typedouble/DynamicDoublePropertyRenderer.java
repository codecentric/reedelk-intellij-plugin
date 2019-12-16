package com.reedelk.plugin.editor.properties.renderer.typedouble;

import com.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldAdapter;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldBaseAdapter;

public class DynamicDoublePropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldBaseAdapter<>(new DoubleInputField(hint));
    }
}
