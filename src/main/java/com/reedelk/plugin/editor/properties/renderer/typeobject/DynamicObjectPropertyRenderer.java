package com.reedelk.plugin.editor.properties.renderer.typeobject;

import com.reedelk.plugin.editor.properties.renderer.typestring.DynamicStringPropertyRenderer;

/**
 * An object dynamic property does not have a not-dynamic field as a string input field
 * since there is not a way to let the user enter a java object from the input field.
 */
public class DynamicObjectPropertyRenderer extends DynamicStringPropertyRenderer {
}
