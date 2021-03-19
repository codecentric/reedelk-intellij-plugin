package de.codecentric.reedelk.plugin.editor.properties.renderer.typestring;

import de.codecentric.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldAdapter;
import de.codecentric.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldBaseAdapter;
import de.codecentric.reedelk.plugin.editor.properties.commons.StringInputField;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;

public class DynamicStringPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldBaseAdapter<>(new StringInputField(hint));
    }
}
