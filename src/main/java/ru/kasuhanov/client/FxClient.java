package ru.kasuhanov.client;

import org.json.JSONObject;
import ru.kasuhanov.util.ClientState;
import ru.kasuhanov.util.Status;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static ru.kasuhanov.util.ClientState.LOGINED;
import static ru.kasuhanov.util.ClientState.NOT_LOGINED;

public class FxClient extends Thread {
    private static String rooms = null;
    private Socket socket;
    private ClientState clientState = NOT_LOGINED;
    private DataOutputStream out;
    private DataInputStream in;
    private InputStream is;
    private OutputStream os;
    private boolean open = true;
    private String user;

    public FxClient(){
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    @Override
    public void run() {
        try {
            int serverPort = 6666;
            //textArea.setText("client is started\n");
            socket = new Socket(InetAddress.getLocalHost(), serverPort);
            is = socket.getInputStream();
            os = socket.getOutputStream();
            in = new DataInputStream(is);
            out = new DataOutputStream(os);
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

    public boolean selectUsername(String username) throws IOException {
        JSONObject request = new JSONObject();
        request.put("status", Status.selectUser);
        request.put("user", username);
        out.writeUTF(request.toString());
        out.flush();
        JSONObject response = new JSONObject(in.readUTF());
        if(response.getString("status").equals(Status.OK.name())){
            user = username;
            clientState = LOGINED;
            return true;
        }else{
            return false;
        }
    }

    public List<String> loadRooms() {
        try {
            JSONObject request = new JSONObject();
            request.put("status", Status.getRooms);
            out.writeUTF(request.toString());
            out.flush();
            JSONObject response = new JSONObject(in.readUTF());
            if(response.getString("status").equals(Status.OK.name())){
                List<String> rooms = new ArrayList<>();
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

    public boolean addRoom(String roomName){
        try {
            JSONObject request = new JSONObject();
            request.put("status", Status.addRoom);
            request.put("room", roomName);
            request.put("user", user);
            out.writeUTF(request.toString());
            out.flush();
            JSONObject response = new JSONObject(in.readUTF());
            return response.getString("status").equals(Status.OK.name());
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public ClientState getClientState() {
        return clientState;
    }

    public String getUser() {
        return user;
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
