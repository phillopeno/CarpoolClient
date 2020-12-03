package com.phillip;

import com.phillip.interfaces.LoginScreen;

import javax.swing.*;

public class Application {

    private static boolean LOCK = true;

    private static String SESSION;

    public static void main(String[] args) {
        JFrame loginScreen = new LoginScreen();
        loginScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginScreen.setVisible(true);
    }

    /**
     * @return
     *      FALSE if login is already false.
     *      TRUE if login set to false.
     */
    public static boolean acquireLock() {
        if (!LOCK)
            return false;
        LOCK = false;
        System.out.println("[Application] Login locked.");
        return true;
    }

    /**
     * Unlocks the login state.
     */
    public static void unlock() {
        System.out.println("[Application] Login unlocked.");
        LOCK = true;
    }

}
