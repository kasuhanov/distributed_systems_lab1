package ru.kasuhanov.task1;

import org.json.JSONObject;
import ru.kasuhanov.util.Status;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    private static String rooms = null;
    private static Socket socket = null;
    public static void main(String... args){
       new Client();
    }
    public Client() {
        run();
    }
    public void run(){
        try {
            int serverPort = 6666;
            System.out.println("client is started");
            socket = new Socket(InetAddress.getLocalHost(), serverPort);
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                while (!selectUsername(keyboard, out,in)){}
                System.out.println("succsess logged in");
                while (!selectRoom(keyboard, out,in)){}
                keyboard.readLine();
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
    public static boolean selectUsername(BufferedReader keyboard, DataOutputStream out, DataInputStream in) throws IOException {
        System.out.println("Type ur username:");
        String line = keyboard.readLine();
        JSONObject request = new JSONObject();
        request.put("status", Status.selectUser);
        request.put("user",line);
        out.writeUTF(request.toString());
        out.flush();
        JSONObject response = new JSONObject(in.readUTF());
        if(response.getString("status").equals(Status.OK.name())){
            rooms = line.substring(1);
            return true;
        }else{
            System.out.println("This username already in use..");
            return false;
        }
    }
    public static boolean selectRoom(BufferedReader keyboard, DataOutputStream out, DataInputStream in) throws IOException {
        System.out.println("select room - 1, create room - 2, disconnect - 3");
        System.out.println("List of rooms:");
        System.out.println(rooms);
        String line = keyboard.readLine();
        out.writeUTF("0"+line);
        out.flush();
        line = in.readUTF();
        System.out.println(line);
        return false;
    }
    public void finalize(){
        try{
            socket.close();
        } catch (IOException e) {
            System.out.println("Could not close socket");
            System.exit(-1);
        }
    }
}