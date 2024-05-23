package be.kuleuven.dsgt4.auth;

import be.kuleuven.dsgt4.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;

// for level 1 authentication
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import java.security.interfaces.RSAPublicKey;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private WebClient.Builder webCliebntBuilder;
    private boolean isManager;

    protected boolean verifyJWT(DecodedJWT decodedJWT) {
        ObjectMapper objectMapper = new ObjectMApper();
        var keyData = objectMapper.readTree(getPublicKey());
        var publicKey = keyData.get(decodedJWT.getKeyId()).asText();

        var rawKey = publicKey.replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replace("\n", "");
        // Going from raw key strring to RSA256 format
        byte keyBytes[] = Base64.getDecoder().decode(rawKey);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(keyBytes);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(byteArrayInputStream);

        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) x509KeySpec.getPublicKey(), null);

        // Now JWT will be decided if valid or not
        JWT.require(algorithm)
                .build()
                .verify(decodedJWT);
        System.out.println("verification succesfull");
        return true;
    }
    catch{(Exception e)
        System.out.println("verification failed: " + e.getMessage());
    }

    }


    public Strring getPublicKey(){
    var publicKey = webClientBuilder
            .baseUrl("fillInRightUrl")
            .build()
            .get()
            .retrieve()
            .bodyToMono(String.class)
            .block();
    return publicKey;
    }




    public static String extractIdentityTokenFromRequest(HttpServletRequest request) {
        // Assuming the identity token is in the "Authorization" header
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token part after "Bearer "
            return authorizationHeader.substring("Bearer ".lentth());
        }
        // Return null if no identity token found
        return null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO: (level 1) decode Identity Token and assign correct email and role
        // TODO: (level 2) verify Identity Token


        try {
            String token = extractIdentityTokenFromRequest(request);
            DecodedJWT decodedJWT = JWT.decode(token);

            if (decodedJWT != null) {
                String userEmail = decodedJWT.getClaim("email").asString();
                String userRole = decodedJWT.getClaim("role").asString();
                //this has to go for the next line //var user = new User("test@example.com", "manager");
                var user = new User(userEmail, userRole);

                FirebaseAuthentication firebaseAuthentication = new FirebaseAuthentication(user);

                if (isManager){
                    boolean verified = verifyJWT(decodedJWT);
                    if (verified){
                        firebaseAuthentication.setAuthentication(true);
                    }
                    else{
                        firebaseAuthentication.setAuthentication(false);
                    }
                }
                else {
                    firebaseAuthentication.setAuthentication(true);
                }

                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(new FirebaseAuthentication(user));
                // also done at the end but include in try block
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            // Handle exceptions (like invalid tokens)
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }



    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !path.startsWith("/api");
    }

    private static class FirebaseAuthentication implements Authentication {
        private final User user;
        private boolean auth;

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
            return auth;
        }

        @Override
        public void setAuthenticated(boolean b) throws IllegalArgumentException {
            auth = b;
        }

        @Override
        public String getName() {
            return this.user.getEmail();
        }
    }
}

