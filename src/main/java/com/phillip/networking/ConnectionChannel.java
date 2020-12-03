package com.phillip.networking;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.phillip.Constants;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionChannel {

    private final Queue<ServerResponse> responses;

    private final Client client;

    /**
     * Opens a new connection to the server.
     */
    public ConnectionChannel() {
        responses = new LinkedBlockingQueue<>();
        client = new Client();
        client.start();
        register();
    }

    public boolean send(Packet packet) {
        if (connect()) {
            responses.clear();
            listen();
            int len = client.sendTCP(packet);
            System.out.println("[INFORMATION] Transferring packet (size: " + len + ")");
            return len > 0;
        } else {
            System.err.println("Unable to send packet, connection issues.");
            return false;
        }
    }

    private void register() {
        Reflections reflections = new Reflections("com.phillip.networking.packets");
        Set<Class<? extends Packet>> classes = reflections.getSubTypesOf(Packet.class);
        System.out.printf("[ClientConnection] %d packets registered\n", classes.size());
        classes.forEach(client.getKryo()::register);
    }

    private boolean connect() {
        if (!client.isConnected()) {
            try {
                client.connect(5000, Constants.HOST, Constants.PORT);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    private boolean listen() {
        if (client.isConnected()) {
            client.addListener(new Listener() {
                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof Packet) {
                        responses.add(new ServerResponse(connection, (Packet) object));
                        System.out.printf(
                                "\nPacket received from server: [%s][PORT: %d]\n",
                                connection.getRemoteAddressTCP().getAddress(),
                                connection.getRemoteAddressTCP().getPort()
                        );
                    } else {
                        System.err.printf(
                                "Unknown transmission from server: [%s][PORT: %d]",
                                connection.getRemoteAddressTCP().getAddress(),
                                connection.getRemoteAddressTCP().getPort()
                        );
                    }
                }
            });
            return true;
        } else {
            System.err.println("Unable to open channel!");
            return false;
        }
    }

    public Queue<ServerResponse> getResponses() {
        return responses;
    }

    public void close() {
        if (client.isConnected() || client.isIdle())
            client.close();
    }
}
