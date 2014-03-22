package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class MyDialog extends JDialog {
    JLabel labelName;
    JLabel labelSex;
    JTextField textfield;
    JComboBox<String> combobox;
    JButton okButton;
    JButton cancelButton;
    
    MyDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        
        labelName = new JLabel("Enter your name:");
        labelSex = new JLabel("Choose your sex:");
        textfield = new JTextField();
        combobox = new JComboBox<String>();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        
        combobox.addItem("M");
        combobox.addItem("F");
        
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (textfield.getText().length() > 0) {
                    JOptionPane.showMessageDialog(null,
                        textfield.getText() + ", " + combobox.getSelectedItem());
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null,
                        "You didn't enter your name.");
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        
        setLayout(new GridLayout(3, 2));
        
        add(labelName);
        add(textfield);
        add(labelSex);
        add(combobox);
        add(okButton);
        add(cancelButton);
        
        pack();
    }
} 
