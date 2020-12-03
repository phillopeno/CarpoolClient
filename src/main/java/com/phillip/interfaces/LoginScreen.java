package com.phillip.interfaces;

import com.phillip.Application;
import com.phillip.networking.ServerResponse;
import com.phillip.networking.packets.AuthenticationPacket;
import com.phillip.networking.packets.AuthenticationResponse;
import com.phillip.networking.threads.ServerRequest;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoginScreen extends JFrame {

    private static final int FRAME_WIDTH = 350;
    private static final int FRAME_HEIGHT = 240;

    private JRadioButton driverButton, riderButton;//the two options to select from when logging in...

    public JLabel usernameLabel, passwordLabel, outputLabel;
    private JTextField usernameTextField;
    private JPasswordField passwordTextField;

    private JButton actionButton;//login button

    public LoginScreen() {
        setTitle("Application");
        setResizable(false);
        buildElements();
        buildPanel();
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null); //center frame
    }

    private void buildElements() {
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        outputLabel = new JLabel("Please login to continue...");
        usernameTextField = new JTextField();
        passwordTextField = new JPasswordField();

        driverButton = new JRadioButton("Driver");
        riderButton = new JRadioButton("Rider");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(driverButton);
        buttonGroup.add(riderButton);

        actionButton = new JButton("Login");

        class ButtonListener implements ActionListener {

            public final LoginScreen screen;

            public ButtonListener(LoginScreen loginScreen) {
                this.screen = loginScreen;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!driverButton.isSelected() && !riderButton.isSelected()) {
                    outputLabel.setText("Please select driver or rider!");
                } else if (Application.acquireLock()) {
                    outputLabel.setText("Connecting to server...");
                    AuthenticationPacket packet = new AuthenticationPacket(
                            usernameTextField.getText(),
                            new String(passwordTextField.getPassword()),//todo encryption
                            driverButton.isSelected() ? "DRIVER" : "RIDER"
                    );
                    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

                    service.scheduleAtFixedRate(new ServerRequest(screen, service, packet) {
                        @Override
                        public void callback(ServerResponse response) {
                            if (response.getPacket() instanceof AuthenticationResponse) {
                                AuthenticationResponse auth = (AuthenticationResponse) response.getPacket();
                                switch (auth.getCode()) {
                                    case AuthenticationResponse.SUCCESS:
                                        dispose();
                                        System.out.println("Received session key: " + auth.getSession()
                                                + " from: " + response.getConnection().getRemoteAddressTCP().getAddress());

                                        break;
                                    case AuthenticationResponse.INCORRECT_USER_PASS:
                                        outputLabel.setText("Incorrect username or password!");
                                        Application.unlock();
                                        break;
                                    default:
                                        outputLabel.setText("Error: Unhandled server response");
                                        Application.unlock();
                                        break;
                                }
                                getChannel().close();
                                service.shutdown();
                            }
                        }
                    }, 600, 600, TimeUnit.MILLISECONDS);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Please wait the server is processing your request!");
                }
            }
        }

        actionButton.addActionListener(new ButtonListener(this));
    }

    private void buildPanel() {
        JPanel panel = new JPanel();

        panel.setLayout(null);

        usernameLabel.setBounds(10, 20, 80, 25);
        usernameTextField.setBounds(100, 20, 165, 25);
        passwordLabel.setBounds(10, 70, 80, 25);
        passwordTextField.setBounds(100, 70, 165, 25);
        outputLabel.setBounds(20, 160, 220, 25);
        actionButton.setBounds(30, 120, 120, 25);
        driverButton.setBounds(160, 120, 60, 25);
        riderButton.setBounds(230, 120, 60, 25);

        panel.add(usernameLabel);
        panel.add(passwordLabel);
        panel.add(outputLabel);
        panel.add(usernameTextField);
        panel.add(passwordTextField);
        panel.add(driverButton);
        panel.add(riderButton);
        panel.add(actionButton);
        add(panel);
    }
}
