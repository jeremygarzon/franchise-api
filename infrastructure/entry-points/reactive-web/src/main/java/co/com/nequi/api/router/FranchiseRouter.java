package co.com.nequi.api.router;

import co.com.nequi.api.dto.request.CreateFranchiseRequest;
import co.com.nequi.api.dto.request.UpdateNameRequest;
import co.com.nequi.api.dto.response.ErrorResponse;
import co.com.nequi.api.dto.response.FranchiseResponse;
import co.com.nequi.api.dto.response.ProductResponse;
import co.com.nequi.api.handler.FranchiseHandler;
import io.swagger.v3.oas.annotations.Operation;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/franchises",
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a new franchise",
                            tags = {"Franchises"},
                            requestBody = @RequestBody(required = true, content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CreateFranchiseRequest.class),
                                    examples = @ExampleObject(name = "createFranchise",
                                            value = "{\"name\":\"Nequi Franchise\"}"))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franchise created",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = FranchiseResponse.class),
                                                    examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Nequi Franchise\"}"))),
                                    @ApiResponse(responseCode = "400", description = "Invalid request body",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(value = "{\"code\":\"FRANCHISE_NAME_REQUIRED\",\"message\":\"Franchise name is required\"}")))
                            })),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/name",
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateFranchiseName",
                    operation = @Operation(
                            operationId = "updateFranchiseName",
                            summary = "Update the name of a franchise",
                            tags = {"Franchises"},
                            parameters = @io.swagger.v3.oas.annotations.Parameter(
                                    name = "franchiseId", in = ParameterIn.PATH, required = true,
                                    description = "Franchise id", example = "1"),
                            requestBody = @RequestBody(required = true, content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UpdateNameRequest.class),
                                    examples = @ExampleObject(value = "{\"name\":\"Renamed Franchise\"}"))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise updated",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = FranchiseResponse.class),
                                                    examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Renamed Franchise\"}"))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(value = "{\"code\":\"FRANCHISE_NOT_FOUND\",\"message\":\"Franchise not found\"}")))
                            })),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/highest-stock-products",
                    method = RequestMethod.GET,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "getHighestStockProducts",
                    operation = @Operation(
                            operationId = "getHighestStockProducts",
                            summary = "Get the highest stock product per branch within a franchise",
                            tags = {"Franchises"},
                            parameters = @io.swagger.v3.oas.annotations.Parameter(
                                    name = "franchiseId", in = ParameterIn.PATH, required = true,
                                    description = "Franchise id", example = "1"),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "List of highest stock products",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                                                            schema = @Schema(implementation = ProductResponse.class)),
                                                    examples = @ExampleObject(value = "[{\"id\":1,\"name\":\"Coffee\",\"stock\":120,\"branchId\":1}]"))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(value = "{\"code\":\"FRANCHISE_NOT_FOUND\",\"message\":\"Franchise not found\"}")))
                            }))
    })
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return route(POST("/api/franchises"), handler::createFranchise)
                .andRoute(GET("/api/franchises/{franchiseId}/highest-stock-products"),
                        handler::getHighestStockProducts)
                .andRoute(PATCH("/api/franchises/{franchiseId}/name"),
                        handler::updateFranchiseName);
    }
}
