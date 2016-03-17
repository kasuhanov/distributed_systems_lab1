package ru.kasuhanov.server;

import org.json.JSONException;
import org.json.JSONObject;
import ru.kasuhanov.util.Status;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private Socket socket;
    private static List<String> users = new ArrayList<>();
    private static List<ChatRoom> rooms = new ArrayList<>();

    public static void main(String[] args) {
        ServerSocket server = null;
        try {
            int port = 6666;
            server = new ServerSocket(port);
            System.out.println("server is started");
            while (true) {
                new Server(server.accept());
            }
        } catch(Exception e){
            System.out.println("init error: " + e);
        } finally {
            try {
                if (server != null)
                    server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Server(Socket socket){
        this.socket = socket;
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    public void run(){
        try{
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);
            JSONObject response = new JSONObject();
            while(!socket.isClosed()) {
                JSONObject request;
                        try {
                            request = new JSONObject(in.readUTF());
                        } catch (JSONException e){
                            request =  new JSONObject();
                        }
                System.out.println("Client request : " + request);
                if( request.getString("status").equals(Status.selectUser.name())){
                    if(!users.contains(request.getString("user"))){
                        users.add(request.getString("user"));
                        System.out.println("user added : " + request.getString("user"));
                        response.put("status",Status.OK);
                    }else{
                        System.out.println("user already exists : " + request.getString("user"));
                        response.put("status",Status.NOK);
                    }
                }
                if( request.getString("status").equals(Status.getRooms.name())){
                    response.put("status", Status.OK);
                    response.put("rooms", getRooms());
                }
                if( request.getString("status").equals(Status.addRoom.name())){
                    ChatRoom room = new ChatRoom();
                    room.setName(request.getString("room"));
                    if(!rooms.contains(room)){
                        rooms.add(room);
                        response.put("status", Status.OK);
                        response.put("room", room.getName());
                    } else {
                        System.out.println("room already exists : " + request.getString("room"));
                        response.put("status",Status.NOK);
                    }
                }
                out.writeUTF(response.toString());
                out.flush();
                System.out.println("Waiting...");
            }
        } catch(Exception e) {
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

    public List<String> getRooms(){
        List<String> result = new ArrayList<>();
        rooms.forEach(chatRoom -> result.add(chatRoom.getName()));
        return result;
    }
}
