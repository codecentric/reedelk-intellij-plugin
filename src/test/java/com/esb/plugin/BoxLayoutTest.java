package com.esb.plugin;

import javax.swing.*;
import java.awt.*;

public class BoxLayoutTest extends JFrame {

    public BoxLayoutTest() {
        setTitle("Test Box Layout");
        setSize(new Dimension(500, 500));

        JPanel secondNested = new JPanel();
        secondNested.setBackground(Color.GREEN);
        secondNested.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        secondNested.add(new JButton("super"), constraints);


        JPanel center = new JPanel();
        center.setBackground(Color.BLUE);
        center.add(new JButton("Hello2"));

        JPanel panel = new JPanel();
        panel.setBackground(Color.GREEN);
        panel.setLayout(new BorderLayout());
        panel.add(secondNested, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);


        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String args[]) {
        BoxLayoutTest test = new BoxLayoutTest();

    }
}
