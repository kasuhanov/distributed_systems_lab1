package ru.kasuhanov.task1;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom {
    private Map<String,Socket> users = new HashMap<>();
    private String name;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatRoom room = (ChatRoom) o;

        return name.equals(room.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
