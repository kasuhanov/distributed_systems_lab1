package ru.kasuhanov.task1;

import javax.swing.*;
import java.awt.event.*;

public class Ui extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea textArea;
    private JTextField textField;
    private UiClient client;
    public Ui() {
        client = new UiClient(textArea,textField);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
    }

    private void onOK() {client.submitClick();}

    private void onCancel() {dispose();}

    public static void main(String[] args) {
        Ui dialog = new Ui();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
