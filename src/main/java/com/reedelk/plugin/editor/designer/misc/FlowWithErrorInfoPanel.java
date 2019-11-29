package com.reedelk.plugin.editor.designer.misc;

import com.reedelk.plugin.commons.*;
import com.reedelk.plugin.editor.designer.text.AbstractText;
import com.reedelk.plugin.graph.FlowGraph;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class FlowWithErrorInfoPanel extends BaseInfoPanel {

    public FlowWithErrorInfoPanel() {
        super(message("flow.error.title"), Images.Flow.Error);
    }

    @Override
    protected void drawAdditionalMessage(@NotNull FlowGraph graph,
                                         @NotNull Graphics2D g2,
                                         @NotNull JComponent parent,
                                         int yCoordinateStart) {
        super.drawAdditionalMessage(graph, g2, parent, yCoordinateStart);
        String message = message("flow.error.message", graph.getError().getMessage());
        FlowErrorText flowErrorText = new FlowErrorText(message);
        flowErrorText.setPosition(Half.of(parent.getWidth()), yCoordinateStart);
        flowErrorText.draw(g2);
    }

    static class FlowErrorText extends AbstractText {

        private static final Pattern REGEX = Pattern.compile(".{1,110}(?:\\s|$)", Pattern.DOTALL);
        private final String text;

        FlowErrorText(String text) {
            super(Fonts.Component.DESCRIPTION, HorizontalAlignment.CENTER, VerticalAlignment.BELOW);
            this.text = text;
        }

        @Override
        protected Color getColor() {
            return Colors.PROPERTIES_EMPTY_SELECTION_TEXT;
        }

        @Override
        protected List<String> getText() {
            return SplitTextInLines.from(text, REGEX);
        }
    }
}
