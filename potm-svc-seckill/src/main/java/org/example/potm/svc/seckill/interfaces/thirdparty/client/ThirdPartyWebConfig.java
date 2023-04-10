package org.example.potm.svc.seckill.interfaces.thirdparty.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author jianchengwang
 * @date 2023/4/7
 */
@Configuration
public class ThirdPartyWebConfig {

    @Value("${payUrl}")
    private String payUrl;

    @Bean
    WebClient webClient(ObjectMapper objectMapper) {
        return WebClient.builder()
                .baseUrl(payUrl)
                .build();
    }

    @SneakyThrows
    @Bean
    ThirdPartyClient thirdPartyClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient))
                        .build();
        return httpServiceProxyFactory.createClient(ThirdPartyClient.class);
    }
}
