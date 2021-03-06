package de.codecentric.reedelk.plugin.editor.designer.text;

import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.commons.Colors;
import de.codecentric.reedelk.plugin.commons.Fonts;
import de.codecentric.reedelk.plugin.commons.Half;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class FlowMetadata {

    private static final int LEFT_PADDING = 20;
    private static final int TITLE_BOTTOM_PADDING = 5;

    private final FlowDescription flowDescription;
    private final FlowSnapshot snapshot;
    private final FlowTitle flowTitle;

    private final int left;
    private final int top;

    public FlowMetadata(FlowSnapshot snapshot, int top) {
        this(snapshot, top, 0);
    }

    public FlowMetadata(FlowSnapshot snapshot, int top, int left) {
        this.flowDescription = new FlowDescription();
        this.flowTitle = new FlowTitle();
        this.snapshot = snapshot;
        this.left = left;
        this.top = top;
    }

    public void draw(Graphics2D graphics) {
        flowTitle.setPosition(left + LEFT_PADDING, top);
        flowTitle.draw(graphics);

        flowDescription.setPosition(left + LEFT_PADDING,
                top + TITLE_BOTTOM_PADDING + Half.of(flowTitle.height(graphics)));
        flowDescription.draw(graphics);
    }

    class FlowTitle extends AbstractText {

        private FlowTitle() {
            super(Fonts.Flow.TITLE, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);
        }

        @Override
        protected Color getColor() {
            return Colors.TEXT_FLOW_TITLE;
        }

        @Override
        protected List<String> getText() {
            String flowTitle = snapshot.getGraphOrThrowIfAbsent().title();
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
            return Colors.TEXT_FLOW_DESCRIPTION;
        }

        @Override
        protected List<String> getText() {
            String description = snapshot.getGraphOrThrowIfAbsent().description();
            if (StringUtils.isNotBlank(description)) {
                return Collections.singletonList(description);
            } else {
                return Collections.emptyList();
            }
        }
    }
}
