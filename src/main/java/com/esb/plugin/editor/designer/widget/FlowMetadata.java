package com.esb.plugin.editor.designer.widget;

import com.esb.internal.commons.StringUtils;
import com.esb.plugin.commons.Fonts;
import com.esb.plugin.commons.Half;
import com.esb.plugin.editor.designer.AbstractGraphNode;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class FlowMetadata {

    private static final int LEFT_PADDING = 20;
    private static final int TITLE_BOTTOM_PADDING = 5;

    private final FlowDescription flowDescription;
    private final FlowSnapshot snapshot;
    private final FlowTitle flowTitle;
    private final int top;

    public FlowMetadata(FlowSnapshot snapshot, int top) {
        this.flowDescription = new FlowDescription();
        this.flowTitle = new FlowTitle();
        this.snapshot = snapshot;
        this.top = top;
    }

    public void draw(Graphics2D graphics) {
        int width = AbstractGraphNode.WIDTH;

        flowTitle.setPosition(LEFT_PADDING + width, top);
        flowTitle.draw(graphics);

        flowDescription.setPosition(LEFT_PADDING + width,
                top + TITLE_BOTTOM_PADDING + Half.of(flowTitle.height(graphics)));
        flowDescription.draw(graphics);
    }

    class FlowTitle extends AbstractText {

        private FlowTitle() {
            super(Fonts.Flow.TITLE, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);
        }

        @Override
        protected Color getColor() {
            return JBColor.GRAY;
        }

        @Override
        protected List<String> getText() {
            String flowTitle = snapshot.getGraph().title();
            if (StringUtils.isNotBlank(flowTitle)) {
                return Collections.singletonList(flowTitle);
            } else {
                return Collections.emptyList();
            }
        }
    }

    class FlowDescription extends AbstractText {

        private FlowDescription() {
            super(Fonts.Flow.DESCRIPTION, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);
        }

        @Override
        protected Color getColor() {
            return JBColor.GRAY;
        }

        @Override
        protected List<String> getText() {
            String description = snapshot.getGraph().description();
            if (StringUtils.isNotBlank(description)) {
                return Collections.singletonList(description);
            } else {
                return Collections.emptyList();
            }
        }
    }
}
