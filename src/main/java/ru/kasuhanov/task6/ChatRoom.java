package ru.kasuhanov.task6;

import java.net.Socket;
import java.util.Map;

public class ChatRoom {
    private String name;
    private Map<String,Socket> users;

    public ChatRoom(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Socket> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Socket> users) {
        this.users = users;
    }
}
