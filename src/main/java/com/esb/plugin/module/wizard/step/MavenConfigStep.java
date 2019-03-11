package com.esb.plugin.module.wizard.step;

import com.esb.plugin.module.ESBModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;

import javax.swing.*;

public class MavenConfigStep extends ModuleWizardStep {

    private final ESBModuleBuilder moduleBuilder;
    private final WizardContext context;

    private JPanel jPanel;
    private JTextField groupIdTextField;
    private JTextField artifactIdTextField;
    private JTextField versionTextField;

    public MavenConfigStep(WizardContext context, ESBModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;
        this.context = context;
    }

    @Override
    public JComponent getComponent() {
        return jPanel;
    }

    @Override
    public void updateDataModel() {
        String groupId = groupIdTextField.getText();
        moduleBuilder.setGroupId(groupId);

        String artifactId = artifactIdTextField.getText();
        moduleBuilder.setArtifactId(artifactId);

        String version = versionTextField.getText();
        moduleBuilder.setVersion(version);
    }
}
