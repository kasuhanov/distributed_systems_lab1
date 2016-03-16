package ru.kasuhanov.task1;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.kasuhanov.util.ClientState;
import ru.kasuhanov.util.Status;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static ru.kasuhanov.util.ClientState.*;

public class UiClient extends Thread {
    private static String rooms = null;
    private JTextArea textArea;
    private JTextField textField;
    private Socket socket;
    private ClientState clientState = NOT_LOGINED;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean open = true;

    public UiClient(JTextArea textArea, JTextField textField){
        this.textArea = textArea;
        this.textField = textField;
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    @Override
    public void run() {
        try {
            int serverPort = 6666;
            textArea.append("client is started\n");
            socket = new Socket(InetAddress.getLocalHost(), serverPort);
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();
            in = new DataInputStream(sin);
            out = new DataOutputStream(sout);
            textArea.append("Type ur username:\n");
            while(open);
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if(socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ClientState onSubmitClick(){
        switch (clientState){
            case NOT_LOGINED:
                try {
                    selectUsername(textField.getText());
                    textField.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case LOGINED:break;
        }
        return clientState;
    }

    public  boolean selectUsername(String username) throws IOException {
        JSONObject request = new JSONObject();
        request.put("status", Status.selectUser);
        request.put("user", username);
        out.writeUTF(request.toString());
        out.flush();
        JSONObject response = new JSONObject(in.readUTF());
        if(response.getString("status").equals(Status.OK.name())){
            textArea.setText("logged in as "+username+"\n");
            clientState = LOGINED;
            return true;
        }else{
            textArea.append("This username already in use..\n");
            textArea.append("try again..\n");
            return false;
        }
    }

    public JSONArray loadRooms() {
        try {
            JSONObject request = new JSONObject();
            request.put("status", Status.getRooms);
            out.writeUTF(request.toString());
            out.flush();
            JSONObject response = new JSONObject(in.readUTF());
            if(response.getString("status").equals(Status.OK.name())){
                textArea.append("rooms: "+response.getJSONArray("rooms")+"\n");
                return response.getJSONArray("rooms");
            }else{
                throw new RuntimeException("invalid server response");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void addRoom(String roomName){
        try {
            JSONObject request = new JSONObject();
            request.put("status", Status.addRoom);
            request.put("room", roomName);
            out.writeUTF(request.toString());
            out.flush();
            JSONObject response = new JSONObject(in.readUTF());
            // TODO: 16.03.2016 add some logic
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public ClientState getClientState() {
        return clientState;
    }

    @Override
    public void finalize() throws Throwable{
        super.finalize();
        try{
            socket.close();
        } catch (IOException e) {
            System.out.println("Could not close socket");
            System.exit(-1);
        }
    }
}
