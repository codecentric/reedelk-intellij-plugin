package com.esb.plugin.module.wizard.step;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.Disposable;

import javax.swing.*;

public class EmptyStep extends ModuleWizardStep implements Disposable {

    private JPanel jPanel;

    @Override
    public JComponent getComponent() {
        return jPanel;
    }

    @Override
    public void updateDataModel() {

    }

    @Override
    public void dispose() {

    }
}
