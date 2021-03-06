package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ClickableLabel extends JBLabel implements MouseListener {

    private static final Cursor ON_HOVER_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    private transient OnClickAction action;
    private transient OnHoverAction onHover;
    private transient OnExitAction onExit;

    public ClickableLabel(String text) {
        this(text, null, null, IconAlignment.LEFT, null);
    }

    public ClickableLabel(Icon icon) {
        this(StringUtils.EMPTY, icon, null, IconAlignment.LEFT, null);
    }

    public ClickableLabel(Icon icon, OnClickAction action) {
        this(StringUtils.EMPTY, icon, null, IconAlignment.LEFT, action);
    }

    public ClickableLabel(String text, Icon enabledIcon, Icon disabledIcon) {
        this(text, enabledIcon, disabledIcon, IconAlignment.LEFT, null);
    }

    public ClickableLabel(Icon icon, Icon disabledIcon, OnClickAction action) {
        this(StringUtils.EMPTY, icon, disabledIcon, IconAlignment.LEFT, action);
    }

    public ClickableLabel(String text, Icon icon, OnClickAction action) {
        this(text, icon, null, IconAlignment.LEFT, action);
    }

    public ClickableLabel(String text, Icon icon, Icon disabledIcon, OnClickAction action) {
        this(text, icon, disabledIcon, IconAlignment.LEFT, action);
    }

    public ClickableLabel(String text, Icon icon, IconAlignment iconAlignment, OnClickAction action) {
        this(text, icon, null, iconAlignment, action);
    }

    private ClickableLabel(String text, Icon icon, Icon disabledIcon, IconAlignment iconAlignment, OnClickAction action) {
        setText(text);
        setIcon(icon);
        addMouseListener(this);
        setCursor(ON_HOVER_CURSOR);
        this.action = action;
        if (disabledIcon != null) {
            setDisabledIcon(disabledIcon);
        }
        if (IconAlignment.LEFT.equals(iconAlignment)) {
            setHorizontalTextPosition(SwingConstants.RIGHT); // Icon pushed to the left
        } else {
            setHorizontalTextPosition(SwingConstants.LEFT); // Icon pushed to the right
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (action != null) {
            action.onClick();
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (onHover != null) {
            onHover.onHover();
        }
    }

    @Override
    public void mouseExited(MouseEvent event) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        if (onExit != null) {
            onExit.onExit();
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        // not used
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        // not used
    }

    public void setAction(OnClickAction action) {
        this.action = action;
    }

    public void setOnHover(OnHoverAction onHover) {
        this.onHover = onHover;
    }

    public void setOnExit(OnExitAction onExit) {
        this.onExit = onExit;
    }

    public interface OnClickAction {
        void onClick();
    }

    public interface OnHoverAction {
        void onHover();
    }

    public interface OnExitAction {
        void onExit();
    }

    public enum IconAlignment {
        LEFT,
        RIGHT
    }
}
