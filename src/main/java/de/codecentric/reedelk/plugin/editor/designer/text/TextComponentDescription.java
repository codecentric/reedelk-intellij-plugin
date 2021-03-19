package de.codecentric.reedelk.plugin.editor.designer.text;

import de.codecentric.reedelk.plugin.commons.Colors;
import de.codecentric.reedelk.plugin.commons.Fonts;
import de.codecentric.reedelk.plugin.commons.SplitTextInLines;
import de.codecentric.reedelk.plugin.component.ComponentData;

import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;

public class TextComponentDescription extends AbstractText {

    private static final Pattern REGEX = Pattern.compile(".{1,17}(?:\\s|$)", Pattern.DOTALL);

    private ComponentData componentData;

    public TextComponentDescription(ComponentData componentData) {
        super(Fonts.Component.DESCRIPTION, HorizontalAlignment.CENTER, VerticalAlignment.BELOW);
        this.componentData = componentData;
    }

    @Override
    protected List<String> getText() {
        String description = componentData.get(Implementor.description());
        return SplitTextInLines.from(description, REGEX);
    }

    @Override
    protected Color getColor() {
        return Colors.TEXT_COMPONENT_DESCRIPTION;
    }

    @Override
    protected Color getSelectedColor() {
        return Colors.TEXT_COMPONENT_DESCRIPTION_SELECTED;
    }
}
