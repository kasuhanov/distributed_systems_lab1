package ru.kasuhanov.server;

import org.json.JSONException;
import org.json.JSONObject;
import ru.kasuhanov.util.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            JSONObject response = new JSONObject();
            while(!socket.isClosed()) {
                boolean message = false;
                JSONObject request;
                        try {
                            String resp = in.readLine();
                            request = new JSONObject(resp);
                        } catch (JSONException e){
                            request =  new JSONObject();
                        }
                System.out.println("Client request : " + request);
                if( request.getString("status").equals(Status.selectUser.name())){
                    if(!users.contains(request.getString("user"))){
                        users.add(request.getString("user"));
                        System.out.println("user added : " + request.getString("user"));
                        response.put("status",Status.LOGIN_OK);
                    }else{
                        System.out.println("user already exists : " + request.getString("user"));
                        response.put("status",Status.LOGIN_NOK);
                    }
                }
                if( request.getString("status").equals(Status.getRooms.name())){
                    response.put("status", Status.ROOMS);
                    response.put("rooms", getRooms());
                }
                if( request.getString("status").equals(Status.addRoom.name())){
                    ChatRoom room = new ChatRoom();
                    room.setName(request.getString("room"));
                    if(!rooms.contains(room)){
                        rooms.add(room);
                        response.put("status", Status.ROOM_ADD_OK);
                        response.put("room", room.getName());
                    } else {
                        System.out.println("room already exists : " + request.getString("room"));
                        response.put("status",Status.ROOM_ADD_NOK);
                    }
                }
                if( request.getString("status").equals(Status.joinRoom.name())){
                    ChatRoom room = new ChatRoom();
                    room.setName(request.getString("room"));
                    if(rooms.contains(room)){
                        ChatRoom chatRoom = rooms.get(rooms.lastIndexOf(room));
                        chatRoom.getUsers().put(request.getString("user"), socket);
                        System.out.println(chatRoom);
                        final JSONObject finalRequest2 = request;
                        chatRoom.getUsers().forEach((u, socket1) -> {
                            try {
                                PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
                                response.put("status", Status.MESSAGE);
                                response.put("room", chatRoom.getName());
                                response.put("message", "User "+ finalRequest2.getString("user")+" has joined чатик..");
                                response.put("user", "System");
                                out1.println(response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        response.put("status", Status.JOIN_OK);
                        response.put("room", chatRoom.getName());
                    } else {
                        System.out.println("no such room : " + request.getString("room"));
                        response.put("status",Status.JOIN_NOK);
                    }
                }
                if( request.getString("status").equals(Status.messasge.name())){
                    message = true;
                    ChatRoom room = new ChatRoom();
                    room.setName(request.getString("room"));
                    if(rooms.contains(room)){
                        ChatRoom chatRoom = rooms.get(rooms.lastIndexOf(room));
                        final JSONObject finalRequest1 = request;
                        chatRoom.getUsers().forEach((u, socket1) -> {
                            try {
                                PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
                                response.put("status", Status.MESSAGE);
                                response.put("room", chatRoom.getName());
                                response.put("message", finalRequest1.getString("message"));
                                response.put("user", finalRequest1.getString("user"));
                                out1.println(response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
                if( request.getString("status").equals(Status.disconnect.name())){
                    if(request.has("user")){
                        final JSONObject finalRequest = request;
                        rooms.forEach( chatRoom -> {
                            if(chatRoom.getUsers().containsKey(finalRequest.getString("user"))){
                                chatRoom.getUsers().forEach((u, socket1) -> {
                                    try {
                                        PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
                                        response.put("status", Status.MESSAGE);
                                        response.put("room", chatRoom.getName());
                                        response.put("message", "User "+finalRequest.getString("user")+" has left чатик..");
                                        response.put("user", "System");
                                        out1.println(response);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                chatRoom.getUsers().remove(finalRequest.getString("user"));
                            }
                        });
                        users.remove(request.getString("user"));
                    }
                    socket.close();
                    return;
                }
                if(!message)out.println(response.toString());
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
