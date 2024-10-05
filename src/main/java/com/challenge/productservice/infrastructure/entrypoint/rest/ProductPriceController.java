package com.challenge.productservice.infrastructure.entrypoint.rest;

import com.challenge.productservice.application.getproductprice.GetProductPriceRequest;
import com.challenge.productservice.application.getproductprice.GetProductPriceResponse;
import com.challenge.productservice.application.getproductprice.GetProductPriceResponse.ProductPriceNotFound;
import com.challenge.productservice.application.getproductprice.GetProductPriceResponse.Successful;
import com.challenge.productservice.application.getproductprice.GetProductPriceUseCase;
import com.challenge.productservice.domain.productprice.BrandId;
import com.challenge.productservice.domain.productprice.ProductId;
import com.challenge.productservice.infrastructure.entrypoint.rest.response.Problem;
import com.challenge.productservice.infrastructure.entrypoint.rest.response.ProductPriceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/prices")
@Tag(
    name = "Product Price",
    description = "API for managing and retrieving product prices based on search criteria."
)
public class ProductPriceController {

    private final GetProductPriceUseCase getProductPriceUseCase;

    public ProductPriceController(GetProductPriceUseCase getProductPriceUseCase) {
        this.getProductPriceUseCase = getProductPriceUseCase;
    }
    @Operation(
            summary = "Get a product price",
            description = "Fetches the price of a product valid at a provided date filtering by product ID and brand ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful response",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductPriceResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Price not found for given parameters.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Problem.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Problem.class)
                            )
                    }
            )
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getProductPrice(
            @Parameter(example = "35455") @RequestParam long productId,
            @Parameter(example = "1") @RequestParam long brandId,
            @Parameter(example = "2020-06-14T15:50:00Z") @RequestParam String validAt
    ) {
        Instant validAtDate = safelyConvertToInstant(validAt)
                .orElseThrow(() -> new RequestParamWithInvalidFormatException("validAt"));
        GetProductPriceRequest request = new GetProductPriceRequest(
                new ProductId(productId),
                new BrandId(brandId),
                validAtDate
        );
        GetProductPriceResponse productPrice = getProductPriceUseCase.execute(request);

        return switch (productPrice) {
            case Successful response -> ResponseEntity.ok(
                    new ProductPriceResponse(
                        response.getProductPrice().productId().value(),
                        response.getProductPrice().brandId().value(),
                        response.getProductPrice().priceList(),
                        response.getProductPrice().startDate(),
                        response.getProductPrice().endDate(),
                        response.getProductPrice().price(),
                        response.getProductPrice().currency().getCurrencyCode()
                )
            );
            case ProductPriceNotFound ignored -> {
                Problem problem = new Problem("Price not found for given parameters.");
                yield ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
            }
        };
    }

    private Optional<Instant> safelyConvertToInstant(String instantString) {
        try {
            return Optional.of(Instant.parse(instantString));
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }

}
