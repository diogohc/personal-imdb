package MyImdb.demo.config;

import MyImdb.demo.model.Role;
import MyImdb.demo.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {
    //2hrs
    private long TOKEN_VALIDITY = 2 * 60 * 600 * 1000;
    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    @Autowired
    private UserRepository userRepository;

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = null;
        try{
            claims = extractAllClaims(token);
        } catch(NullPointerException e){
            log.error("No JWT found");
        }
        if(claims == null) return null;

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        Claims claims = null;
        try{
            claims = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
        } catch (MalformedJwtException e){
            log.error("Malformed JWT");
        } catch (ExpiredJwtException e1){
            log.error("Expired JWT");
        }

        return claims;
    }

    private Key getSignInKey() {
        byte[] keyBites = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBites);
    }

    public String generateToken(UserDetails userDetails, Long userId, Role role){

        List<String> currentUserRoles =
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", "ROLE_"+role);
        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails){
        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token.substring(7))
                .getBody();

        return claims.get("userId", Long.class);
    }
}
