package com.interswitchgroup.tx_user_portal.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class JwtUtil {

    static Algorithm algorithm = Algorithm.HMAC256("secret");

    public static String createJwt() throws JWTCreationException {
        HashSet<String> permissions = createPermissions();
        //convert permissions to list

        List<String> list = new ArrayList<>();
        for (String permission : permissions) list.add(permission);
        return JWT.create()
                .withClaim("username", "john_doe")
                .withClaim("permissions", list)
                .withIssuer("tx-portal")
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis()+5000L))
                .sign(algorithm);
    }

    private static HashSet<String> createPermissions(){
        HashSet<String> permissions = new HashSet<>();
        permissions.add("viewRequestForm");
        return permissions;
    }

    public static boolean validateJwt(String token){
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build(); //R
            verifier.verify(token);
            return true;
        }catch (JWTVerificationException jwtVerificationException){
            return false;
        }
    }

    public static String getUsername(String token){
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();
        verifier.verify(token);
        DecodedJWT jwt = verifier.verify(token);
        Claim claim = jwt.getClaim("username");
        return claim.asString();
    }
}
