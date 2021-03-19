package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class FormBuilder {

    private GridBagConstraints lastConstraints;
    private GridBagConstraints labelConstraints;
    private GridBagConstraints labelTopConstraints;
    private GridBagConstraints fullWidthAndHeightConstraints;

    private FormBuilder() {

        // Last components
        lastConstraints = new GridBagConstraints();
        lastConstraints.fill = GridBagConstraints.HORIZONTAL; // Stretch components horizontally (but not vertically)
        lastConstraints.anchor = GridBagConstraints.CENTER;
        lastConstraints.weightx = 1.0; // Give the "last" component as much space as possible
        lastConstraints.gridwidth = GridBagConstraints.REMAINDER; // Give the "last" component the remainder of the row
        lastConstraints.insets = JBUI.insets(1, 1, 1, 1); // Add a little padding

        // Full Width and Heights used for example for tables which require to stretch Horizontally but also vertically.
        fullWidthAndHeightConstraints = (GridBagConstraints) lastConstraints.clone();
        fullWidthAndHeightConstraints.fill = GridBagConstraints.BOTH;
        fullWidthAndHeightConstraints.weighty = 1.0;

        // And finally the "label" constrains, typically to be
        // used for the first component on each row
        labelConstraints = (GridBagConstraints) lastConstraints.clone();
        labelConstraints.weightx = 0.0; // Give these as little space as necessary
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

    public FormBuilder addLabel(JLabel label, Container parent) {
        GridBagLayout gbl = (GridBagLayout) parent.getLayout();
        gbl.setConstraints(label, labelConstraints);
        parent.add(label);
        return this;
    }

    public FormBuilder addFullWidthAndHeight(JComponent p, JComponent parent) {
        GridBagLayout gbl = (GridBagLayout) parent.getLayout();
        gbl.setConstraints(p, fullWidthAndHeightConstraints);
        parent.add(p);
        return this;
    }
}
