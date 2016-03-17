package ru.kasuhanov.client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import ru.kasuhanov.util.ClientState;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    public Button okBtn;
    public TextField textField;
    public AnchorPane anchorPane;
    public TextArea textArea;
    public Button logBtn;
    public ComboBox roomsBox;
    public Button addBtn;
    public Button joinBtn;
    private FxClient client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = new FxClient();
        textArea.setText("client is started\n");
        textField.setDisable(true);
        okBtn.setDisable(true);
    }

    public void onLogClick(ActionEvent actionEvent) {
        if(client.getClientState()== ClientState.NOT_LOGINED){
            try {
                if (client.selectUsername(loginDialog())){
                    logBtn.setVisible(false);
                    textArea.appendText("logged in as "+client.getUser()+"\n");
                    roomsBox.setItems(FXCollections.observableArrayList(client.loadRooms()));
                    roomsBox.getSelectionModel().selectFirst();
                } else {
                    textArea.appendText("failed to login. Username already in use"+"\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onOkClick(ActionEvent actionEvent) {
        //okBtn.setVisible(false);
    }

    public void onAddClick(ActionEvent actionEvent) {
        if (client.addRoom(roomDialog())){
            textArea.appendText("Room created"+"\n");
            roomsBox.setItems( FXCollections.observableArrayList(client.loadRooms()));
            roomsBox.getSelectionModel().selectFirst();
        } else {
            textArea.appendText("failed to create room.."+"\n");
        }
    }

    public void onJoinClick(ActionEvent actionEvent) {
        String roomname = String.valueOf(roomsBox.getSelectionModel().getSelectedItem());
        if(client.joinRoom(roomname)){
            textArea.setText("joined room "+roomname+"\n");
            roomsBox.setDisable(true);
            addBtn.setDisable(true);
            joinBtn.setDisable(true);
            textField.setDisable(false);
            okBtn.setDisable(false);
        }
    }

    public String loginDialog(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Please login");
        dialog.setHeaderText("Please enter your username");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
        Optional<String> result = dialog.showAndWait();
        return result.get();
    }

    public String roomDialog(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New room");
        dialog.setHeaderText("Please new room's name");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
        Optional<String> result = dialog.showAndWait();
        return result.get();
    }

    public void close(){
        client.disconnect();
    }
}
