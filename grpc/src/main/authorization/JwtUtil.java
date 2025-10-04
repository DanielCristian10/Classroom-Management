package authorization;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET_KEY = "am_o_cheie_mica_asa_si_asa_si_serverul_cam_pica_asa_si_asa";

    public static Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8))).build().parseClaimsJws(token).getBody();
    }

    public static String extractEmail(String token) {
        Claims claims = extractClaims(token);
        return claims.get("email", String.class);
    }

    public static String extractJwtFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new IllegalStateException("JWT token is missing or malformed.");
    }

    public static String extractRole(String token) {
        Claims claims = extractClaims(token);
        return claims.get("role", String.class);
    }
}
