package com.esb.plugin.runconfig.module;

import com.intellij.application.options.ModuleDescriptionsComboBox;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.UI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.uiDesigner.core.GridConstraints.*;

public class ESBModuleRunConfigurationSettings extends SettingsEditor<ESBModuleRunConfiguration> {

    private ConfigurationModuleSelector myModuleSelector;


    // Make Module Selectable
    // Make Runtime Selectable
    private JPanel jPanel;



    public ESBModuleRunConfigurationSettings(@NotNull Project project) {

        ModuleDescriptionsComboBox myModuleCombo = new ModuleDescriptionsComboBox();
        myModuleCombo.setAllModulesFromProject(project);

        myModuleSelector = new ConfigurationModuleSelector(project, myModuleCombo);

        JPanel moduleChooserPanel = UI.PanelFactory.panel(myModuleCombo).withLabel("Module:").
                withComment("Choose the ESB Module this run configuration will be applied to")
                .createPanel();

        jPanel.add(moduleChooserPanel, MODULE_SELECTOR);
    }

    private static final GridConstraints MODULE_SELECTOR  =
            new GridConstraints(0, 0, 1, 1,
                    ANCHOR_WEST,
                    FILL_HORIZONTAL,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    new Dimension(-1, -1),
                    new Dimension(-1, -1),
                    new Dimension(-1, -1));


    @Override
    protected void resetEditorFrom(@NotNull ESBModuleRunConfiguration configuration) {
        myModuleSelector.reset(configuration);

    }

    @Override
    protected void applyEditorTo(@NotNull ESBModuleRunConfiguration configuration) throws ConfigurationException {
        myModuleSelector.applyTo(configuration);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return jPanel;
    }
}
