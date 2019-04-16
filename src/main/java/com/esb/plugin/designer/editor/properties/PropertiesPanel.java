package com.esb.plugin.designer.editor.properties;

import com.esb.plugin.designer.editor.SelectListener;
import com.esb.plugin.designer.graph.drawable.Drawable;

import javax.swing.*;

public class PropertiesPanel extends JPanel implements SelectListener {

    private JLabel label;

    public PropertiesPanel() {
        label = new JLabel("Properties Panel");
        add(label);
    }

    @Override
    public void onSelect(Drawable drawable) {
        label.setText(drawable.displayName());
    }

    @Override
    public void onUnselect(Drawable drawable) {
        label.setText("Nothing selected");
    }
}
