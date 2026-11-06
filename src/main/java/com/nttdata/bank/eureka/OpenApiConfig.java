package com.nttdata.bank.eureka;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Published OpenAPI contract of the service registry.
 * Eureka serves these operations through Jersey, so they are declared here.
 */
@Configuration
public class OpenApiConfig {

    /**
     * @return contract metadata and the registry operations
     */
    @Bean
    public OpenAPI eurekaOpenApi() {
        OpenAPI api = new OpenAPI().info(new Info()
                .title("Eureka server")
                .version("1.0.0")
                .description("Service registry and dashboard. Business services register here and the gateway resolves them by name."));
        api.path("/", get(operation("open-dashboard", "Open the Eureka dashboard")));
        api.path("/eureka/apps", get(operation("list-applications", "List registered applications")));
        api.path("/eureka/apps/{appId}", post(
                get(operation("list-instances", "List instances of one application"), parameter("appId", "Application name")),
                operation("register-instance", "Register an instance")));
        api.path("/eureka/apps/{appId}/{instanceId}", remove(
                put(get(operation("read-instance", "Read one instance"),
                        parameter("appId", "Application name"),
                        parameter("instanceId", "Instance identifier")),
                        operation("renew-instance", "Renew the instance lease")),
                operation("deregister-instance", "Deregister the instance")));
        api.path("/eureka/apps/{appId}/{instanceId}/status", remove(
                put(empty(parameter("appId", "Application name"), parameter("instanceId", "Instance identifier")),
                        operation("override-status", "Override the instance status")),
                operation("clear-status", "Remove the status override")));
        return api;
    }

    private static PathItem empty(Parameter... parameters) {
        PathItem item = new PathItem();
        if (parameters.length > 0) {
            item.setParameters(new ArrayList<>(List.of(parameters)));
        }
        return item;
    }

    private static PathItem get(Operation operation, Parameter... parameters) {
        PathItem item = empty(parameters);
        item.setGet(operation);
        return item;
    }

    private static PathItem post(PathItem item, Operation operation) {
        item.setPost(operation);
        return item;
    }

    private static PathItem put(PathItem item, Operation operation) {
        item.setPut(operation);
        return item;
    }

    private static PathItem remove(PathItem item, Operation operation) {
        item.setDelete(operation);
        return item;
    }

    private static Operation operation(String operationId, String summary) {
        Operation operation = new Operation();
        operation.setOperationId(operationId);
        operation.setSummary(summary);
        operation.setResponses(new ApiResponses().addApiResponse("200", new ApiResponse().description(summary)));
        return operation;
    }

    private static PathParameter parameter(String name, String description) {
        PathParameter parameter = new PathParameter();
        parameter.setName(name);
        parameter.setDescription(description);
        parameter.setRequired(true);
        parameter.setSchema(new StringSchema());
        return parameter;
    }
}
