package com.phillip.networking.packets;

import com.phillip.networking.Packet;

public class AuthenticationPacket extends Packet {

    private String username, password, intent;

    public AuthenticationPacket() {

    }

    public AuthenticationPacket(String username, String password, String intent) {
        this.username = username;
        this.password = password;
        this.intent = intent;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getIntent() {
        return intent;
    }
}
