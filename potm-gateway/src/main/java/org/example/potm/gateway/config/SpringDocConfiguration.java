package org.example.potm.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/10
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class SpringDocConfiguration {

    private final SwaggerUiConfigParameters swaggerUiConfigParameters;

    private final RouteDefinitionLocator locator;

    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis() {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        assert definitions != null;
        definitions.stream().filter(routeDefinition -> (routeDefinition.getId().startsWith("potm-svc-")
        )).forEach(routeDefinition -> {
            String routeDefinitionId = routeDefinition.getId();
            String name = routeDefinitionId.replace("potm-", "");
            swaggerUiConfigParameters.addGroup(name);
            groups.add(GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").group(name).build());
        });
        return groups;
    }

}

