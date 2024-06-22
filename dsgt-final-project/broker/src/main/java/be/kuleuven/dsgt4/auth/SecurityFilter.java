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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import com.auth0.jwt.JWT;
import java.security.interfaces.RSAPublicKey;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;


@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private boolean isProduction;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // (level 1) decode Identity Token and assign correct email and role (DONE)
        // TODO: (level 2) verify Identity Token
        // We have no need to check which request URI have to be decoded,
        // because this selection has already been implemented in the WebSecurityConfig
        // System.out.println("Uniform Resource Identifier: "+request.getRequestURI());

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try {
                // Separate the "Bearer " from the encoded string and then decode this string
                String token = authorizationHeader.substring("Bearer ".length());
                DecodedJWT decodedToken = JWT.decode(token);

                // Get the desired credentials out of the decoded token
                String email = decodedToken.getClaim("email").asString();
                String role = decodedToken.getClaim("role").asString();


                // Use the acquired credentials to create a new user instance to add to the security context
                var user = new User(email, role);
                FirebaseAuthentication fireAuth = new FirebaseAuthentication(user);
                if(isProduction){ // check if the application is running in production :isProduction
                    boolean verified = verifyJWT(decodedToken);
                    if(verified){
                        //System.out.println("Authentication has been set to true");
                        fireAuth.setAuthenticated(true);
                    } else {
                        //System.out.println("Authentication has been set to false");
                        fireAuth.setAuthenticated(false);
                    }
                } else {
                    fireAuth.setAuthenticated(true);
                }

                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(fireAuth);

                filterChain.doFilter(request,response);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        } else{
            //if we have nothing to decode, do nothing
            filterChain.doFilter(request,response);
        }
    }

    protected boolean verifyJWT(DecodedJWT decodedToken){
        try {
            //processing the raw data we received from the google endpoint
            ObjectMapper objectMapper = new ObjectMapper();
            var keyData = objectMapper.readTree(getPublicKey());
            var publicKey = keyData.get(decodedToken.getKeyId()).asText();
            System.out.println(publicKey);
            var rawKey = publicKey
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replace("\n","");

            //turning the raw string of the public key into the RSA256 style format
            byte keyBytes[] = Base64.getDecoder().decode(rawKey);
            ByteArrayInputStream inputStream  =  new ByteArrayInputStream(keyBytes);
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate x509KeySpec = (X509Certificate)fact.generateCertificate(inputStream);

            // The algorithm does not require a
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) x509KeySpec.getPublicKey(), null);

            // if the JWT is valid it would return the decoded token, if it is invalid it will throw an exception
            JWT. require(algorithm)
                    .build()
                    .verify(decodedToken);

            System.out.println("JWT verification successful");
            return true;
        } catch (Exception e){
            System.out.println("JWT verification failed: " + e.getMessage());
            return false;
        }
    }
    public String getPublicKey() {
        // fetch the public keys from the google endpoint
        var publicKey = webClientBuilder
                .baseUrl("https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com")
                .build()
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return publicKey;
    }

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

