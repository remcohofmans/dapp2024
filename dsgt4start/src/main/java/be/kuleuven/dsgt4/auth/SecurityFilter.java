package be.kuleuven.dsgt4.auth;

import be.kuleuven.dsgt4.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// for level 1 authentication
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Base64;
// till here


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    // added to implement level 1 of the authentication
    public static String extractIdentityTokenFromRequest(HttpServletRequest request) {
        // Assuming the identity token is in the "Authorization" header
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token part after "Bearer "
            return authorizationHeader.substring(7);
        }
        ///////////////This method should be reviewed (but just to get rid of errors now)
        // If not found in the header, check other places (e.g., query parameters)
        // Implement additional logic here based on your specific use case

        // Return null if no identity token found
        return null;
    }
    public static DecodedJWT decodeIdentityToken(String token) {
        String[] chunks = token.split("\\.");

        if (chunks.length < 2) {
            // Invalid token format
            return null;
        }

        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        // Create a DecodedJWT object
        return JWT.decode(token);
    }

    //till here
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO: (level 1) decode Identity Token and assign correct email and role
        // TODO: (level 2) verify Identity Token

        //Implemented for level 1
        try {
            String identityToken = extractIdentityTokenFromRequest(request);
            DecodedJWT decodedJWT = decodeIdentityToken(identityToken);

            if (decodedJWT != null) {
                String userEmail = decodedJWT.getSubject();
                String userRole = decodedJWT.getClaim("role").asString();
                //I think this has to go for the next line //var user = new User("test@example.com", "manager");
                var user = new User(userEmail, userRole);
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(new FirebaseAuthentication(user));
            }
        } catch (Exception e) {
            // Handle exceptions (e.g., invalid tokens)
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
        //


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !path.startsWith("/api");
    }

    private static class FirebaseAuthentication implements Authentication {
        private final User user;

        FirebaseAuthentication(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            if (user.isManager()) {
                return List.of(new SimpleGrantedAuthority("manager"));
            } else {
                return new ArrayList<>();
            }
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public User getPrincipal() {
            return this.user;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean b) throws IllegalArgumentException {

        }

        @Override
        public String getName() {
            return null;
        }
    }
}

