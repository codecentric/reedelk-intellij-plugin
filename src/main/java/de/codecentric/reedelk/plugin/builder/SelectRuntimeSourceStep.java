package de.codecentric.reedelk.plugin.builder;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SelectRuntimeSourceStep extends ModuleWizardStep implements ItemListener {

    private final ModuleBuilder moduleBuilder;
    private JPanel jPanel;
    private JRadioButton downloadLatestRuntimeRadioButton;
    private JRadioButton useRuntimeFromFileRadioButton;
    private JPanel titlePanel;

    public SelectRuntimeSourceStep(ModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;
        downloadLatestRuntimeRadioButton.addItemListener(this);
        useRuntimeFromFileRadioButton.addItemListener(this);
        this.moduleBuilder.setDownloadDistribution(true); // By default we download distribution.
    }

    @Override
    public JComponent getComponent() {
        return jPanel;
    }

    @Override
    public void updateDataModel() {

    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        // We can only select one of the two options
        if (event.getItem() == downloadLatestRuntimeRadioButton && downloadLatestRuntimeRadioButton.isSelected()) {
            useRuntimeFromFileRadioButton.setSelected(false);
            moduleBuilder.setDownloadDistribution(true);
        } else if (event.getItem() == useRuntimeFromFileRadioButton && useRuntimeFromFileRadioButton.isSelected()){
            downloadLatestRuntimeRadioButton.setSelected(false);
            moduleBuilder.setDownloadDistribution(false);
        }
    }
}
