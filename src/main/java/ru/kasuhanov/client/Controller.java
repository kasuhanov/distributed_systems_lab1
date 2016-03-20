package ru.kasuhanov.client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import ru.kasuhanov.util.ClientState;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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
    public Button userListBtn;
    public Button quitBtn;
    private FxClient client;
    private String room;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = new FxClient(this);
        textArea.setText("client is started\n");
        textField.setDisable(true);
        joinBtn.setDisable(true);
        addBtn.setDisable(true);
        roomsBox.setDisable(true);
        okBtn.setDisable(true);
    }

    public void onLogClick(ActionEvent actionEvent) {
        if(client.getClientState()== ClientState.NOT_LOGINED){
            try {
                client.selectUsername(loginDialog());
                client.loadRooms();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void roomsCallback(List<String> rooms){
        roomsBox.setItems(FXCollections.observableArrayList(rooms));
        //roomsBox.getSelectionModel().selectFirst();
    }

    public void loginCallback(boolean resp){
        if (resp){
            logBtn.setVisible(false);
            joinBtn.setDisable(false);
            addBtn.setDisable(false);
            roomsBox.setDisable(false);
            quitBtn.setVisible(false);
            textArea.appendText("logged in as "+client.getUser()+"\n");
        } else {
            textArea.appendText("failed to login. Username already in use"+"\n");
        }
    }

    public void addRoomCallback(boolean resp){
        if (resp){
            textArea.appendText("Room created"+"\n");
            client.loadRooms();
        } else {
            textArea.appendText("failed to create room.."+"\n");
        }
    }

    public void joinCallback(String room){
        this.room = room;
        textArea.setText("joined room "+room+"\n");
        roomsBox.setDisable(true);
        addBtn.setDisable(true);
        joinBtn.setDisable(true);
        textField.setDisable(false);
        okBtn.setDisable(false);
    }

    public void messageCallback(String message, String user){
        textArea.appendText(user + ": " + message + "\n");
    }

    public void onOkClick(ActionEvent actionEvent) {
        client.sendMessage(textField.getText(),room);
        textField.setText("");
    }

    public void onAddClick(ActionEvent actionEvent) {
        client.addRoom(roomDialog());
    }

    public void onJoinClick(ActionEvent actionEvent) {
        String roomname = String.valueOf(roomsBox.getSelectionModel().getSelectedItem());
        client.joinRoom(roomname);
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

    public void onGetUsersClick(ActionEvent actionEvent) {
        client.getUsers(room);
    }

    public void onQuitClick(ActionEvent actionEvent) {
        client.quit(room);
    }
}
