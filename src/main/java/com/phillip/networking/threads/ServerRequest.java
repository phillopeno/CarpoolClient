package com.phillip.networking.threads;

import com.phillip.Application;
import com.phillip.interfaces.LoginScreen;
import com.phillip.networking.ConnectionChannel;
import com.phillip.networking.ServerResponse;
import com.phillip.networking.packets.AuthenticationPacket;

import javax.swing.*;
import java.util.concurrent.ScheduledExecutorService;

public class ServerRequest implements Runnable {

    private final ScheduledExecutorService service;

    private final LoginScreen screen;

    private final ConnectionChannel channel;

    int loop = 0;

    public ServerRequest(LoginScreen loginScreen, ScheduledExecutorService service, AuthenticationPacket packet) {
        this.service = service;
        this.screen = loginScreen;
        channel = new ConnectionChannel();
        channel.send(packet);
    }

    public void callback(ServerResponse response) {

    }

    @Override
    public void run() {
        if (loop++ > 10) {
            System.out.println("Server taking too long to respond, shutting down thread.");
            screen.outputLabel.setText("Unable to connect!");
            JOptionPane.showMessageDialog(null, "Error establishing a connection with the server!");
            Application.unlock();
            channel.close();
            service.shutdown();
            return;
        }

        ServerResponse response;

        if ((response = channel.getResponses().poll()) != null) {
            callback(response);
        }

        System.out.println("Thread [" + Thread.currentThread().getId() + "] is RUNNING [LOOP: " + loop + "]");
    }

    public ConnectionChannel getChannel() {
        return channel;
    }
}
