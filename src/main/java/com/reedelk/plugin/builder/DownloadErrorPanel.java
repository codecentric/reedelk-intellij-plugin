package com.reedelk.plugin.builder;

import javax.swing.*;

public class DownloadErrorPanel {

    private JPanel errorPanel;
    private JLabel errorTitleLabel;
    private JLabel errorMessageLabel;

    public JPanel content() {
        return errorPanel;
    }

    public void setTitle(String errorTitle) {
        errorTitleLabel.setText(errorTitle);
    }

    public void setMessage(String errorMessage) {
        errorMessageLabel.setText(errorMessage);
    }
}
