package ru.kasuhanov.task1;

import ru.kasuhanov.util.ClientState;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Ui extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea textArea;
    private JTextField textField;
    private JComboBox comboBox;
    private JButton buttonAdd;
    private UiClient client;

    public Ui() {
        //TODO:add scrollbar to textarea
        //TODO:submit empty string
        client = new UiClient(textArea,textField);
        setContentPane(contentPane);
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        buttonAdd.addActionListener(e -> onAdd());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
    }

    private void onOK() {
        switch (client.getClientState()){
            case NOT_LOGINED:
                if(client.onSubmitClick() == ClientState.LOGINED){
                    textField.setVisible(false);
                    client.loadRooms();
                    buttonAdd.setVisible(true);
                    comboBox.setVisible(true);
                }
                break;
            case LOGINED:
                break;
        }
    }

    private void onAdd(){
        JDialog dialog = new JDialog();
        String result = JOptionPane.showInputDialog(dialog, "Enter room name:");
        dialog.dispose();
        client.addRoom(result);
    }

    private void onCancel() {dispose();}

    public static void main(String[] args) {
        Ui frame = new Ui();
        frame.pack();
        frame.setVisible(true);
    }
}