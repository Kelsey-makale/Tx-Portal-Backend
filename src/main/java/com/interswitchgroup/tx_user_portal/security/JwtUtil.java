package com.interswitchgroup.tx_user_portal.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.interswitchgroup.tx_user_portal.models.CreatedUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class JwtUtil {

    static Algorithm algorithm = Algorithm.HMAC256("secret");

    public static String createJwt(CreatedUser createdUser) throws JWTCreationException {
        return JWT.create()
                .withSubject(createdUser.getUsername())
                .withClaim("organization_id", createdUser.getOrganization_id())
                .withIssuer("auth0")
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis()+86400000L)) //24 hrs
                .sign(algorithm);
    }

    private static HashSet<String> createPermissions(){
        HashSet<String> permissions = new HashSet<>();
        permissions.add("viewRequestForm");
        return permissions;
    }

    public static boolean validateJwt(String token, UserDetails userDetails){
        try {
            final String username = getUsername(token);
            if(!username.equals(userDetails.getUsername())){
                return false;
            }
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
        return jwt.getSubject();
    }

    public static String getOrganizationId(String token){
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();
        verifier.verify(token);
        DecodedJWT jwt = verifier.verify(token);
        Claim claim = jwt.getClaim("organization_id");
        return claim.asString();
    }

    public static Date getExpirationDate(String token){
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();
        verifier.verify(token);
        DecodedJWT jwt = verifier.verify(token);
        Date expirationDate = jwt.getExpiresAt();
        return expirationDate;
    }
}
