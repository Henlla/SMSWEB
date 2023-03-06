package com.example.smsweb.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JWTUtils {
    public static void checkExpired(String _token) {
        try {
            DecodedJWT jwt = JWT.decode(_token);
            if (jwt.getExpiresAt().before(new Date())) {
                throw new Exception("Token expired");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String isExpired(String _token) {
        DecodedJWT jwt = JWT.decode(_token);
        if (jwt.getExpiresAt().before(new Date())) {
            return "token expired";
        } else {
            return "";
        }
    }
}
