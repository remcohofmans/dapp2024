package be.kuleuven.dsgt4.auth;

import be.kuleuven.dsgt4.User;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
// for level authentication
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.ByteArrayInputStream;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
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

    // added to implement the authentication
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private boolean isProduction; //Must be adjusted as this is just fo easier debugging


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
    // TODO: Change some logic here:

    protected boolean verifyJWT(DecodedJWT decodedToken) {
        try {
            // Fetch and parse the public key
            ObjectMapper objectMapper = new ObjectMapper();
            var keyData = objectMapper.readTree(getPublicKey());
            var publicKey = keyData.get(decodedToken.getKeyId()).asText();
            System.out.println("Public key fetched: " + publicKey);

            // Clean up the public key string
            var rawKey = publicKey
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replace("\n", "");

            // Decode the public key
            byte[] keyBytes = Base64.getDecoder().decode(rawKey);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(keyBytes);
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate x509KeySpec = (X509Certificate) fact.generateCertificate(inputStream);

            // Create the RSA256 algorithm
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) x509KeySpec.getPublicKey(), null);

            // Verify the token
            JWT.require(algorithm).build().verify(decodedToken);

            System.out.println("JWT verification successful");
            return true;
        } catch (CertificateException e) {
            System.out.println("Certificate processing failed: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.out.println("Public key fetching/parsing failed: " + e.getMessage());
            return false;
        } catch (JWTVerificationException e) {
            System.out.println("JWT verification failed: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("JWT verification encountered an unexpected error: " + e.getMessage());
            return false;
        }
    }

    public String getPublicKey() {
        // fetch the public keys from the google endpoint
        String url= "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";
        var publicKey = webClientBuilder
                .baseUrl(url)
                .build()
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return publicKey;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                processToken(authorizationHeader);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT verification failed");
            }
        } else {
            // If there is nothing to decode, do nothing
            filterChain.doFilter(request, response);
        }
    }
    private DecodedJWT processToken(String authorizationHeader) throws Exception {
        // Separate the "Bearer " from the encoded string and then decode this string
        String token = authorizationHeader.substring("Bearer ".length());
        DecodedJWT decodedToken = JWT.decode(token);

        // Get the desired credentials out of the decoded token
        String email = decodedToken.getClaim("email").asString();
        String role = decodedToken.getClaim("role").asString();

        // Use the acquired credentials to create a new user instance to add to the security context
        var user = new User(email, role);
        FirebaseAuthentication fireAuth = new FirebaseAuthentication(user);

        if (isProduction) { // check if the application is running in production :isProduction
            boolean verified = verifyJWT(decodedToken);
            fireAuth.setAuthenticated(verified);
        } else {
            fireAuth.setAuthenticated(true);
        }

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(fireAuth);

        return decodedToken;
    }
    //till here


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !path.startsWith("/api");
    }

    private static class FirebaseAuthentication implements Authentication {
        private final User user;
        private boolean Auth;

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
            return Auth;
        }

        @Override
        public void setAuthenticated(boolean b) throws IllegalArgumentException {
            Auth = b;
        }

        @Override
        public String getName() {
            return this.user.getEmail();
        }
    }
}

