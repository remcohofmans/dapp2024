package be.kuleuven.dsgt4.auth;

import be.kuleuven.dsgt4.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private boolean isProduction;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                DecodedJWT decodedToken = JWT.decode(token);

                String email = decodedToken.getClaim("email").asString();
                String role = decodedToken.getClaim("role").asString();

                User user = new User(email, role);
                FirebaseAuthentication authentication = new FirebaseAuthentication(user);
                if (isProduction && !verifyJWT(decodedToken)) {
                    authentication.setAuthenticated(false);
                } else {
                    authentication.setAuthenticated(true);
                }

                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authentication);

                filterChain.doFilter(request, response);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                filterChain.doFilter(request, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    protected boolean verifyJWT(DecodedJWT decodedToken) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String keyData = objectMapper.readTree(getPublicKey()).get(decodedToken.getKeyId()).asText();

            String rawKey = keyData
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replace("\n", "");

            byte[] keyBytes = Base64.getDecoder().decode(rawKey);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(keyBytes);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) certificate.getPublicKey(), null);
            JWT.require(algorithm).build().verify(decodedToken);

            System.out.println("JWT verification successful");
            return true;
        } catch (Exception e) {
            System.out.println("JWT verification failed: " + e.getMessage());
            return false;
        }
    }

    public String getPublicKey() {
        return webClientBuilder
                .baseUrl("https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com")
                .build()
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !path.startsWith("/api");
    }

    private static class FirebaseAuthentication implements Authentication {
        private final User user;
        private boolean isAuthenticated;

        FirebaseAuthentication(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return user.isManager() ? List.of(new SimpleGrantedAuthority("manager")) : new ArrayList<>();
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
            return isAuthenticated;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            this.isAuthenticated = isAuthenticated;
        }

        @Override
        public String getName() {
            return this.user.getEmail();
        }
    }
}
