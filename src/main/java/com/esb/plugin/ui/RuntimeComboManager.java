package com.esb.plugin.ui;

import com.esb.plugin.module.wizard.step.ConfigureRuntimeStep;
import com.esb.plugin.service.application.runtime.ESBRuntimeService;

import javax.swing.*;

import static com.esb.plugin.module.wizard.step.ConfigureRuntimeStep.*;

public class RuntimeCombo {

    public JComboBox<RuntimeItem> runtimeCombo;

    public RuntimeCombo(JComboBox<RuntimeItem> comboBox, ESBRuntimeService) {

    }


}
