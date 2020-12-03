package com.phillip.networking.packets;

import com.phillip.networking.Packet;

public class MessagePacket extends Packet {

    private String message;

    public MessagePacket() {

    }

    public MessagePacket(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
