package be.kuleuven.dsgt4;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.HypermediaWebClientConfigurer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.Objects;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication
public class

Dsgt4Application {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// System.setProperty("server.port", System.getenv().getOrDefault("PORT",
		// "8080"));
		SpringApplication.run(Dsgt4Application.class, args);

	}

	@Bean
	public boolean isProduction() {
		return Objects.equals(System.getenv("GAE_ENV"), "standard");
	}

	@Bean
	public String projectId() {
		if (this.isProduction()) {
			return "TODO level 2";
		} else {
			return "demo-distributed-systems-kul";
		}
	}

	@Bean
	public Firestore db() {
		if (isProduction()) {
			return FirestoreOptions.getDefaultInstance().toBuilder().setProjectId(this.projectId()).build()
					.getService();
		} else {
			return FirestoreOptions.getDefaultInstance().toBuilder().setProjectId(this.projectId())
					.setCredentials(new FirestoreOptions.EmulatorCredentials()).setEmulatorHost("localhost:8084")
					.build().getService();
		}
	}

	/*
	 * You can use this builder to create a Spring WebClient instance which can be
	 * used to make REST-calls.
	 */
	@Bean
	WebClient.Builder webClientBuilder(HypermediaWebClientConfigurer configurer) {
		return configurer.registerHypermediaTypes(
				WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
						.codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
								.maxInMemorySize(100 * 1024 * 1024)));
	}

	@Bean
	HttpFirewall httpFirewall() {
		DefaultHttpFirewall firewall = new DefaultHttpFirewall();
		firewall.setAllowUrlEncodedSlash(true);
		return firewall;
	}


}
