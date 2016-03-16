package ru.kasuhanov.task1;

import org.json.JSONObject;
import ru.kasuhanov.util.ClientState;
import ru.kasuhanov.util.Status;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

import static ru.kasuhanov.util.ClientState.LOGINED;
import static ru.kasuhanov.util.ClientState.NOT_LOGINED;

public class UiClient extends Thread {
    private static String rooms = null;
    private JTextArea textArea;
    private JTextField textField;
    private Socket socket;
    private ClientState clientState = NOT_LOGINED;
    private DataOutputStream out;
    private DataInputStream in;
    private InputStream is;
    private OutputStream os;
    private boolean open = true;
    private String user;

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
            is = socket.getInputStream();
            os = socket.getOutputStream();
            in = new DataInputStream(is);
            out = new DataOutputStream(os);
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

    public void selectUsername(String username) throws IOException {
        JSONObject request = new JSONObject();
        request.put("status", Status.selectUser);
        request.put("user", username);
        out.writeUTF(request.toString());
        out.flush();
        JSONObject response = new JSONObject(in.readUTF());
        if(response.getString("status").equals(Status.OK.name())){
            textArea.setText("logged in as "+username+"\n");
            user = username;
            clientState = LOGINED;
        }else{
            textArea.append("This username already in use..\n");
            textArea.append("try again..\n");
        }
    }

    public Vector<String> loadRooms() {
        try {
            JSONObject request = new JSONObject();
            request.put("status", Status.getRooms);
            out.writeUTF(request.toString());
            out.flush();
            JSONObject response = new JSONObject(in.readUTF());
            if(response.getString("status").equals(Status.OK.name())){
                Vector<String> rooms = new Vector<>();
                for (Object o: response.getJSONArray("rooms")) {
                    rooms.add((String) o);
                }
                return rooms;
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
            request.put("user", user);
            out.writeUTF(request.toString());
            out.flush();
            JSONObject response = new JSONObject(in.readUTF());
            if(response.getString("status").equals(Status.OK.name())){
                textArea.append("room "+response.getString("room")+" created\n");
            }else{
                textArea.append("Room with this name already exists..\n");
                textArea.append("try again..\n");
            }
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
            System.out.println("fgsd");
            // TODO: 16.03.2016 close socket properly
            in.close();
            out.close();
            is.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Could not close socket");
            System.exit(-1);
        }
    }
}
