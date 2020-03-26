package com.reedelk.plugin.editor.properties.commons;

import com.intellij.util.ui.JBUI;

import java.awt.*;

public class FormBuilder {

    private GridBagConstraints lastConstraints;
    private GridBagConstraints labelConstraints;
    private GridBagConstraints labelTopConstraints;

    private FormBuilder() {
        // Set up the constraints for the "last" field in each
        // row first, then copy and modify those constraints.

        // weightx is 1.0 for fields, 0.0 for labels
        // gridwidth is REMAINDER for fields, 1 for labels
        lastConstraints = new GridBagConstraints();

        // Stretch components horizontally (but not vertically)
        lastConstraints.fill = GridBagConstraints.HORIZONTAL;

        // Components that are too short or narrow for their space
        // Should be pinned to the northwest (upper left) corner
        lastConstraints.anchor = GridBagConstraints.CENTER;

        // Give the "last" component as much space as possible
        lastConstraints.weightx = 1.0;

        // Give the "last" component the remainder of the row
        lastConstraints.gridwidth = GridBagConstraints.REMAINDER;

        // Add a little padding
        lastConstraints.insets = JBUI.insets(1, 1, 1, 1);

        // And finally the "label" constrains, typically to be
        // used for the first component on each row
        labelConstraints = (GridBagConstraints) lastConstraints.clone();

        // Give these as little space as necessary
        labelConstraints.weightx = 0.0;
        labelConstraints.gridwidth = 1;

        labelTopConstraints = (GridBagConstraints) labelConstraints.clone();
        labelTopConstraints.anchor = GridBagConstraints.NORTH;
    }

    public static FormBuilder get() {
        return new FormBuilder();
    }

    /**
     * Adds a field component. Any component may be used. The
     * component will be stretched to take the remainder of
     * the current row.
     */
    public FormBuilder addLastField(Component component, Container parent) {
        GridBagLayout gbl = (GridBagLayout) parent.getLayout();
        gbl.setConstraints(component, lastConstraints);
        parent.add(component);
        return this;
    }

    /**
     * Adds an arbitrary label component, starting a new row
     * if appropriate. The width of the component will be set
     * to the minimum width of the widest component on the
     * form.
     */
    public FormBuilder addLabelTop(PropertyTitleLabel label, Container parent) {
        GridBagLayout gbl = (GridBagLayout) parent.getLayout();
        gbl.setConstraints(label, labelTopConstraints);
        parent.add(label);
        return this;
    }

    /**
     * Adds an arbitrary label component, starting a new row
     * if appropriate. The width of the component will be set
     * to the minimum width of the widest component on the
     * form.
     */
    public FormBuilder addLabel(PropertyTitleLabel label, Container parent) {
        GridBagLayout gbl = (GridBagLayout) parent.getLayout();
        gbl.setConstraints(label, labelConstraints);
        parent.add(label);
        return this;
    }

    /**
     * Adds a JLabel with the given string to the label column
     */
    public FormBuilder addLabel(String propertyTitle, Container parent) {
        PropertyTitleLabel label = new PropertyTitleLabel(propertyTitle);
        addLabel(label, parent);
        return this;
    }
}
