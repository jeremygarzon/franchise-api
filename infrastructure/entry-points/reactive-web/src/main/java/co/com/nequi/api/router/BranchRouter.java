package co.com.nequi.api.router;

import co.com.nequi.api.dto.request.CreateBranchRequest;
import co.com.nequi.api.dto.request.UpdateNameRequest;
import co.com.nequi.api.dto.response.BranchResponse;
import co.com.nequi.api.dto.response.ErrorResponse;
import co.com.nequi.api.handler.BranchHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BranchRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/branches",
                    method = RequestMethod.POST,
                    beanClass = BranchHandler.class,
                    beanMethod = "createBranch",
                    operation = @Operation(
                            operationId = "createBranch",
                            summary = "Add a branch to a franchise",
                            tags = {"Branches"},
                            requestBody = @RequestBody(required = true, content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CreateBranchRequest.class),
                                    examples = @ExampleObject(value = "{\"name\":\"Downtown Branch\",\"franchiseId\":1}"))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch created",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = BranchResponse.class),
                                                    examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Downtown Branch\",\"franchiseId\":1}"))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(value = "{\"code\":\"FRANCHISE_NOT_FOUND\",\"message\":\"Franchise not found\"}")))
                            })),
            @RouterOperation(
                    path = "/api/branches/{branchId}/name",
                    method = RequestMethod.PATCH,
                    beanClass = BranchHandler.class,
                    beanMethod = "updateBranchName",
                    operation = @Operation(
                            operationId = "updateBranchName",
                            summary = "Update the name of a branch",
                            tags = {"Branches"},
                            parameters = @Parameter(name = "branchId", in = ParameterIn.PATH, required = true,
                                    description = "Branch id", example = "1"),
                            requestBody = @RequestBody(required = true, content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UpdateNameRequest.class),
                                    examples = @ExampleObject(value = "{\"name\":\"North Branch\"}"))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch updated",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = BranchResponse.class),
                                                    examples = @ExampleObject(value = "{\"id\":1,\"name\":\"North Branch\",\"franchiseId\":1}"))),
                                    @ApiResponse(responseCode = "404", description = "Branch not found",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(value = "{\"code\":\"BRANCH_NOT_FOUND\",\"message\":\"Branch not found\"}")))
                            }))
    })
    public RouterFunction<ServerResponse> branchRoutes(BranchHandler handler) {
        return route(POST("/api/branches"), handler::createBranch)
                .andRoute(PATCH("/api/branches/{branchId}/name"),
                        handler::updateBranchName);
    }
}
