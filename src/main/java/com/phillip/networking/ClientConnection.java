package com.phillip.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.phillip.Constants;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class ClientConnection {

    public static ClientConnection SINGLETON;

    private final HashMap<Connection, Packet> queue;

    private final Client client;

    public ClientConnection() {
        client = new Client();
        queue = new HashMap<>();
        client.start();
        register(client.getKryo());
        try {
            client.connect(300, Constants.HOST, Constants.PORT);
        } catch (IOException e) {
            System.err.printf("Unable to establish connection to host [%s:%d]\n", Constants.HOST, Constants.PORT);
        }
        if (client.isConnected()) {
            client.addListener(new Listener() {
                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof Packet) {
                        queue.put(connection, (Packet) object);
                        System.out.println("Stored packet in queue from [" + connection.getRemoteAddressTCP().getAddress() + "]");
                    } else {
                        System.out.println("Unknown object received from " + connection.getRemoteAddressTCP().getAddress());
                    }
                }
            });
        } else {
            System.err.println("Unable to listen to commands.");
        }
    }

    private void register(Kryo kryo) {
        Reflections reflections = new Reflections("com.phillip.networking.packets");
        Set<Class<? extends Packet>> classes = reflections.getSubTypesOf(Packet.class);
        System.out.printf("[ClientConnection] %d packets registered\n", classes.size());
        classes.forEach(kryo::register);
    }

    public void send(Packet packet) {
        if (!client.isConnected()) {
            try {
                client.reconnect();
            } catch (IOException e) {
                System.err.println("Connection error: " + e.getMessage());
            }
        }
        queue.clear();
        client.sendTCP(packet);
    }

    public HashMap<Connection, Packet> getQueue() {
        return queue;
    }

    public static ClientConnection getInstance() {
        if (SINGLETON == null)
            SINGLETON = new ClientConnection();
        return SINGLETON;
    }
}
