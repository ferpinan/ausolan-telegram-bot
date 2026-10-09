package eus.ferpinan.ausolanmenu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class responsible for defining and initializing
 * Spring-managed beans related to REST communication.
 */
@Configuration
public class RestConfig {

    /**
     * Creates and registers a {@link RestTemplate} bean in the Spring application context.
     * The RestTemplate is a synchronous client used to perform HTTP requests,
     * facilitating communication with external RESTful services.
     *
     * @return A new instance of {@link RestTemplate}.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}