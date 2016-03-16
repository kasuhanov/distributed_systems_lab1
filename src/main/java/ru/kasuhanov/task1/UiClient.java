package ru.kasuhanov.task1;

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
    private ClientState state = NOT_LOGINED;
    private DataOutputStream out;
    private DataInputStream in;

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

    public void submitClick(){
        switch (state){
            case NOT_LOGINED:
                try {
                    selectUsername("sdf");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case LOGINED:break;
        }
    }

    public  boolean selectUsername(String username) throws IOException {
        JSONObject request = new JSONObject();
        request.put("status", Status.selectUser);
        request.put("user", username);
        out.writeUTF(request.toString());
        out.flush();
        JSONObject response = new JSONObject(in.readUTF());
        if(response.getString("status").equals(Status.OK.name())){
            textArea.append("logged\n");
            return true;
        }else{
            textArea.append("This username already in use..\n");
            return false;
        }
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
