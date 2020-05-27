package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.application.ApplicationManager;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.TooltipContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.ArrowDown;
import static com.intellij.icons.AllIcons.General.ArrowRight;
import static com.reedelk.plugin.editor.properties.commons.PanelWithText.LoadingContentPanel;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class DisposableCollapsiblePane extends DisposablePanel {

    private final String displayName;
    private final boolean horizontalBar;
    private final DisposablePanel collapsedContent;
    private final transient TooltipContent tooltipContent;
    private final transient ContentRenderer renderingFunction;
    private ClickableLabel.IconAlignment iconAlignment;

    private DisposablePanel unCollapsedContent;

    private boolean collapsed;
    private boolean loaded = false;

    public DisposableCollapsiblePane(@NotNull String displayName,
                                     @NotNull ContentRenderer unCollapsedContentRenderer) {
        this(displayName, unCollapsedContentRenderer, null, false, true, ClickableLabel.IconAlignment.LEFT);
    }

    public DisposableCollapsiblePane(@NotNull String displayName,
                                     @NotNull ContentRenderer unCollapsedContentRenderer,
                                     boolean defaultCollapsed) {
        this(displayName, unCollapsedContentRenderer, null, defaultCollapsed, true, ClickableLabel.IconAlignment.LEFT);
    }

    public DisposableCollapsiblePane(@NotNull String displayName,
                                     @NotNull ContentRenderer unCollapsedContentRenderer,
                                     boolean defaultCollapsed,
                                     boolean horizontalBar,
                                     ClickableLabel.IconAlignment iconAlignment) {
        this(displayName, unCollapsedContentRenderer, null, defaultCollapsed, horizontalBar, iconAlignment);
    }

    public DisposableCollapsiblePane(@NotNull String displayName,
                                     @NotNull ContentRenderer unCollapsedContentRenderer,
                                     boolean defaultCollapsed,
                                     boolean horizontalBar) {
        this(displayName, unCollapsedContentRenderer, null, defaultCollapsed, horizontalBar, ClickableLabel.IconAlignment.LEFT);
    }

    public DisposableCollapsiblePane(@NotNull String displayName,
                                     @NotNull ContentRenderer unCollapsedContentRenderer,
                                     @Nullable TooltipContent tooltipContent) {
        this(displayName, unCollapsedContentRenderer, tooltipContent, true, true, ClickableLabel.IconAlignment.LEFT);
    }

    public DisposableCollapsiblePane(@NotNull String displayName,
                                     @NotNull ContentRenderer unCollapsedContentRenderer,
                                     @Nullable TooltipContent tooltipContent,
                                     boolean defaultCollapsed,
                                     boolean horizontalBar,
                                     ClickableLabel.IconAlignment iconAlignment) {
        this.iconAlignment = iconAlignment;
        this.displayName = displayName;
        this.collapsed = defaultCollapsed;
        this.horizontalBar = horizontalBar;
        this.tooltipContent = tooltipContent;
        this.renderingFunction = unCollapsedContentRenderer;
        this.collapsedContent = new CollapsedContent(displayName, tooltipContent, horizontalBar);
        this.unCollapsedContent = new UnCollapsedContent(displayName, tooltipContent, new LoadingContentPanel(), horizontalBar);

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(DefaultObjectTypeContainer.BORDER_OBJECT_TYPE_CONTAINER_TOP);

        if (defaultCollapsed) {
            add(collapsedContent, CENTER);
        } else {
            add(unCollapsedContent, CENTER);
            lazyLoadUnCollapsedContent();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(collapsedContent);
        DisposableUtils.dispose(unCollapsedContent);
    }

    private void collapse() {
        ApplicationManager.getApplication().invokeLater(() -> {
            remove(unCollapsedContent);
            add(collapsedContent);
            revalidate();
            repaint();
        });
    }

    private void unCollapse() {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (loaded) {
                removeAll();
                add(unCollapsedContent);
                revalidate();
                repaint();
            } else {
                lazyLoadUnCollapsedContent();
            }
        });
    }

    /**
     * Loads the content of the unCollapsed Panel by using the provided rendering
     * function. This is needed to improve rendering performances when component's
     * properties are displayed in the Properties Tool Window.
     */
    private void lazyLoadUnCollapsedContent() {
        ApplicationManager.getApplication().invokeLater(() -> {
            removeAll();
            JComponent renderedContent = renderingFunction.render();
            loaded = true;
            unCollapsedContent = new UnCollapsedContent(displayName, tooltipContent, renderedContent, horizontalBar);
            add(unCollapsedContent);
            revalidate();
            repaint();
        });
    }

    class UnCollapsedContent extends DisposablePanel {
        UnCollapsedContent(String displayName, TooltipContent tooltipContent, JComponent content, boolean horizontalBar) {
            // header
            TypeObjectContainerHeader topHeader =
                    new TypeObjectContainerHeader(displayName, tooltipContent, ArrowDown, clickAction, horizontalBar, iconAlignment);
            setLayout(new BorderLayout());
            setOpaque(false);
            add(topHeader, NORTH);
            add(content, CENTER);
        }
    }

    class CollapsedContent extends TypeObjectContainerHeader {
        CollapsedContent(String displayName, TooltipContent tooltipContent, boolean horizontalBar) {
            super(displayName, tooltipContent, ArrowRight, clickAction, horizontalBar, iconAlignment);
        }
    }

    private final transient ClickableLabel.OnClickAction clickAction = () -> {
        if (collapsed) {
            unCollapse();
            collapsed = false;
        } else {
            collapse();
            collapsed = true;
        }
    };
}
