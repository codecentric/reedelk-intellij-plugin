package com.reedelk.plugin.editor.properties.widget.input.script;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public interface InputFieldAdapter {

    Object getValue();

    JComponent getComponent();

    void setFont(Font font);

    void setValue(Object value);

    void setMargin(Insets insets);

    void setBorder(Border border);

    void addListener(DynamicValueField.OnChangeListener listener);
}
