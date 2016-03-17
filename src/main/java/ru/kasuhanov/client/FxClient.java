package ru.kasuhanov.client;

import org.json.JSONObject;
import ru.kasuhanov.util.ClientState;
import ru.kasuhanov.util.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static ru.kasuhanov.util.ClientState.LOGINED;
import static ru.kasuhanov.util.ClientState.NOT_LOGINED;

public class FxClient extends Thread {
    private Socket socket;
    private ClientState clientState = NOT_LOGINED;
    private PrintWriter out;
    private BufferedReader in;
    private String user;
    private Controller controller;
    private boolean open = true;
    public FxClient(Controller controller){
        this.controller = controller;
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    @Override
    public void run() {
        try {
            int serverPort = 6666;
            socket = new Socket(InetAddress.getLocalHost(), serverPort);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            while (!socket.isClosed()) {
                if(open && in.ready()){
                    JSONObject message =  new JSONObject(in.readLine());
                    System.out.println(message);
                    switch (Status.valueOf(message.getString("status"))){
                        case LOGIN_OK:
                            clientState = LOGINED;
                            controller.loginCallback(true);
                            break;
                        case LOGIN_NOK:
                            controller.loginCallback(false);
                            break;
                        case ROOMS:
                            List<String> rooms = new ArrayList<>();
                            for (Object o: message.getJSONArray("rooms")) {
                                rooms.add((String) o);
                            }
                            controller.roomsCallback(rooms);
                            break;
                        case ROOM_ADD_OK:
                            controller.addRoomCallback(true);
                            break;
                        case ROOM_ADD_NOK:
                            controller.addRoomCallback(false);
                            break;
                        case JOIN_OK:
                            controller.joinCallback(message.getString("room"));
                            break;
                    }
                }

            }
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

    public void selectUsername(String username) throws IOException {
        JSONObject request = new JSONObject();
        request.put("status", Status.selectUser);
        request.put("user", username);
        user = username;
        out.println(request.toString());
    }

    public void loadRooms() {
        JSONObject request = new JSONObject();
        request.put("status", Status.getRooms);
        out.println(request.toString());
    }

    public void joinRoom(String roomName){
        JSONObject request = new JSONObject();
        request.put("status", Status.joinRoom);
        request.put("room", roomName);
        request.put("user", user);
        out.println(request.toString());
    }

    public boolean addRoom(String roomName){
        JSONObject request = new JSONObject();
        request.put("status", Status.addRoom);
        request.put("room", roomName);
        request.put("user", user);
        out.println(request.toString());
        return false;
    }

    public ClientState getClientState() {
        return clientState;
    }

    public String getUser() {
        return user;
    }

    public void disconnect(){
        try {
            JSONObject request = new JSONObject();
            request.put("status", Status.disconnect);
            request.put("user", user);
            open = false;
            out.println(request.toString());
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
