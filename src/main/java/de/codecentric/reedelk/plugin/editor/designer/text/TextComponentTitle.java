package de.codecentric.reedelk.plugin.editor.designer.text;

import de.codecentric.reedelk.plugin.commons.Colors;
import de.codecentric.reedelk.plugin.commons.Fonts;
import de.codecentric.reedelk.plugin.commons.SplitTextInLines;
import de.codecentric.reedelk.plugin.component.ComponentData;

import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class TextComponentTitle extends AbstractText {

    private static final Pattern REGEX = Pattern.compile(".{1,17}(?:\\s|$)", Pattern.DOTALL);

    private final String title;

    public TextComponentTitle(ComponentData componentData) {
        super(Fonts.Component.TITLE, HorizontalAlignment.CENTER, VerticalAlignment.BELOW);
        this.title = componentData.getDisplayName();
    }

    @Override
    protected List<String> getText() {
        return SplitTextInLines.from(title, REGEX);
    }

    @Override
    protected Color getColor() {
        return Colors.TEXT_COMPONENT_TITLE;
    }

    @Override
    protected Color getSelectedColor() {
        return Colors.TEXT_COMPONENT_TITLE_SELECTED;
    }
}
