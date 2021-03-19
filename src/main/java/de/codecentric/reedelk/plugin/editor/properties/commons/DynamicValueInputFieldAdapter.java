package de.codecentric.reedelk.plugin.editor.properties.commons;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public interface DynamicValueInputFieldAdapter {

    Object getValue();

    JComponent getComponent();

    void setFont(Font font);

    void setValue(Object value);

    void setMargin(Insets insets);

    void setBorder(Border border);

    void addListener(ScriptEditorChangeListener listener);
}
