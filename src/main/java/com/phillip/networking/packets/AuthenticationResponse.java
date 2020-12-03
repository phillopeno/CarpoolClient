package com.phillip.networking.packets;

import com.phillip.networking.Packet;

import java.util.Random;

/**
 * Server Response
 */
public class AuthenticationResponse extends Packet {

    public static final int SUCCESS = 1, INCORRECT_USER_PASS = -1, UNKNOWN = 0;

    private int code;//the response code

    private String session;//the session key

    public AuthenticationResponse() {

    }

    public AuthenticationResponse(int code) {
        this.session = code == 1 ? generate() : "";
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getSession() {
        return session;
    }

    public String generate() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 10;
        return new Random().ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
