package com.challenge.productservice.infrastructure.entrypoint.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductPriceResponse(
        @Schema(
            description = "Brand ID",
            example = "1"
        )
        long brandId,

        @Schema(
            description = "Product ID",
            example = "1"
        )
        long productId,

        @Schema(
            description = "Price list ID",
            example = "1"
        )
        int priceList,

        @Schema(
            description = "Price start date",
            example = "2024-10-04T11:14:29.070Z"
        )
        LocalDateTime startDate,

        @Schema(
            description = "Price end date",
            example = "2024-12-04T11:14:29.070Z"
        )
        LocalDateTime endDate,

        @Schema(
            description = "Price amount",
            example = "25.50"
        )
        BigDecimal price,

        @Schema(
            description = "Price currency",
            example = "EUR"
        )
        String currency
) {}
