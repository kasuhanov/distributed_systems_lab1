package ru.kasuhanov.task6;

import org.json.JSONObject;
import ru.kasuhanov.util.Status;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    Socket socket;
    int num;
    List<String> users = new ArrayList<>();
    List<String> rooms = new ArrayList<>();
    public static void main(String... args) {
        ServerSocket server = null;
        try {
            int port = 6666;
            int i = 0;
            server = new ServerSocket(port);
            System.out.println("server is started");
            while (true) {
                new Server(i, server.accept());
                i++;
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
    public Server(int num, Socket socket){
        this.num = num;
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
            while(true) {
                JSONObject request = new JSONObject(in.readUTF());
                System.out.println("Client request : " + request);
                if( request.getString("status").equals(Status.selectUser.name())){
                    if(!users.contains(request.getString("user"))){
                        users.add(request.getString("user"));
                        System.out.println("user added :" + request.getString("user"));
                        response.put("status",Status.OK);
                    }else{
                        System.out.println("user already exists : " + request.getString("user"));
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
}