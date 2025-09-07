package com.example.serverchillrate.secutiry.jwt;

import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.entity.AuthorizationDetails;
import com.example.serverchillrate.models.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

/*
JWT service
реализует функционал связанный с jwt- токеном
 */
@Service
public class JwtService {
    @Value("${spring.refresh-token.time-to-live}")
    Long refresh_exp;
    @Value("${spring.access-token.time-to-live}")
    Long access_exp;

    private static final String SECRET_KEY=System.getenv("SECRET_KEY");
    //генерация токена: токен действует 1 день
    public String generateToken(Map<String,Object> extraClaims, UserDetails userDetails,long expiration){
        return Jwts.builder().claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now().truncatedTo(ChronoUnit.SECONDS))).expiration(Date.from(Instant.now().truncatedTo(ChronoUnit.SECONDS).plus(expiration, ChronoUnit.SECONDS)))
                .signWith(getSigningKey())
                .compact();
    }
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }
    public String generateToken(UserApp userDetails){
        return generateToken(userDetails,false);
    }
    public String generateToken(UserApp userDetails,boolean confirmEmail){
        return generateToken(AuthUser.builder()
                .uuid(userDetails.getId())
                .authorities(userDetails.getAuthorities())
                .secret(null)
                .device(null)
                .confirmMail(confirmEmail)
                .build(), access_exp);
    }
     String generateToken(AuthUser authUser,Long expiration){
        HashMap<String,Object> claims= new HashMap<>();
        ArrayList<String> roles=new ArrayList<>();
        authUser.getAuthorities().forEach(c->roles.add(c.getAuthority()));
        claims.put("confirmEmail",authUser.isConfirmMail());
        claims.put("roles",roles);
        claims.put("device",authUser.getDevice());
        claims.put("secret",authUser.getSecret()==null?null:authUser.getSecret().toString());
        claims.put("uuid",authUser.getUuid().toString());
        return  generateToken(claims,authUser,expiration);
    }
    public String generateRefreshToken(UserApp userDetails,boolean confirmEmail,
                                              AuthorizationDetails securityDetails){
        return generateToken(AuthUser.builder()
                .uuid(userDetails.getId())
                .authorities(userDetails.getAuthorities())
                .confirmMail(confirmEmail)
                .secret(securityDetails.getSecret())
                .device(securityDetails.getDevice())
                .build(), refresh_exp);
    }

    public AuthUser getAuthUser(String token){
        final Claims claims=extractAllClaims(token);
        var secret=claims.get("secret",String.class);
        List<String> roles=claims.get("roles",List.class);
        List<SimpleGrantedAuthority> authorities=roles.stream().map(SimpleGrantedAuthority::new).toList();
        return AuthUser.builder()
                .authorities(authorities)
                .confirmMail(claims.get("confirmEmail",Boolean.class))
                .uuid(UUID.fromString(claims.get("uuid",String.class)))
                .secret(secret==null?null:UUID.fromString(secret))
                .device(claims.get("device",String.class))
                .build();
    }
    /*
    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username=extractUsername(token);
        return (username.equals(userDetails.getUsername())&&!isTokenExpired(token));
    }
    */
    private boolean isTokenExpired(String token) {
        return  extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }
    private Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith((SecretKey) getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
    public  <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return  claimsResolver.apply(claims);
    }
    private Key getSigningKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public boolean isTokenValid(@NonNull String token) {
        Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(token);
        return !isTokenExpired(token);
    }

}