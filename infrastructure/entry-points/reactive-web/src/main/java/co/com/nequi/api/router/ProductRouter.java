package co.com.nequi.api.router;

import co.com.nequi.api.dto.request.CreateProductRequest;
import co.com.nequi.api.dto.request.UpdateNameRequest;
import co.com.nequi.api.dto.request.UpdateStockRequest;
import co.com.nequi.api.dto.response.ErrorResponse;
import co.com.nequi.api.dto.response.ProductResponse;
import co.com.nequi.api.handler.ProductHandler;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/products",
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "createProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Add a product to a branch",
                            tags = {"Products"},
                            requestBody = @RequestBody(required = true, content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CreateProductRequest.class),
                                    examples = @ExampleObject(value = "{\"name\":\"Coffee\",\"stock\":50,\"branchId\":1}"))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product created",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ProductResponse.class),
                                                    examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Coffee\",\"stock\":50,\"branchId\":1}"))),
                                    @ApiResponse(responseCode = "404", description = "Branch not found",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(value = "{\"code\":\"BRANCH_NOT_FOUND\",\"message\":\"Branch not found\"}")))
                            })),
            @RouterOperation(
                    path = "/api/products/{productId}",
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Delete a product from a branch",
                            tags = {"Products"},
                            parameters = @Parameter(name = "productId", in = ParameterIn.PATH, required = true,
                                    description = "Product id", example = "1"),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product deleted"),
                                    @ApiResponse(responseCode = "404", description = "Product not found",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(value = "{\"code\":\"PRODUCT_NOT_FOUND\",\"message\":\"Product not found\"}")))
                            })),
            @RouterOperation(
                    path = "/api/products/{productId}/stock",
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProductStock",
                    operation = @Operation(
                            operationId = "updateProductStock",
                            summary = "Update the stock of a product",
                            tags = {"Products"},
                            parameters = @Parameter(name = "productId", in = ParameterIn.PATH, required = true,
                                    description = "Product id", example = "1"),
                            requestBody = @RequestBody(required = true, content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UpdateStockRequest.class),
                                    examples = @ExampleObject(value = "{\"stock\":120}"))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Stock updated",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ProductResponse.class),
                                                    examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Coffee\",\"stock\":120,\"branchId\":1}"))),
                                    @ApiResponse(responseCode = "404", description = "Product not found",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(value = "{\"code\":\"PRODUCT_NOT_FOUND\",\"message\":\"Product not found\"}")))
                            })),
            @RouterOperation(
                    path = "/api/products/{productId}/name",
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProductName",
                    operation = @Operation(
                            operationId = "updateProductName",
                            summary = "Update the name of a product",
                            tags = {"Products"},
                            parameters = @Parameter(name = "productId", in = ParameterIn.PATH, required = true,
                                    description = "Product id", example = "1"),
                            requestBody = @RequestBody(required = true, content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UpdateNameRequest.class),
                                    examples = @ExampleObject(value = "{\"name\":\"Premium Coffee\"}"))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Name updated",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ProductResponse.class),
                                                    examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Premium Coffee\",\"stock\":120,\"branchId\":1}"))),
                                    @ApiResponse(responseCode = "404", description = "Product not found",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = ErrorResponse.class),
                                                    examples = @ExampleObject(value = "{\"code\":\"PRODUCT_NOT_FOUND\",\"message\":\"Product not found\"}")))
                            }))
    })
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return route(POST("/api/products"), handler::createProduct)
                .andRoute(DELETE("/api/products/{productId}"), handler::deleteProduct)
                .andRoute(PATCH("/api/products/{productId}/stock"), handler::updateProductStock)
                .andRoute(PATCH("/api/products/{productId}/name"), handler::updateProductName);
    }
}
