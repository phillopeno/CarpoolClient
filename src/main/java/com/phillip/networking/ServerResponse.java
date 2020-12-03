package com.phillip.networking;

import com.esotericsoftware.kryonet.Connection;

public class ServerResponse {

    private final Connection connection;

    private final Packet packet;

    public ServerResponse(Connection connection, Packet packet) {
        this.connection = connection;
        this.packet = packet;
    }

    public Connection getConnection() {
        return connection;
    }

    public Packet getPacket() {
        return packet;
    }
}
